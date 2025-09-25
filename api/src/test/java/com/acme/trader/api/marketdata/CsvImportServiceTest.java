package com.acme.trader.api.marketdata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CsvImportServiceTest {
    @Autowired
    CandleRepository repo;

    CsvImportService svc;

    @BeforeEach
    void setup() {
        svc = new CsvImportService(repo);
    }

    @Test
    void importParsesCsv() {
        String csv = "timestamp,open,high,low,close,volume\n" +
                "2024-01-01T00:00:00Z,1,1,1,1,100\n" +
                "2024-01-02T00:00:00Z,2,2,2,2,200\n";
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
        List<Candle> candles = svc.importCsv("AAPL", file);
        assertEquals(2, candles.size());
        assertEquals(2, repo.count());
    }

    @Test
    void malformedCsvThrows() {
        String csv = "timestamp,open,high,low,close,volume\n" +
                "2024-01-01T00:00:00Z,1,1,1,1\n"; // missing volume
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
        assertThrows(IllegalArgumentException.class, () -> svc.importCsv("AAPL", file));
    }

    @Test
    void extraColumnCsvThrows() {
        String csv = "timestamp,open,high,low,close,volume,extra\n" +
                "2024-01-01T00:00:00Z,1,1,1,1,100,foo\n";
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
        assertThrows(IllegalArgumentException.class, () -> svc.importCsv("AAPL", file));
    }
}
