package com.github.fabriciolfj.study.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class InsertRepositoryImpl<E> implements InsertRepository<E> {

    private final JdbcAggregateTemplate template;

    @Override
    public E insert(E employee) {
        return template.insert(employee);
    }
}