package com.meu.porquinho.dto.resumo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumoComparativoDTO {
    private ResumoDTO mesAtual;
    private ResumoDTO  mesAnterior;
    private BigDecimal variacaoGastos;
    private BigDecimal variacaoEntradas;
    private BigDecimal variacaoSaldo;
}
