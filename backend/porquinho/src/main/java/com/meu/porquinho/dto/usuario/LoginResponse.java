package com.meu.porquinho.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String nome;
    private String email;
    private long expiresIn;
}
