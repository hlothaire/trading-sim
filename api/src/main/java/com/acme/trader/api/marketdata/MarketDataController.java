package com.acme.trader.api.marketdata;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/marketdata")
public class MarketDataController {
    private final MarketDataService service;
    private final CsvImportService csvService;

    public MarketDataController(MarketDataService service, CsvImportService csvService) {
        this.service = service;
        this.csvService = csvService;
    }


    @GetMapping("/candles")
    public List<Candle> candles(@RequestParam String symbol,
                                @RequestParam String from,
                                @RequestParam String to) {
        return service.getCandles(symbol, Instant.parse(from), Instant.parse(to));
    }


    @PostMapping("/import")
    public ResponseEntity<?> importCsv(@RequestParam("symbol") String symbol,
                                       @RequestParam("file") MultipartFile file) {
        if (symbol == null || symbol.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "symbol is required"));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "file is required"));
        }
        csvService.importCsv(symbol, file);
        return ResponseEntity.ok(Map.of("status", "imported"));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handle(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
    }
}
