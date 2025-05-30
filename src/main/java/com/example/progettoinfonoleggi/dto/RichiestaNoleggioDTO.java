package com.example.progettoinfonoleggi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RichiestaNoleggioDTO {

    @NotNull(message = "L'ID del noleggio")
    private Integer idNoleggio;

    @NotNull(message = "L'email dell'utente è obbligatoria")
    private String emailUtenteRichiedente;


    @NotNull(message = "L'ID dell'oggetto è obbligatorio")
    private Integer codiceOggetto;

    @NotNull(message = "La data di inizio è obbligatoria")
    @Future(message = "La data di inizio deve essere futura")
    private LocalDate dataInizio;

    @NotNull(message = "La data di fine è obbligatoria")
    @Future(message = "La data di fine deve essere futura")
    private LocalDate dataFine;

    private OggettoCompletoDTO oggetto;
}

