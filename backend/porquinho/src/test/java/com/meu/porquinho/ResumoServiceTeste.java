package com.meu.porquinho;

import com.meu.porquinho.dto.lancamento.ResumoMensalDTO;
import com.meu.porquinho.dto.resumo.ResumoCategoriaDTO;
import com.meu.porquinho.dto.resumo.ResumoComparativoDTO;
import com.meu.porquinho.dto.resumo.ResumoDTO;
import com.meu.porquinho.model.Usuario;
import com.meu.porquinho.repository.LancamentoRepository;
import com.meu.porquinho.repository.UsuarioRepository;
import com.meu.porquinho.service.ResumoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumoServiceTest {

    @InjectMocks
    private ResumoService resumoService;

    @Mock
    private LancamentoRepository lancamentoRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .email("davy@email.com")
                .nome("Davy")
                .build();
    }

    @Test
    @DisplayName("Deve retornar resumo geral do mês")
    void deveRetornarResumoGeral() {
        ResumoDTO resumoMock = new ResumoDTO(
                new BigDecimal("5000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("500.00")
        );

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.calcularResumo(usuario.getId(), "2024-06"))
                .thenReturn(resumoMock);

        ResumoDTO resultado = resumoService.resumoGeral(usuario.getEmail(), "2024-06");

        assertEquals(new BigDecimal("5000.00"), resultado.getTotalEntradas());
        assertEquals(new BigDecimal("1500.00"), resultado.getTotalGastos());
        assertEquals(new BigDecimal("500.00"),  resultado.getTotalInvestimentos());
        assertEquals(new BigDecimal("3000.00"), resultado.getSaldo());
    }

    @Test
    @DisplayName("Deve normalizar resumo nulo retornando zeros")
    void deveNormalizarResumoNulo() {
        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.calcularResumo(any(), any()))
                .thenReturn(null);

        ResumoDTO resultado = resumoService.resumoGeral(usuario.getEmail(), "2024-06");

        assertEquals(BigDecimal.ZERO, resultado.getTotalEntradas());
        assertEquals(BigDecimal.ZERO, resultado.getTotalGastos());
        assertEquals(BigDecimal.ZERO, resultado.getTotalInvestimentos());
    }

    @Test
    @DisplayName("Deve retornar gastos agrupados por categoria")
    void deveRetornarGastosPorCategoria() {
        List<ResumoCategoriaDTO> categorias = List.of(
                new ResumoCategoriaDTO("Alimentação", new BigDecimal("800.00"), 10L),
                new ResumoCategoriaDTO("Transporte",  new BigDecimal("300.00"),  5L)
        );

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.gastosPorCategoria(usuario.getId(), "2024-06"))
                .thenReturn(categorias);

        List<ResumoCategoriaDTO> resultado =
                resumoService.gastosPorCategoria(usuario.getEmail(), "2024-06");

        assertEquals(2, resultado.size());
        assertEquals("Alimentação", resultado.get(0).getCategoria());
        assertEquals(new BigDecimal("800.00"), resultado.get(0).getTotal());
    }

    @Test
    @DisplayName("Deve retornar evolução dos últimos 6 meses")
    void deveRetornarEvolucaoMensal() {
        List<ResumoMensalDTO> evolucao = List.of(
                new ResumoMensalDTO("2024-01",
                        new BigDecimal("4000"), new BigDecimal("2000"), new BigDecimal("500")),
                new ResumoMensalDTO("2024-02",
                        new BigDecimal("4500"), new BigDecimal("1800"), new BigDecimal("700"))
        );

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.evolucaoMensal(eq(usuario.getId()), any(LocalDate.class)))
                .thenReturn(evolucao);

        List<ResumoMensalDTO> resultado =
                resumoService.evolucaoMensal(usuario.getEmail(), 6);

        assertEquals(2, resultado.size());
        assertEquals("2024-01", resultado.get(0).getMesAno());
        assertEquals(new BigDecimal("1500"), resultado.get(0).getSaldo());
    }

    @Test
    @DisplayName("Deve calcular variação positiva entre meses")
    void deveCalcularComparativoComVariacaoPositiva() {
        ResumoDTO mesAtual    = new ResumoDTO(
                new BigDecimal("5000"), new BigDecimal("1000"), new BigDecimal("500"));
        ResumoDTO mesAnterior = new ResumoDTO(
                new BigDecimal("4000"), new BigDecimal("1200"), new BigDecimal("400"));

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.calcularResumo(usuario.getId(), "2024-06"))
                .thenReturn(mesAtual);
        when(lancamentoRepo.calcularResumo(usuario.getId(), "2024-05"))
                .thenReturn(mesAnterior);

        ResumoComparativoDTO resultado = resumoService.comparativo(
                usuario.getEmail(), "2024-06", "2024-05"
        );

        // gastos caíram: variação negativa esperada
        assertTrue(resultado.getVariacaoGastos().compareTo(BigDecimal.ZERO) < 0);
        // entradas subiram: variação positiva esperada
        assertTrue(resultado.getVariacaoEntradas().compareTo(BigDecimal.ZERO) > 0);
    }
}