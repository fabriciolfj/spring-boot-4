package com.github.fabriciolfj.study.repositories;

import com.github.fabriciolfj.study.controller.SalesId;
import com.github.fabriciolfj.study.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<Sales, SalesId> {
}
