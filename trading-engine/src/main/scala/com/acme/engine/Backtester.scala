package com.acme.engine

import java.time.*

object Backtester:
  def run(candles: List[Candle], cfg: BacktestConfig): BacktestReport =
    val signals = Strategies.smaCross(candles, cfg.fast, cfg.slow)
    var cash = cfg.initialCash
    var positionQty = 0.0

    val trades = scala.collection.mutable.ListBuffer.empty[Trade]
    val equity = scala.collection.mutable.ListBuffer.empty[EquityPoint]

    def fee(v: Double) = v * (cfg.feeBps / 10000.0)

    candles.foreach { c =>
      signals.find(_.ts == c.ts).foreach { s =>
        s.side match
          case Side.Buy if cash > 0 =>
            val qty = (cash * cfg.riskFraction) / c.close
            val cost = qty * c.close
            cash -= cost + fee(cost)
            positionQty += qty
            trades += Trade(Order(c.ts, Side.Buy, c.close, qty), None)
          case Side.Sell if positionQty > 0 =>
            val proceeds = positionQty * c.close
            cash += proceeds - fee(proceeds)
            val exit = Order(c.ts, Side.Sell, c.close, positionQty)
            trades.lastIndexWhere(_.exit.isEmpty) match
              case idx if idx >= 0 =>
                val t = trades(idx)
                trades.update(idx, Trade(t.entry, Some(exit)))
              case _ => ()
            positionQty = 0
          case _ => ()
      }
      val equityVal = cash + positionQty * c.close
      equity += EquityPoint(c.ts, equityVal)
    }

    val closedTrades = trades.filter(_.exit.isDefined).toList
    val ret =
      if equity.nonEmpty then (equity.last.value / cfg.initialCash) - 1 else 0.0

    val peakAndDd = equity.foldLeft((Double.MinValue, 0.0)) { case ((peak, maxDd), p) =>
      val newPeak = math.max(peak, p.value)
      val dd = if newPeak == 0 then 0.0 else (newPeak - p.value) / newPeak
      (newPeak, math.max(maxDd, dd))
    }

    val wins = closedTrades.count(_.pnl > 0)
    val wr =
      if closedTrades.nonEmpty then wins.toDouble / closedTrades.size else 0.0

    BacktestReport(closedTrades, equity.toList, ret, peakAndDd._2, wr)