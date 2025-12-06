package com.github.fabriciolfj.study.joblistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;



/**
 * Listener que monitora a execução de cada Step.
 */
@Slf4j
@Component
public class StepNotificationListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info(">>> Step INICIADO: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("<<< Step FINALIZADO: {}", stepExecution.getStepName());
        log.info("    Lidos: {}, Processados: {}, Escritos: {}, Pulados: {}",
                stepExecution.getReadCount(),
                stepExecution.getFilterCount() + stepExecution.getWriteCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount());

        return stepExecution.getExitStatus();
    }
}
