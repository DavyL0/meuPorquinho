package com.meu.porquinho.controller;

import com.meu.porquinho.dto.usuario.LoginRequest;
import com.meu.porquinho.dto.usuario.LoginResponse;
import com.meu.porquinho.dto.lancamento.RegistroRequest;
import com.meu.porquinho.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public void registro(@RequestBody @Valid RegistroRequest req) {
        authService.registrar(req);
    }
}
