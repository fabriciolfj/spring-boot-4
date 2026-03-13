package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.entity.Book;
import com.github.fabriciolfj.study.repositories.PGVectorBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PgVectorBookService {

    private final EmbeddingModel embeddingModel;
    private final PGVectorBookRepository vectorBookRepository;

    public void searchByYear() {
        final Vector vector = getEmbedding("Alguma documento falando de java??");
        final SearchResults<Book> results = vectorBookRepository.searchByYearPublishedAndEmbeddingNear("2025", vector, Score.of(0.9, ScoringFunction.euclidean()));

        final var contents  = results.getContent();
        log.info("total resultados {}", contents.size());

        results.forEach(b ->  log.info("book retornado {}", b));
    }

    public void searchByYearRangeSimilarity() {
        final Vector vector = getEmbedding("Alguma documento falando de java?");
        final Range<Similarity> range = Range.closed(
                Similarity.of(0.7, ScoringFunction.cosine()),
                Similarity.of(0.9, ScoringFunction.cosine())
        );

        final SearchResults<Book> results = vectorBookRepository
                .searchByYearPublishedAndEmbeddingWithin("2025", vector, range, Limit.of(5));

        final var contents  = results.getContent();
        log.info("total resultados {}", contents.size());

        results.forEach(b ->  log.info("book retornado {}", b));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save() {
        final float[] floats = getFloats("aprendendo java em 21 dias");
        log.info("size embedding {}", floats.length);

        final var book = Book.builder().embedding(floats)
                .yearPublished("2025")
                .content("book java")
                .build();

        vectorBookRepository.save(book);
    }

    private Vector getEmbedding(final String text) {
        final var output = getFloats(text);

        return Vector.of(output);
    }

    private float @NonNull [] getFloats(String text) {
        final var response = embeddingModel.embedForResponse(List.of(text));
        return response.getResult().getOutput();
    }
}
