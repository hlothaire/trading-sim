package com.acme.trader.api.marketdata;


import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;


@Service
public class MarketDataService {
    private final CandleRepository repo;
    public MarketDataService(CandleRepository repo) { this.repo = repo; }


    public List<Candle> getCandles(String symbol, Instant from, Instant to) {
        return repo.findBySymbolAndTsBetweenOrderByTsAsc(symbol, from, to);
    }
}