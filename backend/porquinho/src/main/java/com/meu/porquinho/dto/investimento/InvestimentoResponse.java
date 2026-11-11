package com.meu.porquinho.dto.investimento;

import com.meu.porquinho.model.DirecaoAlerta;
import com.meu.porquinho.model.Investimento;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InvestimentoResponse {

    private UUID id;
    private String simbolo;
    private BigDecimal quantidade;
    private BigDecimal valorInicial;
    private BigDecimal percentualAlerta;
    private DirecaoAlerta direcao;
    private boolean ativo;
    private LocalDateTime notificadoEm;
    private LocalDateTime createdAt;

    // Enriquecidos com a cotação atual da Binance (null se indisponível)
    private BigDecimal cotacaoAtual;
    private BigDecimal variacaoPercentual;
    private BigDecimal valorTotalAtual;

    public static InvestimentoResponse from(Investimento inv) {
        return InvestimentoResponse.builder()
                .id(inv.getId())
                .simbolo(inv.getSimbolo())
                .quantidade(inv.getQuantidade())
                .valorInicial(inv.getValorInicial())
                .percentualAlerta(inv.getPercentualAlerta())
                .direcao(inv.getDirecao())
                .ativo(inv.isAtivo())
                .notificadoEm(inv.getNotificadoEm())
                .createdAt(inv.getCreatedAt())
                .build();
    }
}
