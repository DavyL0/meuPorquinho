package com.meu.porquinho.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExcluirContaRequest {

    @NotBlank
    private String senha;
}
