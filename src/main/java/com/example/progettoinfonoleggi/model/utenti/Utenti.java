package com.example.progettoinfonoleggi.model.utenti;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "utenti")

public class Utenti {
    @Id
    @Size(max = 255)
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 20)
    @NotNull
    @Column(name = "nomeUtente", nullable = false, length = 20)
    private String nomeUtente;

    @Size(max = 20)
    @NotNull
    @Column(name = "nome", nullable = false, length = 20)
    private String nome;

    @Size(max = 20)
    @NotNull
    @Column(name = "cognome", nullable = false, length = 20)
    private String cognome;

    @Size(max = 100)
    @NotNull
    @Column(name = "indirizzo", nullable = false, length = 100)
    private String indirizzo;

    @Size(max = 5)
    @NotNull
    @Column(name = "CAP", nullable = false, length = 5)
    private String cap;

    @Size(max = 30)
    @NotNull
    @Column(name = "citta", nullable = false, length = 30)
    private String citta;

    @Size(max = 10)
    @NotNull
    @Column(name = "telefono", nullable = false, length = 10)
    private String telefono;

    @NotNull
    @Column(name = "dataNascita", nullable = false)
    private LocalDate dataNascita;

    @Size(max = 64)
    @NotNull
    @Column(name = "password", nullable = false, length = 64)
    private String password;


}