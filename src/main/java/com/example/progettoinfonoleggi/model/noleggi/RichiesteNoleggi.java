package com.example.progettoinfonoleggi.model.noleggi;

import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "richiesteNoleggi")
public class RichiesteNoleggi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codiceID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "codiceOggetto", referencedColumnName = "codiceID")
    private Oggetti codiceOggetto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailRichiedente", referencedColumnName = "email")
    private Utenti emailRichiedente;

    @Column(nullable = false)
    private LocalDate dataInizio;

    @Column(nullable = false)
    private LocalDate dataFine;

    @Column(nullable = false, length = 20)
    private String stato; // es. IN_ATTESA, ACCETTATA, RIFIUTATA

    @Column(nullable = false)
    private LocalDateTime dataRichiesta = LocalDateTime.now();
}
