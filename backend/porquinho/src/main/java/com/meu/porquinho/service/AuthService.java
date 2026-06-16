package com.meu.porquinho.service;

import com.meu.porquinho.dto.usuario.AlterarSenhaRequest;
import com.meu.porquinho.dto.usuario.LoginRequest;
import com.meu.porquinho.dto.usuario.LoginResponse;
import com.meu.porquinho.dto.lancamento.RegistroRequest;
import com.meu.porquinho.model.Usuario;
import com.meu.porquinho.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha())
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }

        Usuario usuario = buscarPorEmail(req.getEmail());
        return montarResposta(usuario);
    }

    public void registrar(RegistroRequest req) {
        if (usuarioRepo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        usuarioRepo.save(
                Usuario.builder()
                        .nome(req.getNome())
                        .email(req.getEmail())
                        .senha(encoder.encode(req.getSenha()))
                        .build()
        );
    }

    public LoginResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Token ausente");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extrairEmail(token);
        Usuario usuario = buscarPorEmail(email);
        return montarResposta(usuario);
    }

    public void alterarSenha(String email, AlterarSenhaRequest req) {
        Usuario usuario = buscarPorEmail(email);

        if (!encoder.matches(req.getSenhaAtual(), usuario.getSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }
        usuario.setSenha(encoder.encode(req.getNovaSenha()));
        usuarioRepo.save(usuario);
    }

    private LoginResponse montarResposta(Usuario usuario) {
        String token = jwtService.gerarToken((UserDetails) usuario);
        return new LoginResponse(token, usuario.getNome(),
                usuario.getEmail(), 86400000L);
    }

    private Usuario buscarPorEmail(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }
}
