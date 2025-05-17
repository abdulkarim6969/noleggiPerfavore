package com.example.progettoinfonoleggi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;


@Getter
@Setter
public class OggettoDTO {

    private Integer id;
    private String emailProprietario;
    private String nomeCategoria;
    private String nome;
    private String descrizione;
    private BigDecimal prezzoGiornaliero;
    private String immagineBase64;
    private Instant dataCreazione;
    private Instant ultimaModifica;


    public OggettoDTO(Instant dataCreazione, String descrizione, String emailProprietario, Integer id, String immagineBase64, Instant ultimaModifica, String nome, String nomeCategoria, BigDecimal prezzoGiornaliero) {
        this.dataCreazione = dataCreazione;
        this.descrizione = descrizione;
        this.emailProprietario = emailProprietario;
        this.id = id;
        this.immagineBase64 = immagineBase64;
        this.ultimaModifica = ultimaModifica;
        this.nome = nome;
        this.nomeCategoria = nomeCategoria;
        this.prezzoGiornaliero = prezzoGiornaliero;
    }

    public OggettoDTO() {

    }
}
