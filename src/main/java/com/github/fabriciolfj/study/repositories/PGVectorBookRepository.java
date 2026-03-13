package com.github.fabriciolfj.study.repositories;

import com.github.fabriciolfj.study.entity.Book;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("pgvectorBookRepository")
public interface PGVectorBookRepository extends JpaRepository<Book, String> {

    SearchResults<Book> searchByYearPublishedAndEmbeddingNear(String yearPublished, Vector vector, Score scoreThreshold);
    SearchResults<Book> searchByYearPublishedAndEmbeddingWithin(String yearPublished, Vector vector, Range<Similarity> range, Limit topK);
}
