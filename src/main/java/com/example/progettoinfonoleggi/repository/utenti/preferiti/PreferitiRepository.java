package com.example.progettoinfonoleggi.repository.utenti.preferiti;

import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.model.utenti.preferiti.Preferiti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PreferitiRepository extends JpaRepository<Preferiti, Integer> {

    Optional<Preferiti> findByEmailUtenteAndOggetto(Utenti utente, Oggetti oggetto);

    @Query("SELECT p.oggetto FROM Preferiti p WHERE p.emailUtente.email = :emailUtente")
    List<Oggetti> findOggettiPreferitiByEmailUtente(@Param("emailUtente") String emailUtente);

    boolean existsByEmailUtenteAndOggetto(Utenti utente, Oggetti oggetto);

    void deleteByOggetto_Id(Integer idOggetto);
}
