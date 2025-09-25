package com.acme.trader.api.marketdata;


import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;


public interface CandleRepository extends JpaRepository<Candle, Long> {
    List<Candle> findBySymbolAndTsBetweenOrderByTsAsc(String symbol, Instant from, Instant to);
}