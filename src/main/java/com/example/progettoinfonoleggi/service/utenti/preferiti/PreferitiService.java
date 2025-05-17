package com.example.progettoinfonoleggi.service.utenti.preferiti;

import com.example.progettoinfonoleggi.dto.PreferitiDTO;
import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.model.utenti.preferiti.Preferiti;
import com.example.progettoinfonoleggi.repository.oggetti.OggettiRepository;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import com.example.progettoinfonoleggi.repository.utenti.preferiti.PreferitiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        preferito.setEmailUtente(utente.getEmail());
        preferito.setIdOggetto(oggetto.getId());

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
}
