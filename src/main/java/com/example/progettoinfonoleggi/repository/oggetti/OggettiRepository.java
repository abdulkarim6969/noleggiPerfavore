package com.example.progettoinfonoleggi.repository.oggetti;

import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OggettiRepository extends JpaRepository<Oggetti, Integer> {
    List<Oggetti> findByEmailProprietario_Email(@Size(max = 255) String emailProprietarioEmail);

    List<Oggetti> findByNomeCategoria_Nome(String nomeCategoria);

    List<Oggetti> findByNomeContainingIgnoreCase(String nome);

    List<Oggetti> findByEmailProprietario_EmailNot(String emailProprietario);
}
