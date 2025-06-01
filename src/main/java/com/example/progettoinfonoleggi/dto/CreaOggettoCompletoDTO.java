package com.example.progettoinfonoleggi.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CreaOggettoCompletoDTO {
    private String nome;
    private String descrizione;
    private double prezzoGiornaliero;
    private String emailProprietario;
    private String nomeCategoria;
    private List<ValoreAttributoDTO> attributi;
}