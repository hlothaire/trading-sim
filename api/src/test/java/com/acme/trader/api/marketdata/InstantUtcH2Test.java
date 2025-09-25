package com.acme.trader.api.marketdata;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.TimeZone;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InstantUtcH2Test {

    @Autowired
    private CandleRepository repository;

    @Autowired
    private EntityManager entityManager;

    private TimeZone originalTimeZone;

    @BeforeEach
    void setUp() {
        originalTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
    }

    @AfterEach
    void tearDown() {
        TimeZone.setDefault(originalTimeZone);
    }

    @Test
    void h2StoresInstantsInUtc() {
        Instant instant = Instant.parse("2024-01-01T00:00:00Z");
        Candle candle = new Candle();
        candle.setSymbol("TEST");
        candle.setTs(instant);
        candle.setOpenPrice(BigDecimal.ONE);
        candle.setHighPrice(BigDecimal.ONE);
        candle.setLowPrice(BigDecimal.ONE);
        candle.setClosePrice(BigDecimal.ONE);
        candle.setVolume(BigDecimal.ONE);
        repository.save(candle);
        entityManager.flush();
        entityManager.clear();

        Instant stored = repository.findById(candle.getId()).orElseThrow().getTs();
        assertThat(stored).isEqualTo(instant);
    }
}
