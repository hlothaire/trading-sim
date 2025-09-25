package com.acme.trader.api.backtest;


import com.acme.engine.*;
import com.acme.trader.api.marketdata.Candle;
import com.acme.trader.api.marketdata.MarketDataService;
import com.acme.trader.api.backtest.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import scala.jdk.javaapi.CollectionConverters;


import java.util.*;


@Service
public class BacktestService {
    private final MarketDataService md;
    public BacktestService(MarketDataService md) { this.md = md; }


    public BacktestResult run(BacktestRequest req) {
        var candles = md.getCandles(req.symbol(), req.from(), req.to());
      
        if (candles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No market data for symbol " + req.symbol());
        }


        var scalaCandles = new ArrayList<com.acme.engine.Candle>();
        for (Candle c : candles) {
            scalaCandles.add(new com.acme.engine.Candle(
                    c.getTs(),
                    c.getOpenPrice().doubleValue(),
                    c.getHighPrice().doubleValue(),
                    c.getLowPrice().doubleValue(),
                    c.getClosePrice().doubleValue(),
                    c.getVolume().doubleValue()
            ));
        }


        var cfg = new BacktestConfig(req.symbol(), req.initialCash(), 2.0, 0.99, req.fast(), req.slow());
        var rep = EngineFacade.runBacktest(scalaCandles, cfg);


        var equity = new ArrayList<BacktestResult.EquityPointDTO>();
        for (var p : CollectionConverters.asJava(rep.equityCurve())) {
            equity.add(new BacktestResult.EquityPointDTO(p.ts().toString(), p.value()));
        }

        var trades = new ArrayList<BacktestResult.TradeDTO>();
        for (var t : CollectionConverters.asJava(rep.trades())) {
            var exit = t.exit().get();
            trades.add(new BacktestResult.TradeDTO(
                    t.entry().ts().toString(), t.entry().price(),
                    exit.ts().toString(), exit.price(),
                    t.pnl()
            ));
        }


        return new BacktestResult(rep.totalReturn(), rep.maxDrawdown(), rep.winRate(), equity, trades);
    }
}
