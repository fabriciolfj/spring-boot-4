package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.model.Pessoa;
import kotlin.collections.ArrayDeque;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final ResourceLoader resourceLoader;

    public List<Pessoa> processar(final String fileName) {
        final List<Pessoa> pessoas = new ArrayDeque<>();
        try {
            final Resource resource = resourceLoader.getResource("classpath:" + fileName);

            try(final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                final CSVParser csvRecords = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreSurroundingSpaces(true)
                        .setTrim(true)
                        .setNullString("")
                        .build());

                for (CSVRecord record: csvRecords) {
                    final var pessoa = Pessoa.builder()
                            .nome(record.get("nome"))
                            .endereco(record.get("cidade"))
                            .build();

                    pessoas.add(pessoa);
                }

                return pessoas;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
