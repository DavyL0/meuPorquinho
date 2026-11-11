package com.meu.porquinho.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Integração com a API pública da Binance (sem autenticação).
 * Documentação: https://developers.binance.com/docs/binance-spot-api-docs
 */
@Slf4j
@Service
public class BinanceService {

    private final RestClient client;
    private final String baseUrl;

    public BinanceService(@Value("${binance.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = RestClient.create(baseUrl);
    }

    /** Preço atual de um único par, ex: BTCBRL. Null se o par não existir. */
    public BigDecimal cotacao(String simbolo) {
        return cotacoes(List.of(simbolo)).get(simbolo.toUpperCase());
    }

    /** Preços atuais de vários pares em uma única chamada. */
    public Map<String, BigDecimal> cotacoes(Collection<String> simbolos) {
        if (simbolos == null || simbolos.isEmpty()) {
            return Map.of();
        }
        String joined = simbolos.stream()
                .map(s -> "\"" + s.toUpperCase() + "\"")
                .distinct()
                .collect(Collectors.joining(",", "[", "]"));

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/api/v3/ticker/price")
                .queryParam("symbols", joined)
                .encode()
                .build()
                .toUri();

        try {
            TickerPrice[] resposta = client.get()
                    .uri(uri)
                    .retrieve()
                    .body(TickerPrice[].class);

            Map<String, BigDecimal> precos = new HashMap<>();
            if (resposta != null) {
                for (TickerPrice t : resposta) {
                    precos.put(t.symbol(), new BigDecimal(t.price()));
                }
            }
            return precos;
        } catch (Exception e) {
            log.error("Falha ao consultar cotações na Binance: {}", e.getMessage());
            return Map.of();
        }
    }

    /** Verifica se um par existe na Binance. */
    public boolean parExiste(String simbolo) {
        return cotacao(simbolo) != null;
    }

    private record TickerPrice(String symbol, String price) {}
}
