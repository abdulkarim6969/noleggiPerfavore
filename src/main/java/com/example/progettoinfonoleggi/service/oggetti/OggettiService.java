package com.example.progettoinfonoleggi.service.oggetti;

import com.example.progettoinfonoleggi.dto.*;
import com.example.progettoinfonoleggi.model.oggetti.categorie.CategorieOggetti;
import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.repository.oggetti.categorie.CategorieOggettiRepository;
import com.example.progettoinfonoleggi.repository.oggetti.OggettiRepository;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OggettiService {

    @Autowired
    private  OggettiRepository oggettiRepository;

    @Autowired
    private  UtentiRepository utentiRepository;

    @Autowired
    private  CategorieOggettiRepository categorieRepository;

    @Autowired
    private ValoriAttributiService valoriAttributiService;

    @Transactional
    public void salvaOggettoCompleto(MultipartFile file, CreaOggettoCompletoDTO dto) throws IOException {
        // 1. Salva l'oggetto principale
        Integer idOggetto = salvaOggettoERitornaId(file, dto);

        // 2. Salva gli attributi
        valoriAttributiService.aggiungiValoriAttributi(
                new AggiungiValoriAttributiDTO(idOggetto, dto.getAttributi())
        );
    }

    // Metodo privato riadattato da salvaOggetto() per ritornare l'ID
    private Integer salvaOggettoERitornaId(MultipartFile file, CreaOggettoCompletoDTO dto) throws IOException {
        Utenti proprietario = utentiRepository
                .findByEmail(dto.getEmailProprietario())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Errore: utente con email '" + dto.getEmailProprietario() + "' non trovato"
                ));

        CategorieOggetti categoria = categorieRepository
                .findByNome(dto.getNomeCategoria())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Errore: categoria '" + dto.getNomeCategoria() + "' non trovata"
                ));

        Oggetti o = new Oggetti();
        o.setNome(dto.getNome());
        o.setDescrizione(dto.getDescrizione());
        o.setPrezzoGiornaliero(BigDecimal.valueOf(dto.getPrezzoGiornaliero()));
        o.setEmailProprietario(proprietario);
        o.setNomeCategoria(categoria);
        o.setImmagine(file.getBytes());

        return oggettiRepository.save(o).getId(); // Ritorna l'ID dell'oggetto salvato
    }

    public void salvaOggetto(MultipartFile file, CreaOggettoDTO dto) throws IOException {
        // 1. Controllo Utente
        Utenti proprietario = utentiRepository
                .findByEmail(dto.getEmailProprietario())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Errore: utente con email '" + dto.getEmailProprietario() + "' non trovato"
                ));

        // 2. Controllo Categoria
        CategorieOggetti categoria = categorieRepository
                .findByNome(dto.getNomeCategoria())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Errore: categoria '" + dto.getNomeCategoria() + "' non trovata"
                ));

        // 3. Costruisco e salvo Oggetti
        Oggetti o = new Oggetti();
        o.setNome(dto.getNome());
        o.setDescrizione(dto.getDescrizione());
        o.setPrezzoGiornaliero(BigDecimal.valueOf(dto.getPrezzoGiornaliero()));
        o.setEmailProprietario(proprietario);
        o.setNomeCategoria(categoria);
        o.setImmagine(file.getBytes());

        oggettiRepository.save(o);
    }

    public void creaOggetto(OggettoDTO dto) {
        // 1) Recupera l’utente proprietario
        Utenti proprietario = utentiRepository.findByEmail(dto.getEmailProprietario())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato: " + dto.getEmailProprietario()));

        // 2) Recupera la categoria
        CategorieOggetti categoria = categorieRepository.findByNome(dto.getNomeCategoria())
                .orElseThrow(() -> new EntityNotFoundException("Categoria non trovata: " + dto.getNomeCategoria()));

        // 3) Mappa a entity
        Oggetti o = new Oggetti();
        o.setEmailProprietario(proprietario);
        o.setNomeCategoria(categoria);
        o.setNome(dto.getNome());
        o.setDescrizione(dto.getDescrizione());
        o.setPrezzoGiornaliero(dto.getPrezzoGiornaliero());
        // Imposta manualmente date (in alternativa puoi lasciare DB default)
        Instant now = Instant.now();
        o.setDataCreazione(now);
        o.setDataUltimaModifica(now);
        // Decodifica Base64 in byte[]
        byte[] imageBytes = Base64.getDecoder().decode(dto.getImmagineBase64());
        o.setImmagine(imageBytes);

        // 4) Salva
        oggettiRepository.save(o);
    }

    public List<OggettoCompletoDTO> getOggettiByEmailProprietario(String email) {
        return oggettiRepository.findByEmailProprietario_Email(email).stream()
                .map(this::convertiACompletoDTO)
                .collect(Collectors.toList());
    }


    public List<OggettoCompletoDTO> getOggettiByNomeCategoria(String nomeCategoria) {
        return oggettiRepository.findByNomeCategoria_Nome(nomeCategoria).stream()
                .map(this::convertiACompletoDTO)
                .collect(Collectors.toList());
    }

    public List<OggettoCompletoDTO> getOggettiByNomeSimile(String nome) {
        return oggettiRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::convertiACompletoDTO)
                .collect(Collectors.toList());
    }

    public OggettoCompletoDTO getOggettoById(Integer id) {
        return convertiACompletoDTO(
                oggettiRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Oggetto non trovato"))
        );
    }

    public byte[] getImmagineOggetto(Integer idOggetto) {
        Oggetti o = oggettiRepository.findById(idOggetto)
                .orElseThrow(() -> new IllegalArgumentException("Oggetto non trovato"));

        byte[] img = o.getImmagine();
        System.out.println(Arrays.toString(img));
        if (img == null || img.length == 0) {
            throw new IllegalStateException("Immagine non presente");
        }
        return img;
    }

    // Metodo privato di supporto per la conversione
    public OggettoCompletoDTO convertiACompletoDTO(Oggetti oggetto) {
        // 1. Converti l'oggetto base
        OggettoCompletoDTO dto = new OggettoCompletoDTO();

        // Mappa i campi base
        String base64 = Base64.getEncoder().encodeToString(oggetto.getImmagine());
        String dataUrl = "data:image/jpeg;base64," + base64;

        dto.setId(oggetto.getId());
        dto.setNome(oggetto.getNome());
        dto.setDescrizione(oggetto.getDescrizione());
        dto.setPrezzoGiornaliero(oggetto.getPrezzoGiornaliero());
        dto.setEmailProprietario(oggetto.getEmailProprietario().getEmail());
        dto.setNomeCategoria(oggetto.getNomeCategoria().getNome());
        dto.setImmagineBase64(dataUrl);
        dto.setDataCreazione(oggetto.getDataCreazione());
        dto.setUltimaModifica(oggetto.getDataUltimaModifica());

        // 2. Aggiungi gli attributi
        dto.setAttributi(valoriAttributiService.getValoriPerOggetto(oggetto.getId()));

        return dto;
    }

}
