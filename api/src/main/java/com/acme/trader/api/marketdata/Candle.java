package com.acme.trader.api.marketdata;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;


@Entity
@Table(name = "candles")
public class Candle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant ts;

    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal volume;

    public Candle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Instant getTs() {
        return ts;
    }

    public void setTs(Instant ts) {
        this.ts = ts;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

}