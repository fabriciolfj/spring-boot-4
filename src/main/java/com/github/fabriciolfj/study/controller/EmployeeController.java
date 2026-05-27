package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.entity.Employee;
import com.github.fabriciolfj.study.service.EmployeeService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.retry.annotation.Recover;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService service;

    //passar no header o X-API-VERSION: 1
    @PostMapping(version = "1")
    @Retryable(
            includes  = {HttpServerErrorException.class},
            excludes  = HttpClientErrorException.class,
            maxRetriesString = "${pix.retry.max-retries:3}",   // fallback: 3
            delayString      = "${pix.retry.delay:500}",
            jitterString     = "${pix.retry.jitter:50}",
            maxDelayString   = "${pix.retry.max-delay:5000}",
            timeoutString    = "${pix.retry.timeout:10000}",
            maxDelay  = 1000     // cap: máx 1s
    )
    @ConcurrencyLimit(limitString = "${psp.max-concurrent-calls:10}", policy = ConcurrencyLimit.ThrottlePolicy.REJECT) //restringe o numero de execucoes simultaneas no mesmo metodo
    @CircuitBreaker(name = "employeeService", fallbackMethod = "recover")
    @RateLimiter(name = "employeeService", fallbackMethod = "tooManyRequests")
    public ResponseEntity create(@RequestBody final Employee employee) {
        log.info("request received api version 1");
        service.executeSave(employee);

        return ResponseEntity.accepted().build();
    }

    @Recover
    public ResponseEntity recover(final Exception ex, final Employee employee) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @Recover
    public ResponseEntity tooManyRequests(final Exception ex, final Employee employee) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @PostMapping(version = "2")
    public void createEmployer(@RequestBody final Employee employee) {
        log.info("request received api version 2");
        service.executeSave(employee);
    }
}
