package com.example.progettoinfonoleggi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequestDTO {
    @Size(max = 255)
    @NotNull
    private String email;

    @Size(max = 20)
    @NotNull
    private String nomeUtente;

    @Size(max = 20)
    @NotNull
    private String nome;

    @Size(max = 20)
    @NotNull
    private String cognome;

    @Size(max = 100)
    @NotNull
    private String indirizzo;

    @Size(max = 5)
    @NotNull
    private String cap;

    @Size(max = 30)
    @NotNull
    private String citta;

    @Size(max = 10)
    @NotNull
    private String telefono;

    @NotNull
    private LocalDate dataNascita;

    @Size(max = 64)
    @NotNull
    private String password;

}
