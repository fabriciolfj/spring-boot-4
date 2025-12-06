package com.github.fabriciolfj.study.configuration;

import com.github.fabriciolfj.study.model.TransacaoCSV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Configuração do ItemReader para ler transações de um arquivo CSV.
 *
 * O FlatFileItemReader é especializado em ler arquivos delimitados (CSV, TSV, etc).
 * Ele lê linha por linha e mapeia cada linha para um objeto Java.
 */
@Slf4j
@Configuration
public class TransacaoItemReaderConfig {

    @Bean
    public FlatFileItemReader<TransacaoCSV> transacaoItemReader(
            @Value("${batch.input-file}") Resource inputResource) {

        log.info("Configurando TransacaoItemReader para arquivo: {}", inputResource);

        return new FlatFileItemReaderBuilder<TransacaoCSV>()
                .name("transacaoItemReader")
                .resource(inputResource)
                .delimited()  // Arquivo delimitado (CSV)
                .delimiter(";")  // Delimitador é ponto-e-vírgula
                .names("id", "dataHora", "tipo", "valor", "origem", "destino", "descricao", "status")
                .linesToSkip(1)  // Pula a primeira linha (cabeçalho)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(TransacaoCSV.class);
                }})
                .build();
    }
}