package com.meu.porquinho.dto.resumo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ResumoMensalDTO {
    private String     mesAno;
    private BigDecimal entradas;
    private BigDecimal gastos;
    private BigDecimal investimentos;

    public ResumoMensalDTO(String mesAno, Object entradas, Object gastos, Object investimentos) {
        this.mesAno = mesAno;
        this.entradas = toBigDecimal(entradas);
        this.gastos = toBigDecimal(gastos);
        this.investimentos = toBigDecimal(investimentos);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return BigDecimal.ZERO;
    }

    public BigDecimal getSaldo() {
        return entradas.subtract(gastos).subtract(investimentos);
    }
}
