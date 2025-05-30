package com.example.progettoinfonoleggi.controller.noleggi;

import com.example.progettoinfonoleggi.dto.NoleggioConOggettoDTO;
import com.example.progettoinfonoleggi.dto.OggettoCompletoDTO;
import com.example.progettoinfonoleggi.dto.RichiestaNoleggioDTO;
import com.example.progettoinfonoleggi.service.noleggi.NoleggioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/noleggi")
public class NoleggiController {

    @Autowired
    private NoleggioService noleggioService;

    // Crea richiesta noleggio
    @PostMapping("/richieste")
    public ResponseEntity<?> creaRichiesta(@RequestBody RichiestaNoleggioDTO richiestaDTO) {
        try {
            RichiestaNoleggioDTO responseDTO = noleggioService.creaRichiestaNoleggio(richiestaDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Accetta richiesta noleggio (solo proprietario)
    @PostMapping("/richieste/{id}/accetta")
    public ResponseEntity<?> accettaRichiesta(@PathVariable Long id) {
        try {
            noleggioService.accettaRichiesta(id);
            return ResponseEntity.ok("Richiesta accettata e noleggio creato");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/richieste/{id}/rifiuta")
    public ResponseEntity<?> rifiutaRichiesta(@PathVariable Long id) {
        try {
            noleggioService.rifiutaRichiesta(id);
            return ResponseEntity.ok("Richiesta rifiutata ");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Restituisce tutti i giorni occupati per un oggetto, inclusi i 3 giorni di buffer prima e dopo ogni noleggio.
     * @param codiceOggetto ID dell'oggetto
     * @return Set di LocalDate con i giorni occupati
     */
    @GetMapping("/giorniOccupati/{codiceOggetto}")
    public ResponseEntity<List<LocalDate>> getGiorniOccupatiConBuffer(@PathVariable Integer codiceOggetto) {
        List<LocalDate> giorniOccupati = noleggioService.getGiorniOccupatiConBuffer(codiceOggetto);
        return ResponseEntity.ok(giorniOccupati);
    }

    @GetMapping("/attivi/proprietario/{email}")
    public ResponseEntity<List<NoleggioConOggettoDTO>> getNoleggiAttiviProprietario(@PathVariable String email) {
        try {
            List<NoleggioConOggettoDTO> result = noleggioService.getNoleggiAttiviProprietarioConOggetto(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/attivi/acquirente/{email}")
    public ResponseEntity<List<NoleggioConOggettoDTO>> getNoleggiAttiviAcquirente(@PathVariable String email) {
        try {
            List<NoleggioConOggettoDTO> result = noleggioService.getNoleggiAttiviAcquirente(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/richieste/ricevute/{emailProprietario}")
    public ResponseEntity<List<RichiestaNoleggioDTO>> getRichiesteRicevute(@PathVariable String emailProprietario) {
        List<RichiestaNoleggioDTO> richieste = noleggioService.getRichiesteRicevuteDaUtente(emailProprietario);
        return ResponseEntity.ok(richieste);
    }
}
