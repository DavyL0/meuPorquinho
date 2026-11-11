package com.meu.porquinho.repository;

import com.meu.porquinho.model.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvestimentoRepository extends JpaRepository<Investimento, UUID> {

    List<Investimento> findByUsuarioIdOrderByCreatedAtDesc(UUID usuarioId);

    List<Investimento> findByAtivoTrue();

    Optional<Investimento> findByIdAndUsuarioId(UUID id, UUID usuarioId);
}
