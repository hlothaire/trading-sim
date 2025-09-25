import { useState } from 'react';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Legend,
} from 'chart.js';
import api from '../api';
import MarketDataUpload from '../components/MarketDataUpload';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend);

interface EquityPoint {
  ts: string;
  value: number;
}

interface BacktestResult {
  totalReturn: number;
  maxDrawdown: number;
  winRate: number;
  equity: EquityPoint[];
}

export default function Backtest() {
  const [form, setForm] = useState({
    symbol: 'AAPL',
    from: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString(),
    to: new Date().toISOString(),
    fast: 5,
    slow: 20,
    initialCash: 1000,
  });
  const [result, setResult] = useState<BacktestResult | null>(null);
  const [csvUploaded, setCsvUploaded] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const runBacktest = async () => {
    try {
      const res = await api.post<BacktestResult>("/api/backtest/run", {
        ...form,
        fast: Number(form.fast),
        slow: Number(form.slow),
        initialCash: Number(form.initialCash),
      });
      setResult(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const data = {
    labels: result?.equity.map((p) => p.ts) ?? [],
    datasets: [
      {
        label: 'Equity',
        data: result?.equity.map((p) => p.value) ?? [],
        borderColor: 'rgb(53, 162, 235)',
        fill: false,
      },
    ],
  };

  return (
    <div className="container">
      <h1>Backtest</h1>
      <MarketDataUpload
        symbol={form.symbol}
        onUploaded={(uploaded, symbol) => {
          setCsvUploaded(uploaded);
          if (uploaded && symbol) {
            setForm((f) => ({ ...f, symbol }));
          }
        }}
      />
      <div className="controls">
        <input name="symbol" value={form.symbol} onChange={handleChange} />
        <input name="from" value={form.from} onChange={handleChange} />
        <input name="to" value={form.to} onChange={handleChange} />
        <input name="fast" type="number" value={form.fast} onChange={handleChange} />
        <input name="slow" type="number" value={form.slow} onChange={handleChange} />
        <input name="initialCash" type="number" value={form.initialCash} onChange={handleChange} />
        <button onClick={runBacktest} disabled={!csvUploaded}>Run</button>
      </div>
      {result && (
        <div className="results">
          <p>Total Return: {result.totalReturn.toFixed(2)}</p>
          <p>Max Drawdown: {result.maxDrawdown.toFixed(2)}</p>
          <p>Win Rate: {result.winRate.toFixed(2)}</p>
          <Line data={data} />
        </div>
      )}
    </div>
  );
}
