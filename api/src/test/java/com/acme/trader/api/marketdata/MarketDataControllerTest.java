package com.acme.trader.api.marketdata;

import com.acme.trader.api.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarketDataController.class)
@Import(SecurityConfig.class)
class MarketDataControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MarketDataService svc;

    @MockBean
    CsvImportService csvSvc;

    @Test
    void candlesEndpointReturnsCandles() throws Exception {
        Candle candle = new Candle();
        candle.setSymbol("AAPL");
        candle.setTs(Instant.parse("2024-01-01T10:15:30Z"));
        candle.setOpenPrice(BigDecimal.valueOf(101.0));
        candle.setHighPrice(BigDecimal.valueOf(102.5));
        candle.setLowPrice(BigDecimal.valueOf(100.5));
        candle.setClosePrice(BigDecimal.valueOf(102.0));
        candle.setVolume(BigDecimal.valueOf(12345.0));

        Instant from = Instant.parse("2024-01-01T00:00:00Z");
        Instant to = Instant.parse("2024-01-02T00:00:00Z");
        when(svc.getCandles(eq("AAPL"), eq(from), eq(to))).thenReturn(List.of(candle));

        mvc.perform(get("/api/marketdata/candles")
                .param("symbol", "AAPL")
                .param("from", from.toString())
                .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$[0].openPrice").value(101.0));

        verify(svc).getCandles(eq("AAPL"), eq(from), eq(to));
    }

    @Test
    void importEndpointDelegatesToService() throws Exception {
        var file = new org.springframework.mock.web.MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());

        mvc.perform(multipart("/api/marketdata/import")
                .file(file)
                .param("symbol", "AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("imported"));

        verify(csvSvc).importCsv(eq("AAPL"), any(MultipartFile.class));
    }

    @Test
    void importEndpointValidatesInput() throws Exception {
        var file = new org.springframework.mock.web.MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());

        mvc.perform(multipart("/api/marketdata/import").file(file))
                .andExpect(status().isBadRequest());

        mvc.perform(multipart("/api/marketdata/import").param("symbol", "AAPL"))
                .andExpect(status().isBadRequest());
    }
}
