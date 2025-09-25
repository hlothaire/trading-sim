package com.acme.trader.api.backtest;

import com.acme.trader.api.backtest.dto.BacktestRequest;
import com.acme.trader.api.backtest.dto.BacktestResult;
import com.acme.trader.api.marketdata.Candle;
import com.acme.trader.api.marketdata.MarketDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BacktestServiceTest {
    @Mock
    MarketDataService md;

    @InjectMocks
    BacktestService svc;

    @Test
    void runProducesReport() {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        double[] closes = {3,2,1,2,3,4,3};
        List<Candle> candles = new ArrayList<>();
        for (int i = 0; i < closes.length; i++) {
            Candle c = new Candle();
            c.setSymbol("AAPL");
            c.setTs(start.plus(Duration.ofDays(i)));
            BigDecimal price = BigDecimal.valueOf(closes[i]);
            c.setOpenPrice(price);
            c.setHighPrice(price);
            c.setLowPrice(price);
            c.setClosePrice(price);
            c.setVolume(BigDecimal.valueOf(1000));
            candles.add(c);
        }
        Instant end = start.plus(Duration.ofDays(closes.length - 1));
        when(md.getCandles("AAPL", start, end)).thenReturn(candles);

        BacktestRequest req = new BacktestRequest("AAPL", start, end, 1, 2, 1000.0);
        BacktestResult res = svc.run(req);

        assertEquals(candles.size(), res.equity().size());
        assertEquals(1, res.trades().size());
        assertTrue(res.trades().get(0).pnl() > 0);
    }

    @Test
    void runThrowsWhenNoCandles() {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = start.plus(Duration.ofDays(7));
        when(md.getCandles("AAPL", start, end)).thenReturn(Collections.emptyList());

        BacktestRequest req = new BacktestRequest("AAPL", start, end, 1, 2, 1000.0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> svc.run(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("No market data for symbol AAPL", ex.getReason());
    }
}
