# API Endpoints

## GET `/api/marketdata/candles`

Returns historical candles for a symbol.

### Query parameters

- `symbol` (string): ticker symbol to query.
- `from` (string, ISO-8601): start timestamp (inclusive).
- `to` (string, ISO-8601): end timestamp (exclusive).

### Response

`200 OK` with a JSON array of candle objects:

```json
[
  {
    "id": 1,
    "symbol": "AAPL",
    "ts": "2024-01-01T10:15:30Z",
    "openPrice": 101.0,
    "highPrice": 102.5,
    "lowPrice": 100.5,
    "closePrice": 102.0,
    "volume": 12345.0
  }
]
```

---

## POST `/api/backtest/run`

Runs a simple moving-average backtest for a symbol.

### Request body

`application/json` formatted as:

```json
{
  "symbol": "AAPL",
  "from": "2024-01-01T00:00:00Z",
  "to": "2024-02-01T00:00:00Z",
  "fast": 5,
  "slow": 20,
  "initialCash": 10000.0
}
```

### Response

`200 OK` with a JSON object:

```json
{
  "totalReturn": 0.15,
  "maxDrawdown": 0.05,
  "winRate": 0.6,
  "equity": [
    { "ts": "2024-01-01T00:00:00Z", "value": 10000.0 }
  ],
  "trades": [
    {
      "entryTs": "2024-01-10T00:00:00Z",
      "entryPrice": 100.0,
      "exitTs": "2024-01-20T00:00:00Z",
      "exitPrice": 110.0,
      "pnl": 10.0
    }
  ]
}
```

