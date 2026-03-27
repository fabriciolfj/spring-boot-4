package com.github.fabriciolfj.study.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DummyService {

    private final MeterRegistry meterRegistry;
    private final Meter.MeterProvider<Counter> counterProvider;
    private final Meter.MeterProvider<Timer> timerProvider;


    public DummyService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.counterProvider = Counter.builder("bar.count")
                .withRegistry(meterRegistry);
        this.timerProvider = Timer.builder("bar.time")
                .withRegistry(meterRegistry);
    }

    public String foo(final String deviceType) {
        log.info("foo {}", deviceType);

        counterProvider.withTag("device.type", deviceType)
                .increment();

        return timerProvider.withTag("device.type", deviceType)
                .record(() ->  invokeSomeLogic(deviceType));
    }


    private String invokeSomeLogic(final String deviceType) {
        return deviceType.toUpperCase();
    }

}
