package com.example.progettoinfonoleggi.utility;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder {

    // Metodo per generare l'hash della password
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    // Metodo per confrontare la password inserita con quella salvata (comparazione dell'hash)
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}