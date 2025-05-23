package com.example.progettoinfonoleggi.repository.utenti;

import com.example.progettoinfonoleggi.model.utenti.Saldo;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SaldoRepository extends JpaRepository<Saldo, Double> {
    Optional<Saldo> findByEmailUtente(String emailUtente);

}
