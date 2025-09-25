import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Backtest from './pages/Backtest';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/backtest" replace />} />
        <Route path="/backtest" element={<Backtest />} />
      </Routes>
    </BrowserRouter>
  );
}