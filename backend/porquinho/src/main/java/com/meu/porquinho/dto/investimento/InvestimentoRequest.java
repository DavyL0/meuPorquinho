package com.meu.porquinho.dto.investimento;

import com.meu.porquinho.model.DirecaoAlerta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvestimentoRequest {

    /** Par na Binance, ex: BTCBRL. */
    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[A-Za-z0-9]{2,20}$",
            message = "Símbolo inválido (ex: BTCBRL)")
    private String simbolo;

    @NotNull
    @DecimalMin(value = "0.00000001")
    private BigDecimal quantidade;

    /** Preço unitário pago na compra. */
    @NotNull
    @DecimalMin(value = "0.00000001")
    private BigDecimal valorInicial;

    /** Percentual que dispara o alerta (ex: 10 = 10%). */
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal percentualAlerta;

    @NotNull
    private DirecaoAlerta direcao;
}
