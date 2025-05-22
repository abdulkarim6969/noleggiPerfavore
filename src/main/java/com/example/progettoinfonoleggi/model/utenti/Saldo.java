package com.example.progettoinfonoleggi.model.utenti;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "saldoUtenti")
public class Saldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String emailUtente;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emailUtente", referencedColumnName = "email", insertable = false, updatable = false)
    private Utenti utente;
}
