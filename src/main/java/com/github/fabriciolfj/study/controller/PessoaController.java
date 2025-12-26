package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.model.Pessoa;
import com.github.fabriciolfj.study.service.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pessoas")
public class PessoaController {

    private final CsvService csvService;

    @GetMapping("/{idArquivo}")
    public List<Pessoa> getPessoas(@PathVariable final String idArquivo) {
        return csvService.processar(idArquivo);
    }
}
