package com.github.fabriciolfj.study.processor;


import com.github.fabriciolfj.study.entity.Transacao;
import com.github.fabriciolfj.study.model.TransacaoCSV;
import com.github.fabriciolfj.study.repositories.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ItemProcessor que transforma TransacaoCSV em Transacao.
 *
 * Aqui é onde aplicamos:
 * - Validações de negócio
 * - Transformações de dados
 * - Enriquecimento de informações
 * - Filtros (retornando null)
 *
 * Se retornar null, o item é filtrado e não vai para o Writer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransacaoItemProcessor implements ItemProcessor<TransacaoCSV, Transacao> {

    private final TransacaoRepository transacaoRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Transacao process(TransacaoCSV item) throws Exception {
        log.debug("Processando transação: {}", item.getId());

        // Validação 1: Verificar se já existe no banco (evitar duplicatas)
        if (transacaoRepository.existsByIdOriginal(item.getId())) {
            log.warn("Transação duplicada, ignorando: {}", item.getId());
            return null;  // Retorna null para filtrar este item
        }

        // Validação 2: Validar valor
        BigDecimal valor = parseValor(item.getValor());
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Valor inválido para transação {}: {}", item.getId(), item.getValor());
            return null;
        }

        // Validação 3: Valores máximos por tipo
        if (!validarValorMaximo(item.getTipo(), valor)) {
            log.warn("Valor excede limite para tipo {} na transação {}", item.getTipo(), item.getId());
            return null;
        }

        // Transformação: Calcular taxa baseada no tipo
        BigDecimal taxa = calcularTaxa(item.getTipo(), valor);
        BigDecimal valorLiquido = valor.subtract(taxa);

        // Transformação: Converter data
        LocalDateTime dataHora = LocalDateTime.parse(item.getDataHora(), FORMATTER);

        // Construir entidade processada
        Transacao transacao = Transacao.builder()
                .idOriginal(item.getId())
                .dataHora(dataHora)
                .tipo(Transacao.TipoTransacao.valueOf(item.getTipo().toUpperCase()))
                .valor(valor)
                .origem(item.getOrigem())
                .destino(item.getDestino())
                .descricao(item.getDescricao())
                .status(converterStatus(item.getStatus()))
                .processadoEm(LocalDateTime.now())
                .taxaAplicada(taxa)
                .valorLiquido(valorLiquido)
                .build();

        log.info("Transação {} processada com sucesso. Valor: {}, Taxa: {}, Líquido: {}",
                item.getId(), valor, taxa, valorLiquido);

        return transacao;
    }

    private BigDecimal parseValor(String valorStr) {
        try {
            // Remove caracteres não numéricos exceto ponto e vírgula
            String cleaned = valorStr.replace("R$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim();
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            log.error("Erro ao parsear valor: {}", valorStr, e);
            return BigDecimal.ZERO;
        }
    }

    private boolean validarValorMaximo(String tipo, BigDecimal valor) {
        return switch (tipo.toUpperCase()) {
            case "PIX" -> valor.compareTo(new BigDecimal("50000")) <= 0;
            case "TED" -> valor.compareTo(new BigDecimal("100000")) <= 0;
            case "DEBITO" -> valor.compareTo(new BigDecimal("5000")) <= 0;
            case "CREDITO" -> valor.compareTo(new BigDecimal("200000")) <= 0;
            default -> true;
        };
    }

    private BigDecimal calcularTaxa(String tipo, BigDecimal valor) {
        BigDecimal percentualTaxa = switch (tipo.toUpperCase()) {
            case "PIX" -> BigDecimal.ZERO;  // PIX não tem taxa
            case "TED" -> new BigDecimal("0.015");  // 1.5%
            case "DOC" -> new BigDecimal("0.02");   // 2%
            case "DEBITO" -> new BigDecimal("0.005"); // 0.5%
            case "CREDITO" -> new BigDecimal("0.03"); // 3%
            default -> BigDecimal.ZERO;
        };

        return valor.multiply(percentualTaxa).setScale(2, RoundingMode.HALF_UP);
    }

    private Transacao.StatusTransacao converterStatus(String status) {
        try {
            return Transacao.StatusTransacao.valueOf(status.toUpperCase());
        } catch (Exception e) {
            log.warn("Status inválido: {}, usando PENDENTE", status);
            return Transacao.StatusTransacao.PENDENTE;
        }
    }
}