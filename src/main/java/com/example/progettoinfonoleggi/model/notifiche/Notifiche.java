package com.example.progettoinfonoleggi.model.notifiche;

import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="notifiche")
public class Notifiche {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailDestinatario", referencedColumnName = "email")
    private Utenti emailDestinatario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailMittente", referencedColumnName = "email")
    private Utenti emailMittente;

    @Column(nullable = false, length = 300)
    private String messaggio;

    @Column(nullable = false, length = 40)
    private String tipo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idOggetto", nullable = false)
    private Oggetti idOggetto;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime data;

    @PrePersist
    protected void onCreate() {
        if (data == null) {
            data = LocalDateTime.now();
        }
    }

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean letto;
}
