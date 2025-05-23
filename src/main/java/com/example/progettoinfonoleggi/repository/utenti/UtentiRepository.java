package com.example.progettoinfonoleggi.repository.utenti;

import com.example.progettoinfonoleggi.model.utenti.Utenti;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtentiRepository extends JpaRepository<Utenti, String> {
    Utenti findByEmailAndPassword(String email, String password);

    Optional<Utenti> findByEmail(String email);

    boolean existsByEmail(String email);

    String email(@Size(max = 255) String email);
}
