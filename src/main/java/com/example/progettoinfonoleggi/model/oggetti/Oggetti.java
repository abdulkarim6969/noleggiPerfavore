package com.example.progettoinfonoleggi.model.oggetti;

import com.example.progettoinfonoleggi.model.utenti.Utenti;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "oggetti")
public class Oggetti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codiceID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emailProprietario", nullable = false)
    private Utenti emailProprietario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nomeCategoria", nullable = false)
    private CategorieOggetti nomeCategoria;

    @Size(max = 40)
    @NotNull
    @Column(name = "nome", nullable = false, length = 40)
    private String nome;

    @Size(max = 200)
    @NotNull
    @Column(name = "descrizione", nullable = false, length = 200)
    private String descrizione;

    @NotNull
    @Column(name = "prezzoGiornaliero", nullable = false, precision = 5, scale = 2)
    private BigDecimal prezzoGiornaliero;

    @CreationTimestamp
    @Column(name = "dataCreazione", nullable = false, updatable = false)
    private Instant dataCreazione;

    @UpdateTimestamp
    @Column(name = "dataUltimaModifica")
    private Instant dataUltimaModifica;

    @NotNull
    @Column(name = "immagine", nullable = false)
    private byte[] immagine;

}