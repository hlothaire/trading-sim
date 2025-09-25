package com.acme.trader.api.backtest.dto;


import java.time.Instant;

import jakarta.validation.constraints.*;


public record BacktestRequest(
        @NotBlank String symbol,
        @NotNull Instant from,
        @NotNull Instant to,
        int fast,
        int slow,
        double initialCash
) {}