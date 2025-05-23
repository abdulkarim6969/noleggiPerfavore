package com.example.progettoinfonoleggi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificaDTO {

    private Long id;
    private String messaggio;
    private String tipo;
    private LocalDateTime data;
    private boolean letto;
    private String emailMittente;
    private String nomeOggetto;

}

