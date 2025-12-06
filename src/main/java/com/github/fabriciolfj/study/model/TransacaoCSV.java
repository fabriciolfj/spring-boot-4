package com.github.fabriciolfj.study.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa uma linha do arquivo CSV de entrada.
 * Corresponde exatamente à estrutura do arquivo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoCSV {

    private String id;
    private String dataHora;
    private String tipo;  // CREDITO, DEBITO, PIX, TED
    private String valor;
    private String origem;
    private String destino;
    private String descricao;
    private String status;  // PENDENTE, APROVADA, REJEITADA
}
