package com.example.progettoinfonoleggi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreferitiDTO {
    @NotNull
    @Email
    private String emailUtente;

    @NotNull
    private Integer idOggetto;
}
