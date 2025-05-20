package com.example.progettoinfonoleggi.service.oggetti;

import com.example.progettoinfonoleggi.dto.CreaOggettoDTO;
import com.example.progettoinfonoleggi.dto.OggettoDTO;
import com.example.progettoinfonoleggi.model.oggetti.categorie.CategorieOggetti;
import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.repository.oggetti.categorie.CategorieOggettiRepository;
import com.example.progettoinfonoleggi.repository.oggetti.OggettiRepository;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public List<OggettoDTO> getOggettiByEmailProprietario(String email) {
        List<Oggetti> lista = oggettiRepository.findByEmailProprietario_Email(email);
         return lista.stream()
                .map(o -> {
                    // codifica + prefisso, qui assumiamo JPEG
                    String base64 = Base64.getEncoder().encodeToString(o.getImmagine());
                    String dataUrl = "data:image/jpeg;base64," + base64;
                    return new OggettoDTO(
                           o.getDataCreazione(),
                            o.getDescrizione(),
                            o.getEmailProprietario().getEmail(),
                            o.getId(),
                            dataUrl,
                            o.getDataUltimaModifica(),
                            o.getNome(),
                            o.getNomeCategoria().getNome(),
                            o.getPrezzoGiornaliero()

                    );
                })
                .collect(Collectors.toList());
    }

    public List<OggettoDTO> getOggettiByNomeCategoria(String nomeCategoria) {
        List<Oggetti> lista = oggettiRepository.findByNomeCategoria_Nome(nomeCategoria);
        return lista.stream()
                .map(o -> {
                    // codifica + prefisso, qui assumiamo JPEG
                    String base64 = Base64.getEncoder().encodeToString(o.getImmagine());
                    String dataUrl = "data:image/jpeg;base64," + base64;
                    return new OggettoDTO(
                            o.getDataCreazione(),
                            o.getDescrizione(),
                            o.getEmailProprietario().getEmail(),
                            o.getId(),
                            dataUrl,
                            o.getDataUltimaModifica(),
                            o.getNome(),
                            o.getNomeCategoria().getNome(),
                            o.getPrezzoGiornaliero()

                    );
                })
                .collect(Collectors.toList());
    }

    public List<OggettoDTO> getOggettiByNomeSimile(String nome) {
        List<Oggetti> lista = oggettiRepository.findByNomeContainingIgnoreCase(nome);
        return lista.stream()
                .map(o -> {
                    String base64 = Base64.getEncoder().encodeToString(o.getImmagine());
                    String dataUrl = "data:image/jpeg;base64," + base64;
                    return new OggettoDTO(
                            o.getDataCreazione(),
                            o.getDescrizione(),
                            o.getEmailProprietario().getEmail(),
                            o.getId(),
                            dataUrl,
                            o.getDataUltimaModifica(),
                            o.getNome(),
                            o.getNomeCategoria().getNome(),
                            o.getPrezzoGiornaliero()
                    );
                })
                .collect(Collectors.toList());
    }


    public OggettoDTO getOggettoById(int id) {
        OggettoDTO oggettoDTO = new OggettoDTO();
        Oggetti oggetti = oggettiRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Oggetto non trovato"));

        String base64 = Base64.getEncoder().encodeToString(oggetti.getImmagine());
        String dataUrl = "data:image/jpeg;base64," + base64;

        oggettoDTO.setId(oggetti.getId());
        oggettoDTO.setDescrizione(oggetti.getDescrizione());
        oggettoDTO.setNome(oggetti.getNome());
        oggettoDTO.setEmailProprietario(oggetti.getEmailProprietario().getEmail());
        oggettoDTO.setNomeCategoria(oggetti.getNomeCategoria().getNome());
        oggettoDTO.setDataCreazione(oggetti.getDataCreazione());
        oggettoDTO.setDataCreazione(oggetti.getDataUltimaModifica());
        oggettoDTO.setPrezzoGiornaliero(oggetti.getPrezzoGiornaliero());
        oggettoDTO.setImmagineBase64(dataUrl);


        return oggettoDTO;

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
}
