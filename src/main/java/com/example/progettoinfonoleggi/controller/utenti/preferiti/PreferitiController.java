package com.example.progettoinfonoleggi.controller.utenti.preferiti;

import com.example.progettoinfonoleggi.dto.OggettoCompletoDTO;
import com.example.progettoinfonoleggi.dto.OggettoDTO;
import com.example.progettoinfonoleggi.dto.PreferitiDTO;
import com.example.progettoinfonoleggi.service.oggetti.OggettiService;
import com.example.progettoinfonoleggi.service.utenti.preferiti.PreferitiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferiti")
@CrossOrigin("http://localhost:5173")
public class PreferitiController {

    @Autowired
    private PreferitiService preferitiService;


    @PostMapping("/aggiungi")
    public ResponseEntity<?> addPreferiti(@RequestBody PreferitiDTO preferito) {
        preferitiService.aggiungiPreferito(preferito);
        return ResponseEntity.ok("Oggetto aggiunto ai preferiti");
    }

    @DeleteMapping("/rimuovi/{idOggetto}")
    public ResponseEntity<?> rimuovi(@PathVariable Integer idOggetto, Authentication authentication) {
        String emailUtente = authentication.getName(); // preso dal token JWT
        preferitiService.rimuoviPreferito(idOggetto, emailUtente);
        return ResponseEntity.ok("Oggetto rimosso dai preferiti");
    }



    @GetMapping("/{emailUtente}")
    public ResponseEntity<List<OggettoCompletoDTO>> getOggettiPreferiti(@PathVariable String emailUtente) {
        List<OggettoCompletoDTO> listaPreferiti = preferitiService.getOggettiPreferitiByEmailUtente(emailUtente);
        return ResponseEntity.ok(listaPreferiti);
    }
}
