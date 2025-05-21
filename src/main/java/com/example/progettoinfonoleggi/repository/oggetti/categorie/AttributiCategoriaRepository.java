package com.example.progettoinfonoleggi.repository.oggetti.categorie;

import com.example.progettoinfonoleggi.model.oggetti.categorie.AttributiCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributiCategoriaRepository extends JpaRepository<AttributiCategoria, Long> {
    List<AttributiCategoria> findByCategoria_Nome(String nomeCategoria);
}
