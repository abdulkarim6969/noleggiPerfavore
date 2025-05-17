package com.example.progettoinfonoleggi.controller.oggetti;


import com.example.progettoinfonoleggi.dto.CreaOggettoDTO;
import com.example.progettoinfonoleggi.dto.OggettoDTO;
import com.example.progettoinfonoleggi.service.oggetti.OggettiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/oggetti")
@CrossOrigin("http://localhost:5173")
public class OggettiController {

    @Autowired
    private OggettiService oggettiService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> salvaOggetto(
            @RequestPart("file") MultipartFile file,
            @RequestPart("dati") CreaOggettoDTO dto) throws IOException {
        oggettiService.salvaOggetto(file, dto);
        return ResponseEntity.ok("Oggetto salvato con successo");
    }


    @GetMapping("")
    public ResponseEntity<List<OggettoDTO>> getOggettiByEmail(@RequestParam String email) {
        List<OggettoDTO> lista = oggettiService.getOggettiByEmailProprietario(email);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/oggetto/{id}")
    public ResponseEntity<OggettoDTO> getOggettoById(@PathVariable int id) {
        OggettoDTO  oggettoDTO = oggettiService.getOggettoById(id);
        return ResponseEntity.ok(oggettoDTO);
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

}
