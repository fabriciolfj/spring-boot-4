package com.github.fabriciolfj.study.repositories;

import com.github.fabriciolfj.study.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
