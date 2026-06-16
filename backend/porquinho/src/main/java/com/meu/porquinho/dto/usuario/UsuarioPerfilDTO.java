package com.meu.porquinho.dto.usuario;

import com.meu.porquinho.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPerfilDTO {

    private UUID id;
    private String    nome;
    private String    email;
    private LocalDateTime createdAt;

    public static UsuarioPerfilDTO from(Usuario u) {
        return UsuarioPerfilDTO.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .createdAt(u.getCreatedAt())
                .build();
    }
}