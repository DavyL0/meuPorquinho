package com.meu.porquinho.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * Converte o filtro "mesAno" (formato YYYY-MM) em faixa de datas,
 * permitindo que as queries usem o indice de tb_lancamento.data
 * em vez de funcoes como TO_CHAR, que anulam o indice.
 */
public final class DataUtils {

    /** Sentinelas: usadas quando mesAno nao e informado (a query nunca recebe data nula). */
    private static final LocalDate INICIO_MINIMO = LocalDate.of(1970, 1, 1);
    private static final LocalDate FIM_MAXIMO    = LocalDate.of(9999, 1, 1);

    private DataUtils() {
    }

    /** Primeiro dia do mes (inclusivo); sentinela 1970-01-01 se mesAno for null. */
    public static LocalDate inicioDe(String mesAno) {
        return mesAno == null ? INICIO_MINIMO : parse(mesAno).atDay(1);
    }

    /** Primeiro dia do mes seguinte (exclusivo); sentinela 9999-01-01 se mesAno for null. */
    public static LocalDate fimExclusivoDe(String mesAno) {
        return mesAno == null ? FIM_MAXIMO : parse(mesAno).plusMonths(1).atDay(1);
    }

    private static YearMonth parse(String mesAno) {
        try {
            return YearMonth.parse(mesAno);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "mesAno invalido (use o formato YYYY-MM): " + mesAno);
        }
    }
}
