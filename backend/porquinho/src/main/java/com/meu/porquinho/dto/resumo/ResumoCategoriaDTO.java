package com.meu.porquinho.dto.resumo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ResumoCategoriaDTO {
    private String     categoria;
    private BigDecimal total;
    private Long       quantidade;

    public ResumoCategoriaDTO(String categoria, Object total, Object quantidade) {
        this.categoria = categoria;
        this.total = toBigDecimal(total);
        this.quantidade = toLong(quantidade);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return BigDecimal.ZERO;
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long l) return l;
        if (value instanceof Number n) return n.longValue();
        return 0L;
    }
}
