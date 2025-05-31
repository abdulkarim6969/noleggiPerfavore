package com.example.progettoinfonoleggi.service.utenti;

import com.example.progettoinfonoleggi.dto.LoginRequestDTO;
import com.example.progettoinfonoleggi.dto.RegisterRequestDTO;
import com.example.progettoinfonoleggi.model.utenti.Saldo;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.repository.utenti.SaldoRepository;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import com.example.progettoinfonoleggi.service.jwt.JWTservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class SaldoService {
    @Autowired
    private SaldoRepository saldoRepository;

    public void creaSaldoPerUtente(String emailUtente) {
        Saldo saldo = new Saldo();
        saldo.setEmailUtente(emailUtente);
        saldo.setSaldo(BigDecimal.ZERO);
        saldoRepository.save(saldo);
    }
}
