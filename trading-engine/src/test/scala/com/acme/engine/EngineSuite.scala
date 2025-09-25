package com.acme.engine

import org.scalatest.funsuite.AnyFunSuite
import java.time.Instant

class EngineSuite extends AnyFunSuite:

  test("Indicators.sma computes simple moving averages"):
    val values = List(1.0, 2.0, 3.0, 4.0, 5.0)
    val result = Indicators.sma(values, 3)
    assert(result == List(2.0, 3.0, 4.0))

  test("Indicators.ema computes exponential moving averages"):
    val values = List(1.0, 2.0, 3.0, 4.0, 5.0)
    val result = Indicators.ema(values, 3)
    val expected = List(1.0, 1.5, 2.25, 3.125, 4.0625)
    assert(result == expected)

  test("Strategies.smaCross detects SMA crossovers"):
    val closes = List(3.0, 2.0, 1.0, 2.0, 3.0, 1.0, 4.0)
    val start = Instant.parse("2024-01-01T00:00:00Z")
    val candles = closes.zipWithIndex.map { case (c, i) =>
      Candle(start.plusSeconds(i.toLong), c, c, c, c, 0.0)
    }
    val signals = Strategies.smaCross(candles, 2, 3)
    val expected = List(
      Signal(candles(4).ts, Side.Buy),
      Signal(candles(6).ts, Side.Sell)
    )
    assert(signals == expected)

  test("Backtester.run generates a profitable trade and report"):
    val closes = List(3.0, 2.0, 1.0, 2.0, 3.0, 1.0, 4.0)
    val start = Instant.parse("2024-01-01T00:00:00Z")
    val candles = closes.zipWithIndex.map { case (c, i) =>
      Candle(start.plusSeconds(i.toLong), c, c, c, c, 0.0)
    }
    val cfg = BacktestConfig(
      symbol = "TEST",
      initialCash = 1000.0,
      feeBps = 0.0,
      riskFraction = 1.0,
      fast = 2,
      slow = 3
    )
    val report = Backtester.run(candles, cfg)
    assert(report.trades.length == 1)
    val trade = report.trades.head
    assert(trade.entry.price == 3.0)
    assert(trade.exit.exists(_.price == 4.0))
    assert(math.abs(report.totalReturn - 0.3333333333333333) < 1e-6)
    assert(report.winRate == 1.0)
