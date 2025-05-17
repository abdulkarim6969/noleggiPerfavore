package com.example.progettoinfonoleggi.controller.utenti;

import com.example.progettoinfonoleggi.dto.LoginRequestDTO;
import com.example.progettoinfonoleggi.dto.LoginResponseDTO;
import com.example.progettoinfonoleggi.dto.RegisterRequestDTO;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import com.example.progettoinfonoleggi.service.jwt.JWTservice;
import com.example.progettoinfonoleggi.service.utenti.UtentiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/utenti")
@CrossOrigin("http://localhost:5173")
public class UtentiController {
    @Autowired
    private UtentiService utentiService;
    @Autowired
    private UtentiRepository utentiRepository;

    @GetMapping("/utente/{emailUtente}")
    public ResponseEntity<Utenti> getUtente(@PathVariable String emailUtente) {
        Optional<Utenti> u = utentiRepository.findByEmail(emailUtente);
        if (u.isPresent()) {
            return new ResponseEntity<>(u.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}