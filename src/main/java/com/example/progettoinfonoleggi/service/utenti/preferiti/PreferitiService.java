package com.example.progettoinfonoleggi.service.utenti.preferiti;

import com.example.progettoinfonoleggi.dto.OggettoDTO;
import com.example.progettoinfonoleggi.dto.PreferitiDTO;
import com.example.progettoinfonoleggi.model.notifiche.Notifiche;
import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.model.utenti.preferiti.Preferiti;
import com.example.progettoinfonoleggi.repository.notifiche.NotificheRepository;
import com.example.progettoinfonoleggi.repository.oggetti.OggettiRepository;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import com.example.progettoinfonoleggi.repository.utenti.preferiti.PreferitiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PreferitiService {
    @Autowired
    private PreferitiRepository preferitiRepository;

    @Autowired
    private UtentiRepository utentiRepository;

    @Autowired
    private OggettiRepository oggettiRepository;

    @Autowired
    private NotificheRepository notificheRepository;

    public void aggiungiPreferito(PreferitiDTO preferito) {

        Utenti utente = utentiRepository.findByEmail(preferito.getEmailUtente())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        Oggetti oggetto = oggettiRepository.findById(preferito.getIdOggetto())
                .orElseThrow(() -> new RuntimeException("Oggetto non trovato"));

        boolean exists = preferitiRepository.existsByEmailUtenteAndIdOggetto(utente, oggetto);
        if (exists) {
            throw new RuntimeException("Articolo già presente nei preferiti");
        }

        Utenti proprietario = oggetto.getEmailProprietario();

        if (!utente.getEmail().equals(oggetto.getEmailProprietario().getEmail())) {
            Notifiche notifica = new Notifiche();
            notifica.setEmailDestinatario(proprietario);
            notifica.setEmailMittente(utente);
            notifica.setMessaggio(utente.getNome() + " ha aggiunto il tuo oggetto '" + oggetto.getNome() + "' ai preferiti");
            notifica.setTipo("PREFERITO_AGGIUNTO");
            notifica.setIdOggetto(oggetto);
            notificheRepository.save(notifica);
        }

        Preferiti NEWpreferito = new Preferiti();
        NEWpreferito.setEmailUtente(utente);
        NEWpreferito.setIdOggetto(oggetto);

        preferitiRepository.save(NEWpreferito);


    }

    public void rimuoviPreferito(PreferitiDTO preferito) {
        Utenti utente = utentiRepository.findByEmail(preferito.getEmailUtente())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        Oggetti oggetto = oggettiRepository.findById(preferito.getIdOggetto())
                .orElseThrow(() -> new RuntimeException("Oggetto non trovato"));

        Preferiti newpreferito = preferitiRepository.findByEmailUtenteAndIdOggetto(utente, oggetto)
                .orElseThrow(() -> new RuntimeException("Preferito non trovato"));

        preferitiRepository.delete(newpreferito);
    }

    public List<OggettoDTO> getOggettiPreferitiByEmailUtente(String emailUtente) {
        List<Oggetti> preferiti = preferitiRepository.findOggettiPreferitiByEmailUtente(emailUtente);

        return preferiti.stream().map(oggetto -> {
            String immagineBase64 = Base64.getEncoder().encodeToString(oggetto.getImmagine());
            return new OggettoDTO(
                    oggetto.getDataCreazione(),
                    oggetto.getDescrizione(),
                    oggetto.getEmailProprietario().getEmail(),
                    oggetto.getId(),
                    immagineBase64,
                    oggetto.getDataUltimaModifica(),
                    oggetto.getNome(),
                    oggetto.getNomeCategoria().getNome(), // o `.toString()` se è enum
                    oggetto.getPrezzoGiornaliero()
            );
        }).toList();
    }


}
