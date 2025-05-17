package com.example.progettoinfonoleggi.service.utenti;

import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.model.utenti.UtentiPrincipal;
import com.example.progettoinfonoleggi.repository.utenti.UtentiRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtentiDetailService implements UserDetailsService {
    @Autowired
    private UtentiRepository userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utenti user = userRepo.findByEmail(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato: " ));
        if (user == null) {
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }

        return new UtentiPrincipal(user);
    }
}
