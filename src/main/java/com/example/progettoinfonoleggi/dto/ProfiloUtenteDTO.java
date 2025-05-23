package com.example.progettoinfonoleggi.dto;

import java.util.List;

public class ProfiloUtenteDTO {
    private UtenteDTO utente; // o direttamente Utenti, se già DTO
    private List<NoleggioDTO> noleggiProprietario;
}
