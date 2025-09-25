package com.acme.engine

import java.time.*


enum Side:
  case Buy, Sell


final case class Candle(
                         ts: Instant,
                         open: Double,
                         high: Double,
                         low: Double,
                         close: Double,
                         volume: Double
                       )


final case class Signal(ts: Instant, side: Side)


final case class Order(ts: Instant, side: Side, price: Double, qty: Double)
final case class Trade(entry: Order, exit: Option[Order]) {
  def pnl: Double =
    exit.fold(0.0) { e =>
      (e.price - entry.price) * (if entry.side == Side.Buy then 1 else -1) * qty
    }
  def qty: Double = entry.qty
}


final case class EquityPoint(ts: Instant, value: Double)


final case class BacktestConfig(
                                 symbol: String,
                                 initialCash: Double = 10_000,
                                 feeBps: Double = 2.0,
                                 riskFraction: Double = 0.99,
                                 fast: Int = 10,
                                 slow: Int = 20
                               )


final case class BacktestReport(
                                 trades: List[Trade],
                                 equityCurve: List[EquityPoint],
                                 totalReturn: Double,
                                 maxDrawdown: Double,
                                 winRate: Double
                               )
