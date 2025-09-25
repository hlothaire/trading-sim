package com.acme.trader.api.backtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BacktestIntegrationTest {

    @Autowired
    MockMvc mvc;

    private String buildCsv(int lines) {
        StringBuilder sb = new StringBuilder("timestamp,open,high,low,close,volume\n");
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        for (int i = 0; i < lines; i++) {
            Instant ts = start.plus(Duration.ofDays(i));
            sb.append(ts).append(",1,1,1,1,100\n");
        }
        return sb.toString();
    }

    @Test
    void uploadCsvThenRunBacktestProducesEquityCurve() throws Exception {
        String csv = buildCsv(20);
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
        mvc.perform(multipart("/api/marketdata/import").file(file).param("symbol", "AAPL"))
                .andExpect(status().isOk());

        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = start.plus(Duration.ofDays(19));
        String body = String.format("{\"symbol\":\"AAPL\",\"from\":\"%s\",\"to\":\"%s\",\"fast\":1,\"slow\":2,\"initialCash\":1000.0}", start, end);

        mvc.perform(post("/api/backtest/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equity[0]").exists());
    }

    @Test
    void backtestWithoutImportReturnsError() throws Exception {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = start.plus(Duration.ofDays(19));
        String body = String.format("{\"symbol\":\"MSFT\",\"from\":\"%s\",\"to\":\"%s\",\"fast\":1,\"slow\":2,\"initialCash\":1000.0}", start, end);

        mvc.perform(post("/api/backtest/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("No market data for symbol MSFT"));
    }
}
