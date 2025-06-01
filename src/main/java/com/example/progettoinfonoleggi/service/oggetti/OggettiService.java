package com.example.progettoinfonoleggi.service.oggetti;

import com.example.progettoinfonoleggi.dto.*;
import com.example.progettoinfonoleggi.model.oggetti.categorie.AttributiCategoria;
import com.example.progettoinfonoleggi.model.oggetti.categorie.CategorieOggetti;
import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.repository.oggetti.categorie.AttributiCategoriaRepository;
import com.example.progettoinfonoleggi.repository.oggetti.categorie.CategorieOggettiRepository;
import com.example.progettoinfonoleggi.repository.oggetti.OggettiRepository;
import com.example.progettoinfonoleggi.repository.oggetti.categorie.ValoriAttributiRepository;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import com.example.progettoinfonoleggi.repository.utenti.preferiti.PreferitiRepository;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OggettiService {

    @Autowired
    private  OggettiRepository oggettiRepository;

    @Autowired
    private  UtentiRepository utentiRepository;

    @Autowired
    private AttributiCategoriaRepository attributiCategoriaRepository;

    @Autowired
    private  CategorieOggettiRepository categorieRepository;

    @Autowired
    private ValoriAttributiService valoriAttributiService;

    @Autowired
    private ValoriAttributiRepository valoriAttributiRepository;

    @Autowired
    private PreferitiRepository preferitiRepository;

    private final Map<String, List<Integer>> cacheOggettiRandom = new ConcurrentHashMap<>();


    @Transactional
    public void salvaOggettoCompleto(MultipartFile file, CreaOggettoCompletoDTO dto) throws IOException {

        //controlla attributi obbligatori categoria
        List<String> attributiObbligatori = valoriAttributiService.getAttributiObbligatoriPerCategoria(dto.getNomeCategoria());

        Set<String> attributiForniti = dto.getAttributi() == null ? Collections.emptySet() :
                dto.getAttributi().stream()
                        .map(ValoreAttributoDTO::getNomeAttributo)
                        .collect(Collectors.toSet());

        if (!attributiForniti.containsAll(attributiObbligatori)) {
            List<String> mancanti = attributiObbligatori.stream()
                    .filter(a -> !attributiForniti.contains(a))
                    .collect(Collectors.toList());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mancano attributi obbligatori per la categoria: " + String.join(", ", mancanti));
        }

        //salva l'oggetto principale
        Integer idOggetto = salvaOggettoERitornaId(file, dto);

        //salva gli attributi
        valoriAttributiService.aggiungiValoriAttributi(
                new AggiungiValoriAttributiDTO(idOggetto, dto.getAttributi())
        );
    }

    public Map<String, Object> getOggettiRandomIntervalloEscludendoProprietario(int start, int end, String emailProprietario) {
        List<Integer> listaIdRandom = cacheOggettiRandom.computeIfAbsent(emailProprietario, email -> {
            List<Integer> ids = oggettiRepository.findIdByEmailProprietarioNot(email);
            Collections.shuffle(ids);
            return ids;
        });

        return creaRispostaRandom(start, end, listaIdRandom);
    }


    private Map<String, Object> creaRispostaRandom(int start, int end, List<Integer> listaIdRandom) {
        int fromIndex = Math.max(0, start - 1);
        int toIndex = Math.min(end, listaIdRandom.size());

        List<Integer> subListId = listaIdRandom.subList(fromIndex, toIndex);

        List<Oggetti> oggetti = oggettiRepository.findAllById(subListId);

        List<OggettoCompletoDTO> dtoList = oggetti.stream()
                .map(this::convertiACompletoDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("oggetti", dtoList);
        response.put("stop", toIndex == listaIdRandom.size());

        if (toIndex == listaIdRandom.size()) {
            listaIdRandom.clear();
        }

        return response;
    }


    private Integer salvaOggettoERitornaId(MultipartFile file, CreaOggettoCompletoDTO dto) throws IOException {
        Utenti proprietario = utentiRepository
                .findByEmail(dto.getEmailProprietario())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Errore: utente con email '" + dto.getEmailProprietario() + "' non trovato"
                ));

        CategorieOggetti categoria = categorieRepository
                .findByNomeIgnoreCase(dto.getNomeCategoria())
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

        return oggettiRepository.save(o).getId(); //ritorna l'id dell'oggetto salvato
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

    //conversione da oggetto normale a oggetto con attributi (completo)
    public OggettoCompletoDTO convertiACompletoDTO(Oggetti oggetto) {
        // 1. Converti l'oggetto base
        OggettoCompletoDTO dto = new OggettoCompletoDTO();

        if (oggetto.getImmagine() != null) {
            dto.setImmagineBase64(Base64.getEncoder().encodeToString(oggetto.getImmagine()));
        }

        dto.setId(oggetto.getId());
        dto.setNome(oggetto.getNome());
        dto.setDescrizione(oggetto.getDescrizione());
        dto.setPrezzoGiornaliero(oggetto.getPrezzoGiornaliero());
        dto.setEmailProprietario(oggetto.getEmailProprietario().getEmail());
        dto.setNomeCategoria(oggetto.getNomeCategoria().getNome());
        dto.setDataCreazione(oggetto.getDataCreazione());
        dto.setUltimaModifica(oggetto.getDataUltimaModifica());

        //aggiunta degli attributi
        dto.setAttributi(valoriAttributiService.getValoriPerOggetto(oggetto.getId()));

        return dto;
    }

    @Transactional
    public void rimuoviOggetto(Integer id) {
        Oggetti oggetto = oggettiRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oggetto non trovato"));

        valoriAttributiRepository.deleteByOggettoId(id);
        preferitiRepository.deleteByOggetto_Id(id);
        oggettiRepository.delete(oggetto);
    }

    public List<CategoriaDTO> getTutteCategorie() {
        List<CategorieOggetti> categorie = categorieRepository.findAll();
        return categorie.stream()
            .map(c -> {
                CategoriaDTO dto = new CategoriaDTO();
                dto.setNome(c.getNome());
                return dto;
            })
        .collect(Collectors.toList());
    }

    public List<AttributoCategoriaDTO> getAttributiByNomeCategoria(String nomeCategoria) {
        List<AttributiCategoria> attributi = attributiCategoriaRepository.findByCategoria_Nome(nomeCategoria);
        return attributi.stream()
                .map(a -> new AttributoCategoriaDTO(a.getNomeAttributo()))
                .collect(Collectors.toList());
    }

}


