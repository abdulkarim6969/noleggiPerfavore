package com.example.progettoinfonoleggi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class OggettoCompletoDTO {
    private Integer id;
    private String emailProprietario;
    private String nomeCategoria;
    private String nome;
    private String descrizione;
    private BigDecimal prezzoGiornaliero;
    private String immagineBase64;
    private Instant dataCreazione;
    private Instant ultimaModifica;
    private List<ValoreAttributoDTO> attributi;
}