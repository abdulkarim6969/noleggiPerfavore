package com.example.progettoinfonoleggi.controller.oggetti;


import com.example.progettoinfonoleggi.dto.*;
import com.example.progettoinfonoleggi.service.noleggi.NoleggioService;
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
    private NoleggioService noleggioService;

    @Autowired
    private ValoriAttributiService valoriAttributiService;


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

    @PutMapping("/valoriAttributi")
    public void aggiornaValoriAttributi(@RequestBody AggiungiValoriAttributiDTO dto) {
        valoriAttributiService.aggiornaValoriAttributi(dto);
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

    @PostMapping(path = "/crea", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> salvaOggettoCompleto(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("dati") @Valid CreaOggettoCompletoDTO dto) throws IOException {

        try {
            oggettiService.salvaOggettoCompleto(file, dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Oggetto e attributi salvati con successo");

        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getReason());

        } catch (IOException ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante il salvataggio dell'immagine: " + ex.getMessage());
        }
    }

    @GetMapping("/random/{start}/{end}/{emailProprietario}")
    public ResponseEntity<?> getOggettiRandom(
            @PathVariable int start,
            @PathVariable int end,
            @PathVariable String emailProprietario) {

        Map<String, Object> result = oggettiService.getOggettiRandomIntervalloEscludendoProprietario(start, end, emailProprietario);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/rimuovi/{id}")
    public ResponseEntity<String> eliminaOggetto(@PathVariable Integer id) {
        try {
            if (noleggioService.oggettoHaNoleggiAttivi(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Impossibile eliminare l'oggetto: è attualmente coinvolto in un noleggio attivo.");
            }

            oggettiService.rimuoviOggetto(id);
            return ResponseEntity.ok("Oggetto eliminato con successo");
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione dell'oggetto: " + ex.getMessage());
        }
    }

    @GetMapping("/categorie")
    public ResponseEntity<List<CategoriaDTO>> getCategorie() {
        List<CategoriaDTO> categorie = oggettiService.getTutteCategorie();
        return ResponseEntity.ok(categorie);
    }

    @GetMapping("/attributiCategoria/{nomeCategoria}")
    public List<AttributoCategoriaDTO> getAttributiByCategoria(@PathVariable String nomeCategoria) {
        return oggettiService.getAttributiByNomeCategoria(nomeCategoria);
    }

}
