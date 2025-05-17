package com.example.progettoinfonoleggi.repository.oggetti;

import com.example.progettoinfonoleggi.model.oggetti.CategorieOggetti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieOggettiRepository extends JpaRepository<CategorieOggetti, String> {
    Optional<CategorieOggetti> findByNome(String nome);
}
