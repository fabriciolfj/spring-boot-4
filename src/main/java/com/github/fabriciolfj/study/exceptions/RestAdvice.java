package com.github.fabriciolfj.study.exceptions;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestAdvice {

    @ExceptionHandler(CarNotFoundException.class)
    public ProblemDetail handleCarNotFoundException(final CarNotFoundException carNotFoundException) {
        return carNotFoundException.getBody();
    }
}
