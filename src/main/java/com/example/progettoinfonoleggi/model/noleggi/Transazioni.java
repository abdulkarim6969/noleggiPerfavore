package com.example.progettoinfonoleggi.model.noleggi;

import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transazioni")
public class Transazioni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codiceID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailMittente", referencedColumnName = "email")
    private Utenti emailMittente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailDestinatario", referencedColumnName = "email")
    private Utenti emailDestinatario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "codiceOggetto", referencedColumnName = "codiceID")
    private Oggetti codiceOggetto;

    @Column(length = 50)
    private String tipo;

    @Column(length = 255)
    private String descrizione;

    @Column(nullable = false)
    private LocalDateTime dataOra = LocalDateTime.now();

    @Column(length = 100, name = "payment_id_mittente")
    private String paymentIdMittente;

    @Column(length = 100, name = "payment_id_destinatario")
    private String paymentIdDestinatario;

    @Column(length = 20)
    private String statoTransazione;
}
