package com.meu.porquinho.service;

import com.meu.porquinho.dto.resumo.ResumoCategoriaDTO;
import com.meu.porquinho.dto.resumo.ResumoComparativoDTO;
import com.meu.porquinho.dto.resumo.ResumoDTO;
import com.meu.porquinho.dto.resumo.ResumoMensalDTO;
import com.meu.porquinho.model.Usuario;
import com.meu.porquinho.repository.LancamentoRepository;
import com.meu.porquinho.repository.UsuarioRepository;
import com.meu.porquinho.util.DataUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumoService {

    private final LancamentoRepository lancamentoRepo;
    private final UsuarioRepository usuarioRepo;

    public ResumoDTO resumoGeral(String email, String mesAno) {
        UUID usuarioId = buscarUsuarioId(email);
        ResumoDTO resumo = lancamentoRepo.calcularResumo(usuarioId,
                DataUtils.inicioDe(mesAno), DataUtils.fimExclusivoDe(mesAno));
        return normalizar(resumo);
    }

    public List<ResumoCategoriaDTO> gastosPorCategoria(String email, String mesAno) {
        UUID usuarioId = buscarUsuarioId(email);
        return lancamentoRepo.gastosPorCategoria(usuarioId,
                DataUtils.inicioDe(mesAno), DataUtils.fimExclusivoDe(mesAno));
    }

    public List<ResumoMensalDTO> evolucaoMensal(String email, int ultimosMeses) {
        UUID usuarioId = buscarUsuarioId(email);
        LocalDate inicio = LocalDate.now().minusMonths(ultimosMeses - 1L)
                .withDayOfMonth(1);
        return lancamentoRepo.evolucaoMensal(usuarioId, inicio);
    }

    public ResumoComparativoDTO comparativo(String email,
                                            String mesAtual,
                                            String mesAnterior) {
        UUID usuarioId = buscarUsuarioId(email);

        ResumoDTO atual    = normalizar(lancamentoRepo.calcularResumo(usuarioId,
                DataUtils.inicioDe(mesAtual), DataUtils.fimExclusivoDe(mesAtual)));
        ResumoDTO anterior = normalizar(lancamentoRepo.calcularResumo(usuarioId,
                DataUtils.inicioDe(mesAnterior), DataUtils.fimExclusivoDe(mesAnterior)));

        return ResumoComparativoDTO.builder()
                .mesAtual(atual)
                .mesAnterior(anterior)
                .variacaoGastos(calcularVariacao(anterior.getTotalGastos(),
                        atual.getTotalGastos()))
                .variacaoEntradas(calcularVariacao(anterior.getTotalEntradas(),
                        atual.getTotalEntradas()))
                .variacaoSaldo(calcularVariacao(anterior.getSaldo(), atual.getSaldo()))
                .build();
    }

    // helpers privados

    private UUID buscarUsuarioId(String email) {
        return usuarioRepo.findByEmail(email)
                .map(Usuario::getId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }

    private ResumoDTO normalizar(ResumoDTO dto) {
        if (dto == null) return new ResumoDTO(ZERO, ZERO, ZERO);
        return new ResumoDTO(
                nvl(dto.getTotalEntradas()),
                nvl(dto.getTotalGastos()),
                nvl(dto.getTotalInvestimentos())
        );
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal calcularVariacao(BigDecimal anterior, BigDecimal atual) {
        if (anterior == null || anterior.compareTo(ZERO) == 0) return null;
        return atual.subtract(anterior)
                .divide(anterior, 4, HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, HALF_UP);
    }
}
