package com.example.progettoinfonoleggi.repository.oggetti.categorie;

import com.example.progettoinfonoleggi.model.oggetti.categorie.ValoriAttributi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ValoriAttributiRepository extends JpaRepository<ValoriAttributi, Integer> {
    List<ValoriAttributi> findByOggetto_Id(Integer idOggetto);
    Optional<ValoriAttributi> findByOggetto_IdAndAttributo_Id(Integer idOggetto, Integer idAttributo);

}
