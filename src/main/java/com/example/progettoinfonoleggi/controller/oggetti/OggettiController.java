package com.example.progettoinfonoleggi.controller.oggetti;


import com.example.progettoinfonoleggi.dto.*;
import com.example.progettoinfonoleggi.service.oggetti.OggettiService;
import com.example.progettoinfonoleggi.service.oggetti.ValoriAttributiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oggetti")
@CrossOrigin("http://localhost:5173")
public class OggettiController {

    @Autowired
    private OggettiService oggettiService;

    @Autowired
    private ValoriAttributiService valoriAttributiService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> salvaOggetto(
            @RequestPart("file") MultipartFile file,
            @RequestPart("dati") CreaOggettoDTO dto) throws IOException {
        oggettiService.salvaOggetto(file, dto);
        return ResponseEntity.ok("Oggetto salvato con successo");
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<OggettoCompletoDTO>> getOggettiByEmail(@PathVariable String email) {
        List<OggettoCompletoDTO> lista = oggettiService.getOggettiByEmailProprietario(email);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/categoria/{nomeCategoria}")
    public ResponseEntity<List<OggettoCompletoDTO>> getOggettiByNomeCategoria(@PathVariable String nomeCategoria) {
        List<OggettoCompletoDTO> lista = oggettiService.getOggettiByNomeCategoria(nomeCategoria);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}/immagine")
    public ResponseEntity<byte[]> getImmagine(@PathVariable Integer id) {
        try {
            byte[] img = oggettiService.getImmagineOggetto(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(img, headers, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/cerca/{nome}")
    public ResponseEntity<List<OggettoCompletoDTO>> cercaOggettiPerNome(@PathVariable String nome) {
        List<OggettoCompletoDTO> lista = oggettiService.getOggettiByNomeSimile(nome);
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/addValoriAttributi")
    public ResponseEntity<String> aggiungiValoriAttributi(@RequestBody AggiungiValoriAttributiDTO dto) {
        valoriAttributiService.aggiungiValoriAttributi(dto);
        return ResponseEntity.ok("Valori attributi salvati con successo.");
    }

    @GetMapping("/{id}/attributi")
    public ResponseEntity<List<ValoreAttributoDTO>> getValoriAttributiPerOggetto(@PathVariable Integer id) {
        List<ValoreAttributoDTO> attributi = valoriAttributiService.getValoriPerOggetto(id);
        return ResponseEntity.ok(attributi);
    }

    @GetMapping("/completo/{id}")
    public ResponseEntity<OggettoCompletoDTO> getOggettoCompleto(@PathVariable Integer id) {
        OggettoCompletoDTO response = oggettiService.getOggettoById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/salvaCompleto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> salvaOggettoCompleto(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("dati") @Valid CreaOggettoCompletoDTO dto) throws IOException {

        try {
            oggettiService.salvaOggettoCompleto(file, dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Oggetto e attributi salvati con successo");

        } catch (ResponseStatusException ex) {
            // Gestisce gli errori di validazione già presenti nel service
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getReason());

        } catch (IOException ex) {
            // Gestione specifica per errori di I/O (es. file corrotto)
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante il salvataggio dell'immagine: " + ex.getMessage());
        }
    }

    @GetMapping("/random")
    public ResponseEntity<?> getOggettiRandom(
            @RequestParam int start,
            @RequestParam int end,
            @RequestParam String emailProprietario) {

        Map<String, Object> result = oggettiService.getOggettiRandomIntervalloEscludendoProprietario(start, end, emailProprietario);
        return ResponseEntity.ok(result);
    }

}
