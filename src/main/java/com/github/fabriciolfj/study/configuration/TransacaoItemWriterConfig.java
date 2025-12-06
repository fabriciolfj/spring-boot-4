package com.github.fabriciolfj.study.configuration;


import com.github.fabriciolfj.study.entity.Transacao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;

/**
 * Configuração do ItemWriter para persistir transações no banco.
 *
 * Usa JpaItemWriter internamente para fazer batch inserts eficientes,
 * mas adiciona logging customizado antes e depois da gravação.
 */
@Slf4j
@Configuration
public class TransacaoItemWriterConfig {

    /**
     * ÚNICA configuração de ItemWriter - com logging integrado.
     *
     * Este bean cria um wrapper que:
     * 1. Calcula estatísticas do chunk
     * 2. Loga informações úteis
     * 3. Delega para JpaItemWriter fazer a gravação
     * 4. Loga sucesso
     */
    @Bean
    public ItemWriter<Transacao> transacaoItemWriter(EntityManagerFactory entityManagerFactory) {
        // Criar o JpaItemWriter interno
        JpaItemWriter<Transacao> jpaItemWriter = new JpaItemWriter<>(entityManagerFactory);

        // Retornar wrapper com logging
        return chunk -> {
            log.info("Gravando chunk de {} transações no banco", chunk.size());

            // Calcular estatísticas do chunk
            BigDecimal valorTotal = chunk.getItems().stream()
                    .map(Transacao::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal taxaTotal = chunk.getItems().stream()
                    .map(Transacao::getTaxaAplicada)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            log.info("Valor total do chunk: R$ {}, Taxa total: R$ {}", valorTotal, taxaTotal);

            // Delega para o JpaItemWriter fazer o trabalho real
            jpaItemWriter.write(chunk);

            log.info("Chunk gravado com sucesso");
        };
    }
}