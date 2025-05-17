package com.example.progettoinfonoleggi.model.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @Size(max = 512)
    @Column(name = "token", nullable = false, length = 512)
    private String token;

    @NotNull
    @Column(name = "expiration", nullable = false)
    private Instant expiration;

}