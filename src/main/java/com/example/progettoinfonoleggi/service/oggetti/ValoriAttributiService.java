package com.example.progettoinfonoleggi.service.oggetti;

import com.example.progettoinfonoleggi.dto.AggiungiValoriAttributiDTO;
import com.example.progettoinfonoleggi.dto.ValoreAttributoDTO;
import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.oggetti.categorie.AttributiCategoria;
import com.example.progettoinfonoleggi.model.oggetti.categorie.ValoriAttributi;
import com.example.progettoinfonoleggi.repository.oggetti.OggettiRepository;
import com.example.progettoinfonoleggi.repository.oggetti.categorie.AttributiCategoriaRepository;
import com.example.progettoinfonoleggi.repository.oggetti.categorie.ValoriAttributiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValoriAttributiService {

    @Autowired
    private ValoriAttributiRepository valoriAttributiRepository;

    @Autowired
    private AttributiCategoriaRepository attributiCategoriaRepository;

    @Autowired
    private OggettiRepository oggettiRepository;

    public void aggiungiValoriAttributi(AggiungiValoriAttributiDTO dto) {
        Oggetti oggetto = oggettiRepository.findById(dto.getIdOggetto())
                .orElseThrow(() -> new RuntimeException("Oggetto non trovato"));

        for (ValoreAttributoDTO valoreAttribuito : dto.getAttributi()) {
            AttributiCategoria attributo = attributiCategoriaRepository.findByCategoria_Nome(oggetto.getNomeCategoria().getNome())
                    .stream()
                    .filter(a -> a.getNomeAttributo().equals(valoreAttribuito.getNomeAttributo()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Attributo non valido per la categoria"));

            ValoriAttributi valore = new ValoriAttributi();
            valore.setOggetto(oggetto);
            valore.setAttributo(attributo);
            valore.setValore(valoreAttribuito.getValore());

            valoriAttributiRepository.save(valore);
        }
    }

    /**
     * Restituisce la lista degli attributi obbligatori per una categoria.
     */
    public List<String> getAttributiObbligatoriPerCategoria(String nomeCategoria) {
        return attributiCategoriaRepository.findByCategoria_Nome(nomeCategoria).stream()
                .map(AttributiCategoria::getNomeAttributo)
                .collect(Collectors.toList());
    }

    public List<ValoreAttributoDTO> getValoriPerOggetto(Integer idOggetto) {
        return valoriAttributiRepository.findByOggetto_Id(idOggetto).stream()
                .map(v -> {
                    ValoreAttributoDTO dto = new ValoreAttributoDTO();
                    dto.setNomeAttributo(v.getAttributo().getNomeAttributo());
                    dto.setValore(v.getValore());
                    return dto;
                }).collect(Collectors.toList());
    }
}
