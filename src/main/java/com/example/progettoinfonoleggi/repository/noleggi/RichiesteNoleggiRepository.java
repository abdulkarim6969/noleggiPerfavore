package com.example.progettoinfonoleggi.repository.noleggi;

import com.example.progettoinfonoleggi.model.noleggi.RichiesteNoleggi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RichiesteNoleggiRepository extends JpaRepository<RichiesteNoleggi, Long> {

    //trova richieste attive che si sovrappongono a un intervallo per un oggetto
    @Query("SELECT r FROM RichiesteNoleggi r WHERE r.codiceOggetto.id = :codiceOggetto AND r.stato = 'ACCETTATA' " +
            "AND r.dataInizio <= :dataFine AND r.dataFine >= :dataInizio")
    List<RichiesteNoleggi> findRichiesteAttiveSovrapposte(@Param("codiceOggetto") Integer codiceOggetto,
                                                           @Param("dataInizio") LocalDate dataInizio,
                                                           @Param("dataFine") LocalDate dataFine);

    @Query("SELECT r FROM RichiesteNoleggi r " +
            "WHERE r.codiceOggetto.emailProprietario.email = :emailProprietario " +
            "AND r.stato = :stato")
    List<RichiesteNoleggi> findRichiesteByProprietarioAndStato(
            @Param("emailProprietario") String emailProprietario,
            @Param("stato") String stato
    );
}

