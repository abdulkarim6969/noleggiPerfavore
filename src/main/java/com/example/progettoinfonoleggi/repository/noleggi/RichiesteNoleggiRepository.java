package com.example.progettoinfonoleggi.repository.noleggi;

import com.example.progettoinfonoleggi.model.noleggi.RichiesteNoleggi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RichiesteNoleggiRepository extends JpaRepository<RichiesteNoleggi, Long> {

    // Trova richieste attive che si sovrappongono a un intervallo per un oggetto
    @Query("SELECT r FROM RichiesteNoleggi r WHERE r.codiceOggetto.id = :codiceOggetto AND r.stato = 'ACCETTATA' " +
            "AND r.dataInizio <= :dataFine AND r.dataFine >= :dataInizio")
    List<RichiesteNoleggi> findRichiesteAttiveSovrapposte(@Param("codiceOggetto") Integer codiceOggetto,
                                                           @Param("dataInizio") LocalDate dataInizio,
                                                           @Param("dataFine") LocalDate dataFine);

    List<RichiesteNoleggi> findByCodiceOggetto_EmailProprietario_Email(String emailProprietario);
}

