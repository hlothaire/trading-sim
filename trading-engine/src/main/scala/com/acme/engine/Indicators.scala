package com.acme.engine


object Indicators:
  def sma(values: List[Double], period: Int): List[Double] =
    if period <= 0 then Nil
    else values.sliding(period).map(ws => ws.sum / period).toList

  def ema(values: List[Double], period: Int): List[Double] =
    if period <= 0 || values.isEmpty then Nil
    else
      val k = 2.0 / (period + 1)
      values.tail.foldLeft(List(values.head)) { (acc, v) =>
        val prev = acc.head
        (prev + k * (v - prev)) :: acc
      }.reverse
