package com.meu.porquinho.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlterarSenhaRequest {
    @NotBlank
    private String senhaAtual;

    @NotBlank
    @Size(min = 6)
    private String novaSenha;
}
