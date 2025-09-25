package com.acme.trader.api.backtest.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BacktestResultJsonTest {

    @Test
    void roundTripSerializationWorks() throws Exception {
        BacktestResult result = new BacktestResult(
                0.25,
                0.1,
                0.6,
                List.of(new BacktestResult.EquityPointDTO("2024-01-01T00:00:00Z", 10000.0)),
                List.of(new BacktestResult.TradeDTO(
                        "2024-01-05T00:00:00Z", 101.0,
                        "2024-01-10T00:00:00Z", 105.0,
                        4.0))
        );

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(result);
        BacktestResult read = mapper.readValue(json, BacktestResult.class);

        assertThat(read).isEqualTo(result);
    }
}
