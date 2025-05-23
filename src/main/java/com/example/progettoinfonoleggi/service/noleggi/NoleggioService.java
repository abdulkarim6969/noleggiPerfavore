package com.example.progettoinfonoleggi.service.noleggi;

import com.example.progettoinfonoleggi.dto.OggettoCompletoDTO;
import com.example.progettoinfonoleggi.dto.RichiestaNoleggioDTO;
import com.example.progettoinfonoleggi.model.noleggi.*;
import com.example.progettoinfonoleggi.model.notifiche.Notifiche;
import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Saldo;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.repository.noleggi.*;
import com.example.progettoinfonoleggi.repository.notifiche.NotificheRepository;
import com.example.progettoinfonoleggi.repository.oggetti.OggettiRepository;
import com.example.progettoinfonoleggi.repository.utenti.SaldoRepository;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import com.example.progettoinfonoleggi.service.oggetti.OggettiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoleggioService {

    @Autowired
    private RichiesteNoleggiRepository richiestaNoleggioRepository;

    @Autowired
    private SaldoRepository saldoRepository;

    @Autowired
    private NoleggiRepository noleggioRepository;

    @Autowired
    private TransazioniRepository transazioneRepository;

    @Autowired
    private SpedizioniRepository spedizioneRepository;

    @Autowired
    private UtentiRepository utentiRepository;

    @Autowired
    private OggettiRepository oggettiRepository;

    @Autowired
    private OggettiService oggettiService;

    @Autowired
    private NotificheRepository notificheRepository;

    @Autowired
    private NoleggiRepository noleggiRepository;

    public static final String STATO_IN_ATTESA = "IN_ATTESA";
    public static final String STATO_IN_SPEDIZIONE = "IN_SPEDIZIONE";
    public static final String STATO_ATTIVO = "ATTIVO";
    public static final String STATO_INATTIVO = "INATTIVO";

    @Transactional
    public RichiestaNoleggioDTO creaRichiestaNoleggio(RichiestaNoleggioDTO richiestaDTO) {

        // Conversione da DTO a entità
        Utenti utente = utentiRepository.findByEmail(richiestaDTO.getEmailUtente())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Oggetti oggetto = oggettiRepository.findById(richiestaDTO.getCodiceOggetto())
                .orElseThrow(() -> new RuntimeException("Oggetto non trovato"));

        // Controllo data inizio (almeno 3 giorni nel futuro)
        LocalDate oggi = LocalDate.now();
        if (richiestaDTO.getDataInizio().isBefore(oggi.plusDays(3))) {
            throw new RuntimeException("La data di inizio deve essere almeno tra 3 giorni da oggi");
        }

        // Controllo disponibilità (con buffer 3 giorni)
        LocalDate inizioBuffer = richiestaDTO.getDataInizio().minusDays(3);
        LocalDate fineBuffer = richiestaDTO.getDataFine().plusDays(3);
        int countNoleggi = noleggioRepository.countNoleggiAttiviSovrapposti(oggetto.getId(), inizioBuffer, fineBuffer);
        if (countNoleggi > 0) {
            throw new RuntimeException("Oggetto non disponibile nel periodo selezionato");
        }

        RichiesteNoleggi richiesta = new RichiesteNoleggi();
        richiesta.setEmailRichiedente(utente);
        richiesta.setCodiceOggetto(oggetto);
        richiesta.setDataInizio(richiestaDTO.getDataInizio());
        richiesta.setDataFine(richiestaDTO.getDataFine());
        richiesta.setStato(STATO_IN_ATTESA);
        richiesta = richiestaNoleggioRepository.save(richiesta);


        Notifiche notifica = new Notifiche();
        notifica.setEmailDestinatario(richiesta.getCodiceOggetto().getEmailProprietario()); // proprietario
        notifica.setEmailMittente(richiesta.getEmailRichiedente()); // richiedente
        notifica.setMessaggio("Hai ricevuto una nuova richiesta di noleggio per l'oggetto '"
                + richiesta.getCodiceOggetto().getNome() + "'");
        notifica.setTipo("RICHIESTA_NOLEGGIO");
        notifica.setIdOggetto(richiesta.getCodiceOggetto());
        notifica.setLetto(false);
        notifica.setData(LocalDateTime.now());

        notificheRepository.save(notifica);


        // Conversione da entità a DTO per la risposta
        return convertToRichiestaDTO(richiesta);
    }

    private RichiestaNoleggioDTO convertToRichiestaDTO(RichiesteNoleggi richiesta) {
        RichiestaNoleggioDTO dto = new RichiestaNoleggioDTO();
        dto.setEmailUtente(richiesta.getEmailRichiedente().getEmail());
        dto.setCodiceOggetto(richiesta.getCodiceOggetto().getId());
        dto.setDataInizio(richiesta.getDataInizio());
        dto.setDataFine(richiesta.getDataFine());
        return dto;
    }

    @Transactional
    public void accettaRichiesta(Long idRichiesta) {
        RichiesteNoleggi richiesta = richiestaNoleggioRepository.findById(idRichiesta)
                .orElseThrow(() -> new RuntimeException("Richiesta non trovata"));

        if (!"IN_ATTESA".equals(richiesta.getStato())) {
            throw new RuntimeException("Richiesta non in stato valido per accettazione");
        }

        richiesta.setStato("ACCETTATA");
        richiestaNoleggioRepository.save(richiesta);

        String paymentIdMittente = "PAYM-" + UUID.randomUUID();
        String paymentIdDestinatario = "PAYD-" + UUID.randomUUID();
        String nomeCorriere = "Corriere Fittizio";

        // Creo transazione
        Transazioni transazione = new Transazioni();
        transazione.setEmailMittente(richiesta.getEmailRichiedente());
        transazione.setEmailDestinatario(richiesta.getCodiceOggetto().getEmailProprietario());
        transazione.setCodiceOggetto(richiesta.getCodiceOggetto());
        transazione.setTipo("NOLEGGIO");
        transazione.setDescrizione("Pagamento noleggio oggetto " + richiesta.getCodiceOggetto().getNome());
        transazione.setStatoTransazione("COMPLETATA");
        transazione.setPaymentIdMittente(paymentIdMittente);
        transazione.setPaymentIdDestinatario(paymentIdDestinatario);
        transazione = transazioneRepository.save(transazione);

        // Creo spedizione
        Spedizioni spedizione = new Spedizioni();
        spedizione.setEmailMittente(richiesta.getCodiceOggetto().getEmailProprietario());
        spedizione.setEmailDestinatario(richiesta.getEmailRichiedente());
        spedizione.setNomeCorriere(nomeCorriere);
        spedizione.setTipoSpedizione("Standard");
        spedizione.setDescrizione("Spedizione oggetto noleggiato");
        spedizione.setStato("IN_PREPARAZIONE");
        spedizione = spedizioneRepository.save(spedizione);

        // Creo noleggio
        Noleggi noleggio = new Noleggi();
        noleggio.setCodiceOggetto(richiesta.getCodiceOggetto());
        noleggio.setEmailNoleggiatore(richiesta.getEmailRichiedente());
        noleggio.setEmailProprietario(richiesta.getCodiceOggetto().getEmailProprietario());
        noleggio.setCodiceTransazione(transazione);
        noleggio.setCodiceSpedizione(spedizione);
        noleggio.setDataInizio(richiesta.getDataInizio());
        noleggio.setDataFine(richiesta.getDataFine());
        noleggio.setStato("ATTIVO");

        long giorni = ChronoUnit.DAYS.between(richiesta.getDataInizio(), richiesta.getDataFine()) + 1;
        BigDecimal prezzoGiornaliero = richiesta.getCodiceOggetto().getPrezzoGiornaliero();
        if (prezzoGiornaliero == null) {
            throw new RuntimeException("Prezzo giornaliero non definito per l'oggetto");
        }

        BigDecimal prezzoTotale = prezzoGiornaliero.multiply(BigDecimal.valueOf(giorni));
        noleggio.setPrezzoTotale(prezzoTotale);

        noleggioRepository.save(noleggio);

        // AGGIORNAMENTO SALDO DEL PROPRIETARIO
        String emailProprietario = richiesta.getCodiceOggetto().getEmailProprietario().getEmail();

        Saldo saldoProprietario = saldoRepository.findByEmailUtente(emailProprietario)
                .orElseThrow(() -> new RuntimeException("Saldo non trovato per l'utente: " + emailProprietario));

        BigDecimal saldoAttuale = saldoProprietario.getSaldo();
        saldoProprietario.setSaldo(saldoAttuale.add(prezzoTotale));

        saldoRepository.save(saldoProprietario);
    }


    public List<LocalDate> getGiorniOccupatiConBuffer(Integer codiceOggetto) {
        List<Noleggi> noleggiAttivi = noleggioRepository.findNoleggiAttiviByOggetto(codiceOggetto);
        Set<LocalDate> giorniOccupati = new HashSet<>();

        for (Noleggi noleggio : noleggiAttivi) {
            // Estendi intervallo con buffer 3 giorni prima e dopo
            LocalDate inizio = noleggio.getDataInizio().minusDays(3);
            LocalDate fine = noleggio.getDataFine().plusDays(3);

            // Aggiungi tutti i giorni dell’intervallo al set
            for (LocalDate data = inizio; !data.isAfter(fine); data = data.plusDays(1)) {
                giorniOccupati.add(data);
            }
        }

        LocalDate oggi = LocalDate.now();

        return giorniOccupati.stream()
                .sorted((d1, d2) -> {
                    long diff1 = Math.abs(d1.toEpochDay() - oggi.toEpochDay());
                    long diff2 = Math.abs(d2.toEpochDay() - oggi.toEpochDay());
                    return Long.compare(diff1, diff2);
                })
                .collect(Collectors.toList());

    }

    @Scheduled(cron = "0 20 16 * * ?") // ogni giorno a mezzanotte
    @Transactional
    public void aggiornaStatoNoleggi() {
        LocalDate oggi = LocalDate.now();
        List<Noleggi> tuttiNoleggi = noleggioRepository.findAll();

        for (Noleggi noleggio : tuttiNoleggi) {
            LocalDate inizio = noleggio.getDataInizio();
            LocalDate fine = noleggio.getDataFine();

            LocalDate inizioSpedizione = inizio.minusDays(3);
            LocalDate fineSpedizione = fine.plusDays(3);

            String nuovoStato;

            if (!oggi.isBefore(inizioSpedizione) && oggi.isBefore(inizio)) {
                nuovoStato = STATO_IN_SPEDIZIONE;
            } else if (!oggi.isBefore(inizio) && !oggi.isAfter(fine)) {
                nuovoStato = STATO_ATTIVO;
            } else if (oggi.isAfter(fine) && !oggi.isAfter(fineSpedizione)) {
                nuovoStato = STATO_IN_SPEDIZIONE;
            } else {
                nuovoStato = STATO_INATTIVO;
            }

            if (!noleggio.getStato().equals(nuovoStato)) {
                noleggio.setStato(nuovoStato);
                noleggioRepository.save(noleggio);
            }
        }
    }

    public List<OggettoCompletoDTO> getNoleggiAttiviProprietario(String emailProprietario) {
        return noleggiRepository.findNoleggiAttiviProprietario(emailProprietario)
                .stream()
                .map(n -> oggettiService.getOggettoById(n.getCodiceOggetto().getId()))
                .collect(Collectors.toList());
    }

    public List<OggettoCompletoDTO> getNoleggiAttiviAcquirente(String emailAcquirente) {
        return noleggiRepository.findNoleggiAttiviAcquirente(emailAcquirente)
                .stream()
                .map(n -> oggettiService.getOggettoById(n.getCodiceOggetto().getId()))
                .collect(Collectors.toList());
    }

}
