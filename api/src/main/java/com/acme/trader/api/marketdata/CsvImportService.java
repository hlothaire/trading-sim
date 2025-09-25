package com.acme.trader.api.marketdata;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CsvImportService {
    private final CandleRepository repo;

    public CsvImportService(CandleRepository repo) {
        this.repo = repo;
    }

    public List<Candle> importCsv(String symbol, MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .build()
                    .parse(reader);
            validateHeader(parser.getHeaderMap());
            List<Candle> list = new ArrayList<>();
            for (CSVRecord r : parser) {
                try {
                    var c = new Candle();
                    c.setSymbol(symbol);
                    c.setTs(Instant.parse(r.get("timestamp")));
                    c.setOpenPrice(new BigDecimal(r.get("open")));
                    c.setHighPrice(new BigDecimal(r.get("high")));
                    c.setLowPrice(new BigDecimal(r.get("low")));
                    c.setClosePrice(new BigDecimal(r.get("close")));
                    c.setVolume(new BigDecimal(r.get("volume")));
                    list.add(c);
                } catch (DateTimeParseException | IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid CSV: " + e.getMessage());
                }
            }
            return repo.saveAll(list);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV", e);
        }
    }

    private void validateHeader(Map<String, Integer> header) {
        Set<String> required = Set.of("timestamp", "open", "high", "low", "close", "volume");
        if (!header.keySet().equals(required)) {
            throw new IllegalArgumentException("CSV must contain columns " + required);
        }
    }
}
