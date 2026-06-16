package com.meu.porquinho.repository;

import com.meu.porquinho.dto.usuario.UsuarioEstatisticaDTO;
import com.meu.porquinho.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    // busca com lançamentos já carregados (evita N+1)
    @Query("""
        SELECT u FROM Usuario u
        WHERE u.email = :email
    """)
    Optional<Usuario> findByEmailComLancamentos(
            @Param("email") String email,
            @Param("mes")   String mes
    );

    // estatística geral do usuário
    @Query("""
        SELECT NEW com.meu.porquinho.dto.usuario.UsuarioEstatisticaDTO(
            u.id,
            u.nome,
            u.email,
            (SELECT COUNT(l) FROM Lancamento l WHERE l.usuario = u),
            u.createdAt
        )
        FROM Usuario u
        WHERE u.email = :email
    """)
    Optional<UsuarioEstatisticaDTO> findEstatisticaByEmail(
            @Param("email") String email
    );
}