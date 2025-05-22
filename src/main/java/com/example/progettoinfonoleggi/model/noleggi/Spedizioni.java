package com.example.progettoinfonoleggi.model.noleggi;

import com.example.progettoinfonoleggi.model.utenti.Utenti;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "spedizioni")
public class Spedizioni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codiceID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailMittente", referencedColumnName = "email")
    private Utenti emailMittente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailDestinatario", referencedColumnName = "email")
    private Utenti emailDestinatario;

    @Column(length = 100)
    private String nomeCorriere;

    @Column(length = 50)
    private String tipoSpedizione;

    @Column(length = 255)
    private String descrizione;

    @Column(length = 20)
    private String stato;

    @Column(nullable = false)
    private LocalDateTime dataCreazione = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime dataUltimaModifica = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        dataUltimaModifica = LocalDateTime.now();
    }
}
