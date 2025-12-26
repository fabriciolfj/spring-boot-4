package com.github.fabriciolfj.study.model;

import lombok.Builder;


@Builder
public record Pessoa(String nome, String endereco) { }
