package com.github.fabriciolfj.study.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Column(name = "year_published")
    private String yearPublished;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 5)
    private float[] embedding;
}