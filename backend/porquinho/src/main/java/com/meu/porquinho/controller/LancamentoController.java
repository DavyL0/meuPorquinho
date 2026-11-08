package com.meu.porquinho.controller;

import com.meu.porquinho.dto.lancamento.LancamentoDTO;
import com.meu.porquinho.model.TipoLancamento;
import com.meu.porquinho.service.LancamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService service;

    @GetMapping
    public Page<LancamentoDTO> listar(
            @RequestParam(required = false) TipoLancamento tipo,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String mesAno,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails user
    ) {
        return service.filtrar(user.getUsername(), tipo, categoria, mesAno,
                PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public LancamentoDTO buscarPorId(@PathVariable UUID id,
                                     @AuthenticationPrincipal UserDetails user) {
        return service.buscarPorId(user.getUsername(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LancamentoDTO criar(@RequestBody @Valid LancamentoDTO dto,
                               @AuthenticationPrincipal UserDetails user) {
        return service.criar(user.getUsername(), dto);
    }

    @PutMapping("/{id}")
    public LancamentoDTO atualizar(@PathVariable UUID id,
                                   @RequestBody @Valid LancamentoDTO dto,
                                   @AuthenticationPrincipal UserDetails user) {
        return service.atualizar(user.getUsername(), id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id,
                        @AuthenticationPrincipal UserDetails user) {
        service.remover(user.getUsername(), id);
    }
}
