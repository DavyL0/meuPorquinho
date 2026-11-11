package com.meu.porquinho.service;

import com.meu.porquinho.dto.investimento.InvestimentoRequest;
import com.meu.porquinho.dto.investimento.InvestimentoResponse;
import com.meu.porquinho.model.Investimento;
import com.meu.porquinho.model.Usuario;
import com.meu.porquinho.repository.InvestimentoRepository;
import com.meu.porquinho.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.math.BigDecimal.valueOf;

@Service
@RequiredArgsConstructor
@Transactional
public class InvestimentoService {

    private final InvestimentoRepository investimentoRepo;
    private final UsuarioRepository usuarioRepo;
    private final BinanceService binanceService;

    @Transactional(readOnly = true)
    public List<InvestimentoResponse> listar(String email) {
        Usuario usuario = buscarUsuario(email);
        List<Investimento> investimentos =
                investimentoRepo.findByUsuarioIdOrderByCreatedAtDesc(usuario.getId());

        Set<String> simbolos = investimentos.stream()
                .map(Investimento::getSimbolo)
                .collect(Collectors.toSet());
        Map<String, BigDecimal> precos = binanceService.cotacoes(simbolos);

        return investimentos.stream()
                .map(inv -> enriquecer(inv, precos.get(inv.getSimbolo())))
                .toList();
    }

    public InvestimentoResponse criar(String email, InvestimentoRequest req) {
        Usuario usuario = buscarUsuario(email);
        String simbolo = req.getSimbolo().toUpperCase();

        if (!binanceService.parExiste(simbolo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Par " + simbolo + " não encontrado na Binance");
        }

        Investimento inv = Investimento.builder()
                .usuario(usuario)
                .simbolo(simbolo)
                .quantidade(req.getQuantidade())
                .valorInicial(req.getValorInicial())
                .percentualAlerta(req.getPercentualAlerta())
                .direcao(req.getDirecao())
                .ativo(true)
                .build();

        return enriquecer(investimentoRepo.save(inv), binanceService.cotacao(simbolo));
    }

    public void remover(String email, UUID id) {
        Investimento inv = buscarPorIdEUsuario(email, id);
        investimentoRepo.delete(inv);
    }

    /** Rearma um alerta já disparado (ativo = true, limpa notificado_em). */
    public InvestimentoResponse reativar(String email, UUID id) {
        Investimento inv = buscarPorIdEUsuario(email, id);
        inv.setAtivo(true);
        inv.setNotificadoEm(null);
        return enriquecer(investimentoRepo.save(inv),
                binanceService.cotacao(inv.getSimbolo()));
    }

    // helpers privados

    private InvestimentoResponse enriquecer(Investimento inv, BigDecimal cotacaoAtual) {
        InvestimentoResponse resp = InvestimentoResponse.from(inv);
        if (cotacaoAtual != null) {
            resp.setCotacaoAtual(cotacaoAtual);
            resp.setValorTotalAtual(cotacaoAtual.multiply(inv.getQuantidade()));
            resp.setVariacaoPercentual(cotacaoAtual
                    .subtract(inv.getValorInicial())
                    .divide(inv.getValorInicial(), 6, RoundingMode.HALF_UP)
                    .multiply(valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP));
        }
        return resp;
    }

    private Investimento buscarPorIdEUsuario(String email, UUID id) {
        Usuario usuario = buscarUsuario(email);
        return investimentoRepo.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Investimento não encontrado"));
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }
}
