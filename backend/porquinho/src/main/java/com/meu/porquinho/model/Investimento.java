package com.meu.porquinho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_investimento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Investimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Par negociado na Binance, ex: BTCBRL, ETHBRL. */
    @Column(nullable = false, length = 20)
    private String simbolo;

    @Column(nullable = false, precision = 30, scale = 8)
    private BigDecimal quantidade;

    /** Preço unitário pago no momento da compra. */
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal valorInicial;

    /** Percentual de variação que dispara o alerta (ex: 10 = 10%). */
    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal percentualAlerta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DirecaoAlerta direcao;

    @Column(nullable = false)
    private boolean ativo;

    private LocalDateTime notificadoEm;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
