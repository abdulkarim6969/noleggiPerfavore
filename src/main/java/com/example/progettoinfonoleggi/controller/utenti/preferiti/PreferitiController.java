package com.example.progettoinfonoleggi.controller.utenti.preferiti;

import com.example.progettoinfonoleggi.dto.OggettoCompletoDTO;
import com.example.progettoinfonoleggi.dto.PreferitiDTO;
import com.example.progettoinfonoleggi.service.oggetti.OggettiService;
import com.example.progettoinfonoleggi.service.utenti.preferiti.PreferitiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/preferiti")
@CrossOrigin("http://localhost:5173")
public class PreferitiController {

    @Autowired
    private PreferitiService preferitiService;


    @PostMapping("/aggiungi/{idOggetto}")
    public ResponseEntity<?> addPreferiti(@PathVariable Integer idOggetto, Authentication authentication) {
        String emailUtente = authentication.getName(); // preso dal token JWT
        PreferitiDTO preferito = new PreferitiDTO();
        preferito.setEmailUtente(emailUtente);
        preferito.setIdOggetto(idOggetto);

        preferitiService.aggiungiPreferito(preferito);
        return ResponseEntity.ok("Oggetto aggiunto ai preferiti");
    }

    @DeleteMapping("/rimuovi/{idOggetto}")
    public ResponseEntity<?> rimuovi(@PathVariable Integer idOggetto, Authentication authentication) {
        String emailUtente = authentication.getName(); // preso dal token JWT
        preferitiService.rimuoviPreferito(idOggetto, emailUtente);
        return ResponseEntity.ok("Oggetto rimosso dai preferiti");
    }

    @GetMapping("/check/{idOggetto}")
    public ResponseEntity<?> isPreferito(@PathVariable Integer idOggetto, Authentication authentication) {
        String emailUtente = authentication.getName(); // preso dal token
        boolean isPreferito = preferitiService.isPreferito(emailUtente, idOggetto);
        return ResponseEntity.ok().body(Map.of("preferito", isPreferito));
    }


    @GetMapping("/{emailUtente}")
    public ResponseEntity<List<OggettoCompletoDTO>> getOggettiPreferiti(@PathVariable String emailUtente) {
        List<OggettoCompletoDTO> listaPreferiti = preferitiService.getOggettiPreferitiByEmailUtente(emailUtente);
        return ResponseEntity.ok(listaPreferiti);
    }
}
