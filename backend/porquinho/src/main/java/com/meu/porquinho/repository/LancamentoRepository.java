package com.meu.porquinho.repository;

import com.meu.porquinho.dto.resumo.ResumoCategoriaDTO;
import com.meu.porquinho.dto.resumo.ResumoDTO;
import com.meu.porquinho.dto.resumo.ResumoMensalDTO;
import com.meu.porquinho.model.Lancamento;
import com.meu.porquinho.model.TipoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, UUID> {

    // busca segura por id + dono
    Optional<Lancamento> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    // filtro completo; a faixa [inicio, fim) usa o indice idx_lancamento_data.
    // datas nunca nulas: o service usa sentinelas quando mesAno nao e informado
    // (parametro nulo faria o PostgreSQL falhar com "could not determine data type")
    @Query("""
        SELECT l FROM Lancamento l
        WHERE l.usuario.id = :uid
          AND (:tipo IS NULL OR l.tipo = :tipo)
          AND (:categoria IS NULL OR l.categoria = :categoria)
          AND l.data >= :inicio
          AND l.data <  :fim
        ORDER BY l.data DESC
    """)
    Page<Lancamento> filtrar(
            @Param("uid")       UUID uid,
            @Param("tipo")      TipoLancamento tipo,
            @Param("categoria") String categoria,
            @Param("inicio")    LocalDate inicio,
            @Param("fim")       LocalDate fim,
            Pageable pageable
    );

    // totais para o resumo
    @Query("""
        SELECT NEW com.meu.porquinho.dto.resumo.ResumoDTO(
            COALESCE(SUM(CASE WHEN l.tipo = 'ENTRADA'      THEN l.valor END), 0),
            COALESCE(SUM(CASE WHEN l.tipo = 'GASTO'        THEN l.valor END), 0),
            COALESCE(SUM(CASE WHEN l.tipo = 'INVESTIMENTO' THEN l.valor END), 0)
        )
        FROM Lancamento l
        WHERE l.usuario.id = :uid
          AND l.data >= :inicio
          AND l.data <  :fim
    """)
    ResumoDTO calcularResumo(
            @Param("uid")    UUID uid,
            @Param("inicio") LocalDate inicio,
            @Param("fim")    LocalDate fim
    );

    // gastos agrupados por categoria
    @Query("""
        SELECT NEW com.meu.porquinho.dto.resumo.ResumoCategoriaDTO(
            l.categoria,
            SUM(l.valor),
            COUNT(l)
        )
        FROM Lancamento l
        WHERE l.usuario.id = :uid
          AND l.tipo = 'GASTO'
          AND l.data >= :inicio
          AND l.data <  :fim
        GROUP BY l.categoria
        ORDER BY SUM(l.valor) DESC
    """)
    List<ResumoCategoriaDTO> gastosPorCategoria(
            @Param("uid")    UUID uid,
            @Param("inicio") LocalDate inicio,
            @Param("fim")    LocalDate fim
    );

    // evolução mês a mês
    @Query("""
        SELECT NEW com.meu.porquinho.dto.resumo.ResumoMensalDTO(
            TO_CHAR(l.data, 'YYYY-MM'),
            COALESCE(SUM(CASE WHEN l.tipo = 'ENTRADA'      THEN l.valor END), 0),
            COALESCE(SUM(CASE WHEN l.tipo = 'GASTO'        THEN l.valor END), 0),
            COALESCE(SUM(CASE WHEN l.tipo = 'INVESTIMENTO' THEN l.valor END), 0)
        )
        FROM Lancamento l
        WHERE l.usuario.id = :uid
          AND l.data >= :inicio
        GROUP BY TO_CHAR(l.data, 'YYYY-MM')
        ORDER BY TO_CHAR(l.data, 'YYYY-MM')
    """)
    List<ResumoMensalDTO> evolucaoMensal(
            @Param("uid")    UUID uid,
            @Param("inicio") LocalDate inicio
    );
}
