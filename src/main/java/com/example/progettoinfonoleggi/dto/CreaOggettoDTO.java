package com.example.progettoinfonoleggi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreaOggettoDTO {
    private String nome;
    private String descrizione;
    private double prezzoGiornaliero;
    private String emailProprietario;
    private String nomeCategoria;
}
