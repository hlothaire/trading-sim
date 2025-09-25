package com.acme.trader.api.backtest;


import com.acme.trader.api.backtest.dto.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/backtest")
public class BacktestController {
    private final BacktestService svc;
    public BacktestController(BacktestService svc) { this.svc = svc; }


    @PostMapping("/run")
    public BacktestResult run(@RequestBody @Valid BacktestRequest req) {
        return svc.run(req);
    }
}