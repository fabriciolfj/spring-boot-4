package com.github.fabriciolfj.study.entity;

import com.github.fabriciolfj.study.controller.SalesId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.PartitionKey;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
@AllArgsConstructor
@NoArgsConstructor
@Data
@IdClass(SalesId.class)
public class Sales {

    @Id
    private Long id;

    @PartitionKey
    @Id
    @Column(name = "sale_date")
    private LocalDate saleDate;

    private BigDecimal amount;
}
