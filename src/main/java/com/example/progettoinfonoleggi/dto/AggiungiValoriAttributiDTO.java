package com.example.progettoinfonoleggi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AggiungiValoriAttributiDTO {
    private Integer idOggetto;
    private List<ValoreAttributoDTO> attributi = new ArrayList<>();
}

