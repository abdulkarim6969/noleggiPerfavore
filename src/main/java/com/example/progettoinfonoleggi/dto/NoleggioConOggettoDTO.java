package com.example.progettoinfonoleggi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NoleggioConOggettoDTO {
    private Long idNoleggio;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String stato;

    private OggettoCompletoDTO oggetto;
}
