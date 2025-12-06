package com.github.fabriciolfj.study.repositories;

import com.github.fabriciolfj.study.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    Optional<Transacao> findByIdOriginal(String idOriginal);

    boolean existsByIdOriginal(String idOriginal);
}
