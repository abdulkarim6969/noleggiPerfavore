package com.example.progettoinfonoleggi.repository.oggetti.categorie;

import com.example.progettoinfonoleggi.model.oggetti.categorie.CategorieOggetti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieOggettiRepository extends JpaRepository<CategorieOggetti, Integer> {
    Optional<CategorieOggetti> findByNome(String nome);
}
