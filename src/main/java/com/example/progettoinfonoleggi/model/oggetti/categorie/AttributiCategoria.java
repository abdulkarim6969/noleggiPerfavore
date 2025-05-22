package com.example.progettoinfonoleggi.model.oggetti.categorie;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attributiCategoria")
public class AttributiCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nomeCategoria", nullable = false)
    private CategorieOggetti categoria;

    @NotNull
    @Size(max = 50)
    @Column(name = "nomeAttributo", nullable = false, length = 50)
    private String nomeAttributo;
}
