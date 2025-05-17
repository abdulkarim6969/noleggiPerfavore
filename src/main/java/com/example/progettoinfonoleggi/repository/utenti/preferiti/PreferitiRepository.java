package com.example.progettoinfonoleggi.repository.utenti.preferiti;

import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.model.utenti.preferiti.Preferiti;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferitiRepository extends JpaRepository<Preferiti, Integer> {
    Optional<Preferiti> findByEmailUtenteAndIdOggetto(Utenti utente, Oggetti oggetto);
}
