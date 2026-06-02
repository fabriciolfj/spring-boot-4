package com.github.fabriciolfj.study.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesId {

    private Long id;
    private LocalDate saleDate;
}
