package com.example.progettoinfonoleggi.model.noleggi;

import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "noleggi")
public class Noleggi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codiceID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "codiceOggetto", referencedColumnName = "codiceID")
    private Oggetti codiceOggetto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailNoleggiatore", referencedColumnName = "email")
    private Utenti emailNoleggiatore;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailProprietario", referencedColumnName = "email")
    private Utenti emailProprietario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "codiceTransazione", referencedColumnName = "codiceID")
    private Transazioni codiceTransazione;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "codiceSpedizione", referencedColumnName = "codiceID")
    private Spedizioni codiceSpedizione;

    @Column(nullable = false)
    private LocalDate dataInizio;

    @Column(nullable = false)
    private LocalDate dataFine;

    @Column(length = 20, nullable = false)
    private String stato; // es. ATTIVO, CONCLUSO, ANNULLATO

    @Column(precision = 10, scale = 2)
    private BigDecimal prezzoTotale;
}

