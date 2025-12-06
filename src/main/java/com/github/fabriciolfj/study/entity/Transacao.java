package com.github.fabriciolfj.study.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa uma transação processada.
 * Esta é a estrutura final que será gravada no banco de dados.
 */
@Entity
@Table(name = "transacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_original", unique = true, nullable = false)
    private String idOriginal;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private String origem;

    @Column(nullable = false)
    private String destino;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;

    @Column(name = "processado_em", nullable = false)
    private LocalDateTime processadoEm;

    @Column(name = "taxa_aplicada", precision = 15, scale = 2)
    private BigDecimal taxaAplicada;

    @Column(name = "valor_liquido", precision = 15, scale = 2)
    private BigDecimal valorLiquido;

    public enum TipoTransacao {
        CREDITO, DEBITO, PIX, TED, DOC
    }

    public enum StatusTransacao {
        PENDENTE, APROVADA, REJEITADA, PROCESSADA
    }
}