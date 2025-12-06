package com.github.fabriciolfj.study.joblistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.ChunkListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Component;

/**
 * Listener que monitora cada Chunk processado.
 * Um chunk é um grupo de itens processados juntos em uma transação.
 */
@Slf4j
@Component
class TransacaoChunkListener implements ChunkListener {

    private long chunkCount = 0;

    @Override
    public void beforeChunk(Chunk chunk) {
        chunkCount++;
        log.debug("Iniciando processamento do chunk #{}", chunkCount);
    }

    @Override
    public void afterChunk(Chunk chunk) {
        log.debug("Chunk #{} processado com sucesso", chunkCount);
    }
}