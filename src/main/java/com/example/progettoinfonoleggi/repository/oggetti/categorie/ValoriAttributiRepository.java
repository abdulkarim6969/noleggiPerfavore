package com.example.progettoinfonoleggi.repository.oggetti.categorie;

import com.example.progettoinfonoleggi.model.oggetti.categorie.ValoriAttributi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ValoriAttributiRepository extends JpaRepository<ValoriAttributi, Long> {
    List<ValoriAttributi> findByOggetto_Id(Integer idOggetto);
}
