package com.example.progettoinfonoleggi.service.noleggi;

import com.example.progettoinfonoleggi.dto.NoleggioConOggettoDTO;
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
import com.example.progettoinfonoleggi.service.oggetti.ValoriAttributiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

    @Autowired
    ValoriAttributiService valoriAttributiService;


    public static final String STATO_IN_ATTESA = "IN_ATTESA";
    public static final String STATO_IN_SPEDIZIONE = "IN_SPEDIZIONE";
    public static final String STATO_ATTIVO = "ATTIVO";
    public static final String STATO_INATTIVO = "INATTIVO";

    @Transactional
    public RichiestaNoleggioDTO creaRichiestaNoleggio(RichiestaNoleggioDTO richiestaDTO) {

        //conversione da DTO a entità
        Utenti utente = utentiRepository.findByEmail(richiestaDTO.getEmailUtenteRichiedente())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Oggetti oggetto = oggettiRepository.findById(richiestaDTO.getCodiceOggetto())
                .orElseThrow(() -> new RuntimeException("Oggetto non trovato"));

        //controllo data inizio (almeno 3 giorni dal giorno in cui si sta prenotando)
        LocalDate oggi = LocalDate.now();
        if (richiestaDTO.getDataInizio().isBefore(oggi.plusDays(3))) {
            throw new RuntimeException("La data di inizio deve essere almeno tra 3 giorni da oggi");
        }

        //controllo disponibilità (con buffer 3 giorni)
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
        notifica.setEmailDestinatario(richiesta.getCodiceOggetto().getEmailProprietario());
        notifica.setEmailMittente(richiesta.getEmailRichiedente()); // richiedente
        notifica.setMessaggio("Hai ricevuto una nuova richiesta di noleggio per l'oggetto '"
                + richiesta.getCodiceOggetto().getNome() + "'");
        notifica.setTipo("RICHIESTA_NOLEGGIO");
        notifica.setIdOggetto(richiesta.getCodiceOggetto());
        notifica.setLetto(false);
        notifica.setData(LocalDateTime.now());
        notificheRepository.save(notifica);

        //conversione da entità a DTO per la risposta
        return convertToRichiestaDTO(richiesta);
    }

    public boolean oggettoHaNoleggiAttivi(Integer idOggetto) {
        return noleggiRepository.existsByCodiceOggetto_IdAndStatoNot(idOggetto, "INATTIVO");
    }

    private RichiestaNoleggioDTO convertToRichiestaDTO(RichiesteNoleggi richiesta) {
        RichiestaNoleggioDTO dto = new RichiestaNoleggioDTO();
        dto.setEmailUtenteRichiedente(richiesta.getEmailRichiedente().getEmail());
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

        Spedizioni spedizione = new Spedizioni();
        spedizione.setEmailMittente(richiesta.getCodiceOggetto().getEmailProprietario());
        spedizione.setEmailDestinatario(richiesta.getEmailRichiedente());
        spedizione.setNomeCorriere(nomeCorriere);
        spedizione.setTipoSpedizione("Standard");
        spedizione.setDescrizione("Spedizione oggetto noleggiato");
        spedizione.setStato("IN_PREPARAZIONE");
        spedizione = spedizioneRepository.save(spedizione);

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

        String emailProprietario = richiesta.getCodiceOggetto().getEmailProprietario().getEmail();

        Saldo saldoProprietario = saldoRepository.findByEmailUtente(emailProprietario)
                .orElseThrow(() -> new RuntimeException("Saldo non trovato per l'utente: " + emailProprietario));

        BigDecimal saldoAttuale = saldoProprietario.getSaldo();
        saldoProprietario.setSaldo(saldoAttuale.add(prezzoTotale));

        saldoRepository.save(saldoProprietario);
    }

    public void rifiutaRichiesta(Long idRichiesta) {
        RichiesteNoleggi richiesta = richiestaNoleggioRepository.findById(idRichiesta)
                .orElseThrow(() -> new RuntimeException("Richiesta non trovata"));

        if (!"IN_ATTESA".equals(richiesta.getStato())) {
            throw new RuntimeException("Richiesta non in stato valido per accettazione");
        }

        richiesta.setStato("RIFIUTATA");
        richiestaNoleggioRepository.save(richiesta);
    }


    public List<LocalDate> getGiorniOccupatiConBuffer(Integer codiceOggetto) {
        List<Noleggi> noleggiAttivi = noleggioRepository.findNoleggiAttiviByOggetto(codiceOggetto);
        Set<LocalDate> giorniOccupati = new HashSet<>();

        for (Noleggi noleggio : noleggiAttivi) {
            LocalDate inizio = noleggio.getDataInizio().minusDays(3);
            LocalDate fine = noleggio.getDataFine().plusDays(3);

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

    public List<RichiestaNoleggioDTO> getRichiesteRicevuteDaUtente(String emailDestinatario) {
        List<RichiesteNoleggi> richieste = richiestaNoleggioRepository.findRichiesteByProprietarioAndStato(
                emailDestinatario,
                "IN_ATTESA"
        );
        return richieste.stream()
                .map(this::convertiInDTO)
                .collect(Collectors.toList());
    }

    private RichiestaNoleggioDTO convertiInDTO(RichiesteNoleggi richiesta) {
        RichiestaNoleggioDTO dto = new RichiestaNoleggioDTO();

        dto.setIdNoleggio(Math.toIntExact(richiesta.getCodiceID()));
        dto.setEmailUtenteRichiedente(richiesta.getEmailRichiedente().getEmail());
        dto.setCodiceOggetto(richiesta.getCodiceOggetto().getId());
        dto.setDataInizio(richiesta.getDataInizio());
        dto.setDataFine(richiesta.getDataFine());

        Optional<Oggetti> o =  oggettiRepository.findById(richiesta.getCodiceOggetto().getId());
        if (o.isPresent()) {
            OggettoCompletoDTO oggettoDTO = oggettiService.convertiACompletoDTO(o.get());
            dto.setOggetto(oggettoDTO);
        }
        else {
            dto.setOggetto(null);
        }

        return dto;
    }


    public List<NoleggioConOggettoDTO> getNoleggiAttiviProprietarioConOggetto(String emailProprietario) {
        List<Noleggi> noleggiAttivi = noleggioRepository.findNoleggiAttiviProprietario(emailProprietario);

        return noleggiAttivi.stream().map(noleggio -> {
            NoleggioConOggettoDTO dto = new NoleggioConOggettoDTO();
            dto.setIdNoleggio(noleggio.getCodiceID());
            dto.setDataInizio(noleggio.getDataInizio());
            dto.setEmail(noleggio.getEmailNoleggiatore().getEmail());
            dto.setDataFine(noleggio.getDataFine());
            dto.setStato(noleggio.getStato());

            OggettoCompletoDTO oggettoDTO = oggettiService.convertiACompletoDTO(noleggio.getCodiceOggetto());
            dto.setOggetto(oggettoDTO);

            return dto;
        }).collect(Collectors.toList());
    }

    public List<NoleggioConOggettoDTO> getNoleggiAttiviAcquirente(String emailAcquirente) {
        List<Noleggi> noleggiAttivi = noleggiRepository.findNoleggiAttiviAcquirente(emailAcquirente);

        return noleggiAttivi.stream().map(noleggio ->
        {
            NoleggioConOggettoDTO dto = new NoleggioConOggettoDTO();
            dto.setIdNoleggio(noleggio.getCodiceID());
            dto.setDataInizio(noleggio.getDataInizio());
            dto.setDataFine(noleggio.getDataFine());
            dto.setEmail(noleggio.getEmailNoleggiatore().getEmail());
            dto.setStato(noleggio.getStato());

            OggettoCompletoDTO oggettoDTO = oggettiService.convertiACompletoDTO(noleggio.getCodiceOggetto());
            dto.setOggetto(oggettoDTO);

            return dto;
        }).collect(Collectors.toList());

    }

}
