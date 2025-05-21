package com.example.progettoinfonoleggi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class AggiungiValoriAttributiDTO {
    private Integer idOggetto;
    private List<ValoreAttributoDTO> attributi;
}

