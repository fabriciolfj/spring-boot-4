package com.github.fabriciolfj.study.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public class CarNotFoundException extends RuntimeException implements ErrorResponse {

    private String message;
    public CarNotFoundException(final String message) {
        super(message);

        this.message = message;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public ProblemDetail getBody() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message);
    }
}
