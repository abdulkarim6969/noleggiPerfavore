package com.example.progettoinfonoleggi.repository.oggetti.categorie;

import com.example.progettoinfonoleggi.model.oggetti.categorie.CategoriePreferite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriePreferiteRepository extends JpaRepository<CategoriePreferite, Long> {
    Optional<CategoriePreferite> findByCodiceID(Long codiceID);
}
