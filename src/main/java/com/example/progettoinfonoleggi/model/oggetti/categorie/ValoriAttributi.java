package com.example.progettoinfonoleggi.model.oggetti.categorie;

import com.example.progettoinfonoleggi.model.oggetti.Oggetti;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "valoriAttributi")
public class ValoriAttributi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idOggetto", nullable = false)
    private Oggetti oggetto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idAttributo", nullable = false)
    private AttributiCategoria attributo;

    @NotNull
    @Size(max = 100)
    @Column(name = "valore", nullable = false, length = 100)
    private String valore;
}
