package com.meu.porquinho.controller;

import com.meu.porquinho.dto.resumo.ResumoCategoriaDTO;
import com.meu.porquinho.dto.resumo.ResumoComparativoDTO;
import com.meu.porquinho.dto.resumo.ResumoDTO;
import com.meu.porquinho.dto.resumo.ResumoMensalDTO;
import com.meu.porquinho.service.ResumoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resumo")
@RequiredArgsConstructor
public class ResumoController {

    private final ResumoService resumoService;

    @GetMapping
    public ResumoDTO resumo(
            @RequestParam(required = false) String mesAno,
            @AuthenticationPrincipal UserDetails user
    ) {
        return resumoService.resumoGeral(user.getUsername(), mesAno);
    }

    @GetMapping("/categorias")
    public List<ResumoCategoriaDTO> porCategoria(
            @RequestParam(required = false) String mesAno,
            @AuthenticationPrincipal UserDetails user
    ) {
        return resumoService.gastosPorCategoria(user.getUsername(), mesAno);
    }

    @GetMapping("/evolucao")
    public List<ResumoMensalDTO> evolucao(
            @RequestParam(defaultValue = "6") int meses,
            @AuthenticationPrincipal UserDetails user
    ) {
        return resumoService.evolucaoMensal(user.getUsername(), meses);
    }

    @GetMapping("/comparativo")
    public ResumoComparativoDTO comparativo(
            @RequestParam String mesAtual,
            @RequestParam String mesAnterior,
            @AuthenticationPrincipal UserDetails user
    ) {
        return resumoService.comparativo(user.getUsername(), mesAtual, mesAnterior);
    }
}
