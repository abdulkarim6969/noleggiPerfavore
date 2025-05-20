package com.example.progettoinfonoleggi.service.utenti.preferiti;

import com.example.progettoinfonoleggi.dto.OggettoDTO;
import com.example.progettoinfonoleggi.dto.PreferitiDTO;
import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.model.utenti.preferiti.Preferiti;
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


    public void aggiungiPreferito(PreferitiDTO preferito) {

        Utenti utente = utentiRepository.findByEmail(preferito.getEmailUtente())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        Oggetti oggetto = oggettiRepository.findById(preferito.getIdOggetto())
                .orElseThrow(() -> new RuntimeException("Oggetto non trovato"));

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
