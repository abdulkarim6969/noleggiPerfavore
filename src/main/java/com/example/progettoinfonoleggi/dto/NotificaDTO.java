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

    public NotificaDTO(Long id, String messaggio, String tipo, LocalDateTime data, boolean letto, String emailMittente, String nomeOggetto) {
        this.id = id;
        this.messaggio = messaggio;
        this.tipo = tipo;
        this.data = data;
        this.letto = letto;
        this.emailMittente = emailMittente;
        this.nomeOggetto = nomeOggetto;
    }
}

