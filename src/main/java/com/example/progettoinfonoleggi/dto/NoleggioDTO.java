package com.example.progettoinfonoleggi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NoleggioDTO {
    private Long id;
    private Integer codiceOggetto;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String stato;
    private OggettoDTO oggetto;
}
