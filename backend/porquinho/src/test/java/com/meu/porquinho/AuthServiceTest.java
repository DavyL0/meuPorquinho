package com.meu.porquinho;

import com.meu.porquinho.dto.lancamento.RegistroRequest;
import com.meu.porquinho.dto.usuario.AlterarSenhaRequest;
import com.meu.porquinho.dto.usuario.LoginRequest;
import com.meu.porquinho.dto.usuario.LoginResponse;
import com.meu.porquinho.model.Usuario;
import com.meu.porquinho.repository.UsuarioRepository;
import com.meu.porquinho.service.AuthService;
import com.meu.porquinho.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepo;
    @Mock private PasswordEncoder encoder;
    @Mock private AuthenticationManager authManager;
    @Mock private JwtService jwtService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Davy")
                .email("davy@email.com")
                .senha("$2a$10$hashbcrypt")
                .build();
    }

    @Test
    @DisplayName("Deve realizar login e retornar token")
    void deveRealizarLoginComSucesso() {
        LoginRequest req = new LoginRequest();
        req.setEmail("davy@email.com");
        req.setSenha("senha123");

        when(usuarioRepo.findByEmail(req.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(jwtService.gerarToken(usuario))
                .thenReturn("jwt.token.aqui");

        LoginResponse response = authService.login(req);

        assertEquals("jwt.token.aqui", response.getToken());
        assertEquals("Davy",           response.getNome());
        assertEquals("davy@email.com", response.getEmail());
        verify(authManager).authenticate(any());
    }

    @Test
    @DisplayName("Deve lançar UNAUTHORIZED para credenciais inválidas")
    void deveLancarUnauthorizedParaCredenciaisInvalidas() {
        LoginRequest req = new LoginRequest();
        req.setEmail("davy@email.com");
        req.setSenha("senhaErrada");

        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authManager).authenticate(any());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(req)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        verify(jwtService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void deveRegistrarNovoUsuario() {
        RegistroRequest req = new RegistroRequest();
        req.setNome("Davy");
        req.setEmail("novo@email.com");
        req.setSenha("senha123");

        when(usuarioRepo.existsByEmail(req.getEmail())).thenReturn(false);
        when(encoder.encode(req.getSenha())).thenReturn("$2a$10$hash");
        when(usuarioRepo.save(any())).thenReturn(usuario);

        assertDoesNotThrow(() -> authService.registrar(req));

        verify(usuarioRepo).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar CONFLICT ao registrar e-mail já existente")
    void deveLancarConflictParaEmailDuplicado() {
        RegistroRequest req = new RegistroRequest();
        req.setEmail("davy@email.com");
        req.setSenha("senha123");

        when(usuarioRepo.existsByEmail(req.getEmail())).thenReturn(true);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.registrar(req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(usuarioRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void deveAlterarSenhaComSucesso() {
        AlterarSenhaRequest req = new AlterarSenhaRequest();
        req.setSenhaAtual("senha123");
        req.setNovaSenha("novaSenha456");

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(encoder.matches("senha123", usuario.getSenha()))
                .thenReturn(true);
        when(encoder.encode("novaSenha456"))
                .thenReturn("$2a$10$novoHash");

        assertDoesNotThrow(
                () -> authService.alterarSenha(usuario.getEmail(), req)
        );

        verify(usuarioRepo).save(usuario);
        assertEquals("$2a$10$novoHash", usuario.getSenha());
    }

    @Test
    @DisplayName("Deve lançar BAD_REQUEST para senha atual incorreta")
    void deveLancarBadRequestParaSenhaIncorreta() {
        AlterarSenhaRequest req = new AlterarSenhaRequest();
        req.setSenhaAtual("senhaErrada");
        req.setNovaSenha("novaSenha456");

        when(usuarioRepo.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(encoder.matches(anyString(), anyString()))
                .thenReturn(false);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.alterarSenha(usuario.getEmail(), req)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(usuarioRepo, never()).save(any());
    }
}
