package com.example.progettoinfonoleggi.service.utenti;

import com.example.progettoinfonoleggi.dto.LoginRequestDTO;
import com.example.progettoinfonoleggi.dto.RegisterRequestDTO;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import com.example.progettoinfonoleggi.service.jwt.JWTservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtentiService {
    @Autowired
    private UtentiRepository repo;

    @Autowired
    private JWTservice jwtService;

    @Autowired
    AuthenticationManager authManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public Utenti register(RegisterRequestDTO userDTO) {
        Utenti user = new Utenti();

        user.setEmail(userDTO.getEmail());
        user.setNomeUtente(userDTO.getNomeUtente());
        user.setNome(userDTO.getNome());
        user.setCognome(userDTO.getCognome());
        user.setIndirizzo(userDTO.getIndirizzo());
        user.setCap(userDTO.getCap());
        user.setCitta(userDTO.getCitta());
        user.setTelefono(userDTO.getTelefono());
        user.setDataNascita(userDTO.getDataNascita());
        user.setPassword(encoder.encode(userDTO.getPassword()));

        repo.save(user);
        return user;
    }

    public String verify(LoginRequestDTO user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getEmail())  ;
        } else {
            return "fail";
        }
    }
}
