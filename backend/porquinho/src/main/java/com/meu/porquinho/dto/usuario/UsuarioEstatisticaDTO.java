package com.meu.porquinho.dto.usuario;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// UsuarioEstatisticaDTO.java
@Data
@NoArgsConstructor
public class UsuarioEstatisticaDTO {
    private UUID            id;
    private String          nome;
    private String          email;
    private Long            totalLancamentos;
    private LocalDateTime   membroDesde;

    public UsuarioEstatisticaDTO(UUID id, String nome, String email, Object totalLancamentos, LocalDateTime membroDesde) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.totalLancamentos = toLong(totalLancamentos);
        this.membroDesde = membroDesde;
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long l) return l;
        if (value instanceof Number n) return n.longValue();
        return 0L;
    }
}
