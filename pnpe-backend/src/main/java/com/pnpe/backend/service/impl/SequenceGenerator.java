package com.pnpe.backend.service.impl;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class SequenceGenerator {

    private final AtomicLong preRegCounter = new AtomicLong(1000);

    public String nextPreRegistrationNumber() {
        return "PR-" + preRegCounter.incrementAndGet();
    }
}