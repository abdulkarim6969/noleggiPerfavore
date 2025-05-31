package com.example.progettoinfonoleggi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    public PreferitiDTO(@Size(max = 255) String email, Integer id) {
    }

    public PreferitiDTO() {

    }
}
