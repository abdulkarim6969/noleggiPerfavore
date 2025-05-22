package com.example.progettoinfonoleggi.repository.noleggi;

import com.example.progettoinfonoleggi.model.noleggi.Noleggi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface NoleggiRepository extends JpaRepository<Noleggi, Long> {

    // Conta noleggi attivi che si sovrappongono a un intervallo per un oggetto
    @Query("SELECT COUNT(n) FROM Noleggi n WHERE n.codiceOggetto.id = :codiceOggetto AND n.stato = 'ATTIVO' " +
            "AND n.dataInizio <= :dataFine AND n.dataFine >= :dataInizio")
    int countNoleggiAttiviSovrapposti(@Param("codiceOggetto") Integer codiceOggetto,
                                      @Param("dataInizio") LocalDate dataInizio,
                                      @Param("dataFine") LocalDate dataFine);

    @Query("SELECT n FROM Noleggi n WHERE n.codiceOggetto.id = :codiceOggetto AND n.stato = 'ATTIVO'")
    List<Noleggi> findNoleggiAttiviByOggetto(@Param("codiceOggetto") Integer codiceOggetto);
}
