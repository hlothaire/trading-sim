package com.acme.trader.api.marketdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CandleJsonTest {

    @Test
    void serializesInstantAsIso8601() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Candle candle = new Candle();
        candle.setTs(Instant.parse("2024-01-01T10:15:30Z"));

        String json = mapper.writeValueAsString(candle);

        assertThat(json).contains("\"ts\":\"2024-01-01T10:15:30Z\"");
    }
}

