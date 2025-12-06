package com.github.fabriciolfj.study.configuration;

import com.github.fabriciolfj.study.entity.Transacao;
import com.github.fabriciolfj.study.joblistener.JobCompletionNotificationListener;
import com.github.fabriciolfj.study.joblistener.StepNotificationListener;
import com.github.fabriciolfj.study.model.TransacaoCSV;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuração principal do Batch Job.
 *
 * Aqui é onde montamos o fluxo completo:
 * Job -> Step -> Reader -> Processor -> Writer
 *
 * Um Job pode ter múltiplos Steps executados em sequência ou condicionalmente.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchJobConfig {

    @Value("${batch.chunk-size:100}")
    private int chunkSize;

    @Value("${batch.max-skip-count:10}")
    private int maxSkipCount;

    /**
     * Define o Job principal de processamento de transações.
     *
     * RunIdIncrementer: Permite executar o mesmo job múltiplas vezes,
     * incrementando automaticamente um parâmetro run.id.
     */
    @Bean
    public Job processarTransacoesJob(
            JobRepository jobRepository,
            Step processarTransacoesStep,
            JobCompletionNotificationListener listener) {

        return new JobBuilder("processarTransacoesJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(processarTransacoesStep)
                // Aqui poderíamos adicionar mais steps:
                // .next(validarTransacoesStep)
                // .next(gerarRelatorioStep)
                .build();
    }

    /**
     * Define o Step de processamento.
     *
     * O Step é configurado com:
     * - chunk: tamanho do lote de processamento
     * - reader: de onde ler os dados
     * - processor: como transformar os dados
     * - writer: onde gravar os dados
     * - fault tolerance: como lidar com erros
     */
    @Bean
    public Step processarTransacoesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<TransacaoCSV> reader,
            ItemProcessor<TransacaoCSV, Transacao> processor,
            ItemWriter<Transacao> writer,
            StepNotificationListener stepListener) {

        log.info("Configurando Step com chunk size: {}", chunkSize);

        return new StepBuilder("processarTransacoesStep", jobRepository)
                .<TransacaoCSV, Transacao>chunk(chunkSize)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(stepListener)
                // Configuração de tolerância a falhas
                .faultTolerant()
                .skip(Exception.class)  // Tipos de exceção que podem ser puladas
                .skipLimit(maxSkipCount)  // Máximo de itens que podem ser pulados
                .retryLimit(3)  // Tentar 3 vezes antes de pular
                .retry(Exception.class)  // Tipos de exceção que devem ser retentadas
                .build();
    }

    /**
     * Exemplo de Step adicional que poderia ser executado após o primeiro.
     * Descomente para adicionar ao Job.
     */
    /*
    @Bean
    public Step validarTransacoesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {

        return new StepBuilder("validarTransacoesStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Executando validação pós-processamento...");
                    // Lógica de validação aqui
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
    */
}