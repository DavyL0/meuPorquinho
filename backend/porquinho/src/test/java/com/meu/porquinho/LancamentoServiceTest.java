package com.meu.porquinho;

import com.meu.porquinho.dto.lancamento.LancamentoDTO;
import com.meu.porquinho.model.Lancamento;
import com.meu.porquinho.model.TipoLancamento;
import com.meu.porquinho.model.Usuario;
import com.meu.porquinho.repository.LancamentoRepository;
import com.meu.porquinho.repository.UsuarioRepository;
import com.meu.porquinho.service.LancamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LancamentoServiceTest {

    @InjectMocks
    private LancamentoService lancamentoService;

    @Mock
    private LancamentoRepository lancamentoRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    private Usuario usuario;
    private Lancamento lancamento;
    private LancamentoDTO dto;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Davy")
                .email("davy@email.com")
                .senha("senha123")
                .build();

        lancamento = Lancamento.builder()
                .id(UUID.randomUUID())
                .usuario(usuario)
                .tipo(TipoLancamento.GASTO)
                .descricao("Supermercado")
                .valor(new BigDecimal("250.00"))
                .categoria("Alimentação")
                .data(LocalDate.now())
                .build();

        dto = LancamentoDTO.builder()
                .tipo(TipoLancamento.GASTO)
                .descricao("Supermercado")
                .valor(new BigDecimal("250.00"))
                .categoria("Alimentação")
                .data(LocalDate.now())
                .build();
    }

    // ── CRIAR ────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar lançamento do tipo GASTO com sucesso")
    void deveCriarGastoComSucesso() {
        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.save(any(Lancamento.class)))
                .thenReturn(lancamento);

        LancamentoDTO resultado = lancamentoService.criar(usuario.getEmail(), dto);

        assertNotNull(resultado);
        assertEquals(TipoLancamento.GASTO, resultado.getTipo());
        assertEquals("Supermercado", resultado.getDescricao());
        assertEquals(new BigDecimal("250.00"), resultado.getValor());
        verify(lancamentoRepo, times(1)).save(any(Lancamento.class));
    }

    @Test
    @DisplayName("Deve criar lançamento do tipo ENTRADA sem categoria")
    void deveCriarEntradaSemCategoria() {
        dto.setTipo(TipoLancamento.ENTRADA);
        dto.setCategoria(null);

        Lancamento entrada = Lancamento.builder()
                .id(UUID.randomUUID())
                .usuario(usuario)
                .tipo(TipoLancamento.ENTRADA)
                .descricao("Salário")
                .valor(new BigDecimal("5000.00"))
                .categoria("Entrada")
                .data(LocalDate.now())
                .build();

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.save(any())).thenReturn(entrada);

        LancamentoDTO resultado = lancamentoService.criar(usuario.getEmail(), dto);

        assertEquals("Entrada", resultado.getCategoria());
        verify(lancamentoRepo).save(any(Lancamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar GASTO sem categoria")
    void deveLancarExcecaoGastoSemCategoria() {
        dto.setCategoria(null);

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> lancamentoService.criar(usuario.getEmail(), dto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(lancamentoRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado ao criar")
    void deveLancarExcecaoUsuarioNaoEncontradoAoCriar() {
        when(usuarioRepo.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> lancamentoService.criar("inexistente@email.com", dto)
        );

        verify(lancamentoRepo, never()).save(any());
    }

    // ── FILTRAR ──────────────────────────────────────────────

    @Test
    @DisplayName("Deve filtrar lançamentos com paginação")
    void deveFiltrarComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lancamento> page = new PageImpl<>(List.of(lancamento));

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.filtrar(
                eq(usuario.getId()), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        Page<LancamentoDTO> resultado = lancamentoService.filtrar(
                usuario.getEmail(), null, null, null, pageable
        );

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Supermercado", resultado.getContent().getFirst().getDescricao());
    }

    @Test
    @DisplayName("Deve filtrar lançamentos por tipo e mês")
    void deveFiltrarPorTipoEMes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lancamento> page = new PageImpl<>(List.of(lancamento));

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.filtrar(
                eq(usuario.getId()),
                eq(TipoLancamento.GASTO),
                isNull(),
                eq("2024-06"),
                eq(pageable)))
                .thenReturn(page);

        Page<LancamentoDTO> resultado = lancamentoService.filtrar(
                usuario.getEmail(), TipoLancamento.GASTO, null, "2024-06", pageable
        );

        assertEquals(1, resultado.getTotalElements());
    }

    // ── ATUALIZAR ────────────────────────────────────────────

    @Test
    @DisplayName("Deve atualizar lançamento com sucesso")
    void deveAtualizarComSucesso() {
        dto.setDescricao("Farmácia");
        dto.setValor(new BigDecimal("80.00"));

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.findByIdAndUsuarioId(lancamento.getId(), usuario.getId()))
                .thenReturn(Optional.of(lancamento));
        when(lancamentoRepo.save(any())).thenReturn(lancamento);

        LancamentoDTO resultado = lancamentoService.atualizar(
                usuario.getEmail(), lancamento.getId(), dto
        );

        assertNotNull(resultado);
        verify(lancamentoRepo).save(any(Lancamento.class));
    }

    @Test
    @DisplayName("Deve lançar NOT_FOUND ao atualizar lançamento de outro usuário")
    void deveLancarNotFoundAoAtualizarLancamentoDeOutroUsuario() {
        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.findByIdAndUsuarioId(any(), any()))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> lancamentoService.atualizar(
                        usuario.getEmail(), UUID.randomUUID(), dto)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // ── REMOVER ──────────────────────────────────────────────

    @Test
    @DisplayName("Deve remover lançamento com sucesso")
    void deveRemoverComSucesso() {
        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.findByIdAndUsuarioId(lancamento.getId(), usuario.getId()))
                .thenReturn(Optional.of(lancamento));

        assertDoesNotThrow(
                () -> lancamentoService.remover(usuario.getEmail(), lancamento.getId())
        );

        verify(lancamentoRepo).delete(lancamento);
    }

    @Test
    @DisplayName("Deve lançar NOT_FOUND ao remover lançamento inexistente")
    void deveLancarNotFoundAoRemoverInexistente() {
        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(lancamentoRepo.findByIdAndUsuarioId(any(), any()))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> lancamentoService.remover(usuario.getEmail(), UUID.randomUUID())
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(lancamentoRepo, never()).delete(any());
    }
}