package com.acme.trader.api.marketdata;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Instant;
import java.util.TimeZone;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InstantUtcPostgresTest {

    private static PostgreSQLContainer<?> postgres;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        boolean dockerAvailable = DockerClientFactory.instance().isDockerAvailable();
        Assumptions.assumeTrue(dockerAvailable, "Docker not available");
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
        postgres.start();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @AfterAll
    static void cleanUp() {
        if (postgres != null) {
            postgres.stop();
        }
    }

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
    void postgresStoresInstantsInUtc() {
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
