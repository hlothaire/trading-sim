package com.acme.trader.api.backtest.dto;


import java.util.List;


public record BacktestResult(
        double totalReturn,
        double maxDrawdown,
        double winRate,
        List<EquityPointDTO> equity,
        List<TradeDTO> trades
) {
    public record EquityPointDTO(String ts, double value) {}
    public record TradeDTO(String entryTs, double entryPrice,
                           String exitTs, double exitPrice,
                           double pnl) {}
}