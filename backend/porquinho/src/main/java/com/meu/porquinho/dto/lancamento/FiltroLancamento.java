package com.meu.porquinho.dto.lancamento;

import com.meu.porquinho.model.TipoLancamento;
import lombok.Data;


@Data
public class FiltroLancamento {
    private TipoLancamento tipo;
    private String categoria;
    private String mesAno;   // formato "2024-06"
    private int page = 0;
    private int size = 20;
}
