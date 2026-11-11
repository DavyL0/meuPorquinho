package com.meu.porquinho.controller;

import com.meu.porquinho.dto.investimento.InvestimentoRequest;
import com.meu.porquinho.dto.investimento.InvestimentoResponse;
import com.meu.porquinho.service.InvestimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/investimentos")
@RequiredArgsConstructor
public class InvestimentoController {

    private final InvestimentoService service;

    @GetMapping
    public List<InvestimentoResponse> listar(@AuthenticationPrincipal UserDetails user) {
        return service.listar(user.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvestimentoResponse criar(@RequestBody @Valid InvestimentoRequest req,
                                      @AuthenticationPrincipal UserDetails user) {
        return service.criar(user.getUsername(), req);
    }

    @PatchMapping("/{id}/reativar")
    public InvestimentoResponse reativar(@PathVariable UUID id,
                                         @AuthenticationPrincipal UserDetails user) {
        return service.reativar(user.getUsername(), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id,
                        @AuthenticationPrincipal UserDetails user) {
        service.remover(user.getUsername(), id);
    }
}
