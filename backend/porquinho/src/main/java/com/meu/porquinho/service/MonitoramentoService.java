package com.meu.porquinho.service;

import com.meu.porquinho.model.DirecaoAlerta;
import com.meu.porquinho.model.Investimento;
import com.meu.porquinho.repository.InvestimentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.math.BigDecimal.valueOf;

/**
 * Verifica periodicamente as cotações dos investimentos com alerta ativo
 * e dispara a notificação por e-mail quando o percentual é atingido.
 * O alerta dispara uma única vez (pode ser reativado pelo usuário).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoramentoService {

    private final InvestimentoRepository investimentoRepo;
    private final BinanceService binanceService;
    private final EmailService emailService;

    @Scheduled(
            fixedDelayString = "${monitoramento.intervalo-ms:60000}",
            initialDelayString = "${monitoramento.atraso-inicial-ms:15000}"
    )
    @Transactional
    public void verificarAlertas() {
        List<Investimento> ativos = investimentoRepo.findByAtivoTrue();
        if (ativos.isEmpty()) {
            return;
        }

        Set<String> simbolos = ativos.stream()
                .map(Investimento::getSimbolo)
                .collect(Collectors.toSet());
        Map<String, BigDecimal> precos = binanceService.cotacoes(simbolos);
        if (precos.isEmpty()) {
            log.warn("Monitoramento: sem cotações da Binance nesta rodada.");
            return;
        }

        for (Investimento inv : ativos) {
            BigDecimal atual = precos.get(inv.getSimbolo());
            if (atual == null) {
                continue;
            }

            BigDecimal variacao = atual
                    .subtract(inv.getValorInicial())
                    .divide(inv.getValorInicial(), 6, RoundingMode.HALF_UP)
                    .multiply(valueOf(100));

            if (deveDisparar(inv, variacao)) {
                log.info("Alerta disparado: {} variação {}% (limite {}%) — usuário {}",
                        inv.getSimbolo(), variacao.setScale(2, RoundingMode.HALF_UP),
                        inv.getPercentualAlerta(), inv.getUsuario().getEmail());

                emailService.enviarAlertaInvestimento(
                        inv, inv.getUsuario().getEmail(), atual, variacao);

                inv.setNotificadoEm(LocalDateTime.now());
                inv.setAtivo(false);
                investimentoRepo.save(inv);
            }
        }
    }

    private boolean deveDisparar(Investimento inv, BigDecimal variacao) {
        BigDecimal limite = inv.getPercentualAlerta();
        return switch (inv.getDirecao()) {
            case ALTA  -> variacao.compareTo(limite) >= 0;
            case QUEDA -> variacao.compareTo(limite.negate()) <= 0;
            case AMBOS -> variacao.abs().compareTo(limite) >= 0;
        };
    }
}
