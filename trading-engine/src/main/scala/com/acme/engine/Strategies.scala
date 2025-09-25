package com.acme.engine

import java.time.*

object Strategies:
  def smaCross(candles: List[Candle], fast: Int, slow: Int): List[Signal] =
    val closes = candles.map(_.close)
    val f = Indicators.sma(closes, fast)
    val s = Indicators.sma(closes, slow)
    val offset = slow - fast
    val pairs = f.drop(math.max(0, offset)).zip(s)
    val times = candles.drop(slow - 1).map(_.ts)

    pairs.zip(times).sliding(2).flatMap {
      case Seq(((fPrev, sPrev), _), ((fNow, sNow), ts)) =>
        if fPrev <= sPrev && fNow > sNow then Some(Signal(ts, Side.Buy))
        else if fPrev >= sPrev && fNow < sNow then Some(Signal(ts, Side.Sell))
        else None
      case _ => None
    }.toList

