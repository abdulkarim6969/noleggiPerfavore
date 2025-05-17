package com.example.progettoinfonoleggi.repository.jwt;

import com.example.progettoinfonoleggi.model.token.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {
    boolean existsByToken(String token);
}
