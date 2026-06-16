package com.meu.porquinho.dto.lancamento;

import com.meu.porquinho.model.Lancamento;
import com.meu.porquinho.model.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor
@AllArgsConstructor
public class LancamentoDTO {

    private UUID id;

    @NotNull
    private TipoLancamento tipo;

    @NotBlank
    @Size(max = 200)
    private String descricao;

    @NotNull
    @Positive
    private BigDecimal valor;

    private String categoria;

    @NotNull
    private LocalDate data;

    private LocalDateTime createdAt;

    public static LancamentoDTO from(Lancamento entity) {
        return LancamentoDTO.builder()
                .id(entity.getId())
                .tipo(entity.getTipo())
                .descricao(entity.getDescricao())
                .valor(entity.getValor())
                .categoria(entity.getCategoria())
                .data(entity.getData())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
