package com.meu.porquinho.service;

import com.meu.porquinho.dto.lancamento.LancamentoDTO;
import com.meu.porquinho.model.Lancamento;
import com.meu.porquinho.model.TipoLancamento;
import com.meu.porquinho.model.Usuario;
import com.meu.porquinho.repository.LancamentoRepository;
import com.meu.porquinho.repository.UsuarioRepository;
import com.meu.porquinho.util.DataUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LancamentoService {

    private final LancamentoRepository lancamentoRepo;
    private final UsuarioRepository usuarioRepo;

    public Page<LancamentoDTO> filtrar(String email,
                                       TipoLancamento tipo,
                                       String categoria,
                                       String mesAno,
                                       Pageable pageable) {
        Usuario usuario = buscarUsuario(email);
        return lancamentoRepo
                .filtrar(usuario.getId(), tipo, categoria,
                        DataUtils.inicioDe(mesAno), DataUtils.fimExclusivoDe(mesAno),
                        pageable)
                .map(LancamentoDTO::from);
    }

    @Transactional
    public LancamentoDTO criar(String email, LancamentoDTO dto) {
        Usuario usuario = buscarUsuario(email);

        validarCategoria(dto);

        Lancamento lancamento = Lancamento.builder()
                .usuario(usuario)
                .tipo(dto.getTipo())
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .categoria(resolverCategoria(dto))
                .data(dto.getData())
                .build();

        return LancamentoDTO.from(lancamentoRepo.save(lancamento));
    }

    @Transactional
    public LancamentoDTO atualizar(String email, UUID id, LancamentoDTO dto) {
        Lancamento lancamento = buscarPorIdEUsuario(email, id);

        validarCategoria(dto);

        lancamento.setTipo(dto.getTipo());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setValor(dto.getValor());
        lancamento.setCategoria(resolverCategoria(dto));
        lancamento.setData(dto.getData());

        return LancamentoDTO.from(lancamentoRepo.save(lancamento));
    }

    @Transactional
    public void remover(String email, UUID id) {
        Lancamento lancamento = buscarPorIdEUsuario(email, id);
        lancamentoRepo.delete(lancamento);
    }

    public LancamentoDTO buscarPorId(String email, UUID id) {
        return LancamentoDTO.from(buscarPorIdEUsuario(email, id));
    }


    private Lancamento buscarPorIdEUsuario(String email, UUID id) {
        Usuario usuario = buscarUsuario(email);
        return lancamentoRepo.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lançamento não encontrado"));
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }

    private void validarCategoria(LancamentoDTO dto) {
        if (dto.getTipo() == TipoLancamento.GASTO && dto.getCategoria() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Categoria obrigatória para gastos");
        }
    }

    private String resolverCategoria(LancamentoDTO dto) {
        return switch (dto.getTipo()) {
            case GASTO        -> dto.getCategoria();
            case INVESTIMENTO -> "Investimento";
            case ENTRADA      -> "Entrada";
        };
    }
}
