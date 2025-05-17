package com.example.progettoinfonoleggi.controller.utenti;

import com.example.progettoinfonoleggi.dto.PreferitiDTO;
import com.example.progettoinfonoleggi.service.utenti.preferiti.PreferitiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferiti")
@CrossOrigin("http://localhost:5173")
public class PreferitiController {

    @Autowired
    private PreferitiService preferitiService;


    @PostMapping("aggiungi")
    public ResponseEntity<?> addPreferiti(@RequestBody PreferitiDTO preferito) {
        preferitiService.aggiungiPreferito(preferito);
        return ResponseEntity.ok("Oggetto aggiunto ai preferiti");
    }

    @PostMapping("rimuovi")
    public ResponseEntity<?> rimuovi(@RequestBody PreferitiDTO preferito) {
        preferitiService.rimuoviPreferito(preferito);
        return ResponseEntity.ok("Oggetto rimosso dai preferiti");
    }
}