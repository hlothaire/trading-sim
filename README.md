# Trading Simulator

## Backend

Build all modules:

```bash
mvn -q -T1C clean install
```

Run tests for individual modules:

```bash
mvn -pl trading-engine test
mvn -pl api test
```

## Frontend

```bash
cd frontend
npm install
npm run dev
```

The Maven `package` phase runs the frontend build and copies the compiled assets into `dist/`.

## Market Data

The application no longer auto-seeds the database with random candles. To run
backtests you must provide your own market data via CSV upload from the
frontend. Use the UI to upload a CSV file containing candle information before
invoking a backtest.
