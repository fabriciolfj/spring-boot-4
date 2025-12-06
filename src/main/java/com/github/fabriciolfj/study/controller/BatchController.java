package com.github.fabriciolfj.study.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para executar jobs batch manualmente.
 *
 * Em produção, os jobs normalmente são executados por um scheduler (cron, Quartz, etc).
 * Este controller é útil para testes e execuções sob demanda.
 */
@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobOperator jobLauncher;
    private final Job processarTransacoesJob;

    /**
     * Endpoint para executar o job de processamento de transações.
     *
     * POST /api/batch/processar
     */
    @PostMapping("/processar")
    public ResponseEntity<Map<String, Object>> processarTransacoes() {
        log.info("Recebida requisição para processar transações");

        try {
            // Criar parâmetros únicos para permitir múltiplas execuções
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("executadoEm", LocalDateTime.now())
                    .addString("requisitante", "API-REST")
                    .toJobParameters();

            // Executar o job
            JobExecution execution = jobLauncher.start(processarTransacoesJob, jobParameters);

            // Preparar resposta
            Map<String, Object> response = new HashMap<>();
            response.put("jobId", execution.getJobInstanceId());
            response.put("status", execution.getStatus().toString());
            response.put("startTime", execution.getStartTime());
            response.put("endTime", execution.getEndTime());

            log.info("Job executado com sucesso. JobId: {}, Status: {}",
                    execution.getJobInstanceId(), execution.getStatus());

            return ResponseEntity.ok(response);

        } catch (JobExecutionAlreadyRunningException e) {
            log.error("Job já está em execução", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Job já está em execução"));

        } catch (JobRestartException e) {
            log.error("Erro ao reiniciar job", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Erro ao reiniciar job: " + e.getMessage()));

        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Instância do job já foi completada", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Job já foi executado com estes parâmetros"));

        } catch (Exception e) {
            log.error("Parâmetros do job inválidos", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Parâmetros inválidos: " + e.getMessage()));
        }
    }
}