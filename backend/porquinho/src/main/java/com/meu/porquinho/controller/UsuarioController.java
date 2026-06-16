package com.meu.porquinho.controller;

import com.meu.porquinho.dto.usuario.AlterarSenhaRequest;
import com.meu.porquinho.dto.usuario.AtualizarPerfilRequest;
import com.meu.porquinho.dto.usuario.UsuarioEstatisticaDTO;
import com.meu.porquinho.dto.usuario.UsuarioPerfilDTO;
import com.meu.porquinho.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public UsuarioPerfilDTO perfil(
            @AuthenticationPrincipal UserDetails user
    ) {
        return usuarioService.perfil(user.getUsername());
    }

    @GetMapping("/me/estatisticas")
    public UsuarioEstatisticaDTO estatisticas(
            @AuthenticationPrincipal UserDetails user
    ) {
        return usuarioService.estatistica(user.getUsername());
    }

    @PutMapping("/me")
    public UsuarioPerfilDTO atualizar(
            @RequestBody @Valid AtualizarPerfilRequest req,
            @AuthenticationPrincipal UserDetails user
    ) {
        return usuarioService.atualizar(user.getUsername(), req);
    }

    @PatchMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(
            @RequestBody @Valid AlterarSenhaRequest req,
            @AuthenticationPrincipal UserDetails user
    ) {
        usuarioService.alterarSenha(user.getUsername(), req);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirConta(
            @RequestParam String senha,
            @AuthenticationPrincipal UserDetails user
    ) {
        usuarioService.excluirConta(user.getUsername(), senha);
    }
}
