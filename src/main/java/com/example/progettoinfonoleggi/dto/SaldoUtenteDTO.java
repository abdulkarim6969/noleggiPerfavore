package com.example.progettoinfonoleggi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaldoUtenteDTO {
    private Long id;
    private String emailUtente;
    private BigDecimal saldo;
}
