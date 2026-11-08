package com.meu.porquinho.service;

import com.meu.porquinho.dto.usuario.AlterarSenhaRequest;
import com.meu.porquinho.dto.usuario.AtualizarPerfilRequest;
import com.meu.porquinho.dto.usuario.UsuarioEstatisticaDTO;
import com.meu.porquinho.dto.usuario.UsuarioPerfilDTO;
import com.meu.porquinho.model.Usuario;
import com.meu.porquinho.repository.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    @Lazy
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }

    public UsuarioPerfilDTO perfil(String email) {
        Usuario usuario = buscarPorEmail(email);
        return UsuarioPerfilDTO.from(usuario);
    }

    public UsuarioEstatisticaDTO estatistica(String email) {
        return usuarioRepo.findEstatisticaByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    @Transactional
    public UsuarioPerfilDTO atualizar(String email, AtualizarPerfilRequest req) {
        Usuario usuario = buscarPorEmail(email);

        if (!usuario.getEmail().equals(req.getEmail())
                && usuarioRepo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "E-mail já está em uso");
        }

        usuario.setNome(req.getNome());
        usuario.setEmail(req.getEmail());

        return UsuarioPerfilDTO.from(usuarioRepo.save(usuario));
    }

    @Transactional
    public void alterarSenha(String email, AlterarSenhaRequest req) {
        Usuario usuario = buscarPorEmail(email);

        if (!encoder.matches(req.getSenhaAtual(), usuario.getSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }
        if (req.getSenhaAtual().equals(req.getNovaSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nova senha não pode ser igual à atual");
        }

        usuario.setSenha(encoder.encode(req.getNovaSenha()));
        usuarioRepo.save(usuario);
    }

    @Transactional
    public void excluirConta(String email, String senhaConfirmacao) {
        Usuario usuario = buscarPorEmail(email);

        if (!encoder.matches(senhaConfirmacao, usuario.getSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Senha incorreta");
        }

        usuarioRepo.delete(usuario);
    }

    private Usuario buscarPorEmail(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }
}
