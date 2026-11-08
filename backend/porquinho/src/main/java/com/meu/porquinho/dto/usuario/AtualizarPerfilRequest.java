package com.meu.porquinho.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AtualizarPerfilRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String nome;

    @NotBlank @Email
    private String email;
}
