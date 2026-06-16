package com.meu.porquinho.dto.resumo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ResumoDTO {

    private BigDecimal totalEntradas;
    private BigDecimal totalGastos;
    private BigDecimal totalInvestimentos;

    public ResumoDTO(Object totalEntradas, Object totalGastos, Object totalInvestimentos) {
        this.totalEntradas = toBigDecimal(totalEntradas);
        this.totalGastos = toBigDecimal(totalGastos);
        this.totalInvestimentos = toBigDecimal(totalInvestimentos);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return BigDecimal.ZERO;
    }

    public BigDecimal getSaldo() {
        return totalEntradas
                .subtract(totalGastos)
                .subtract(totalInvestimentos);
    }
}
