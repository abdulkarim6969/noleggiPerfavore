package com.example.progettoinfonoleggi.model.oggetti.categorie;

import com.example.progettoinfonoleggi.model.utenti.Utenti;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categoriePreferite")
public class CategoriePreferite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codiceID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emailUtente", referencedColumnName = "email", nullable = false)
    private Utenti emailUtente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomeCategoria", referencedColumnName = "nome", nullable = false)
    private CategorieOggetti nomeCategoria;
}
