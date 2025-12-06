package com.github.fabriciolfj.study.joblistener;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Listener que monitora a execução do Job inteiro.
 * Útil para logging, notificações, limpeza de recursos, etc.
 */
@Slf4j
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("========================================");
        log.info("Job INICIADO: {}", jobExecution.getJobInstance().getJobName());
        log.info("JobId: {}", jobExecution.getJobInstanceId());
        log.info("Parâmetros: {}", jobExecution.getJobParameters());
        log.info("========================================");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Duration duration = Duration.between(
                jobExecution.getStartTime(),
                jobExecution.getEndTime()
        );

        log.info("========================================");
        log.info("Job FINALIZADO: {}", jobExecution.getJobInstance().getJobName());
        log.info("Status: {}", jobExecution.getStatus());
        log.info("Tempo de execução: {} segundos", duration.getSeconds());
        log.info("Itens lidos: {}", jobExecution.getStepExecutions().stream()
                .mapToLong(s -> s.getReadCount())
                .sum());
        log.info("Itens processados: {}", jobExecution.getStepExecutions().stream()
                .mapToLong(s -> s.getWriteCount())
                .sum());
        log.info("Itens com erro: {}", jobExecution.getStepExecutions().stream()
                .mapToLong(s -> s.getSkipCount())
                .sum());

        if (jobExecution.getAllFailureExceptions().size() > 0) {
            log.error("Erros encontrados durante execução:");
            jobExecution.getAllFailureExceptions().forEach(e ->
                    log.error("- {}: {}", e.getClass().getSimpleName(), e.getMessage())
            );
        }

        log.info("========================================");
    }
}