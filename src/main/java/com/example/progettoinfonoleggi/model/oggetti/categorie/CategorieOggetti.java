package com.example.progettoinfonoleggi.model.oggetti.categorie;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categorieOggetti")
public class CategorieOggetti {
    @Id
    @Size(max = 25)
    @Column(name = "nome", nullable = false, length = 15)
    private String nome;

    @Size(max = 100)
    @Column(name = "descrizione", length = 100)
    private String descrizione;

}