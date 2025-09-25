package com.acme.engine

import java.time.*
import java.util.{List => JList}
import scala.jdk.CollectionConverters.*
import scala.annotation.nowarn
import scala.annotation.static

class EngineFacade

object EngineFacade:
  @static def runBacktest(candles: JList[Candle], cfg: BacktestConfig): BacktestReport =
    Backtester.run(candles.asScala.toList, cfg)