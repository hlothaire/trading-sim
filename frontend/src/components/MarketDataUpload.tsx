import { useState } from 'react';
import api from '../api';
import axios from 'axios';

interface Props {
  symbol: string;
  onUploaded: (uploaded: boolean, symbol?: string) => void;
}

export default function MarketDataUpload({ symbol, onUploaded }: Props) {
  const [file, setFile] = useState<File | null>(null);
  const [error, setError] = useState('');
  const [uploading, setUploading] = useState(false);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFile(e.target.files?.[0] ?? null);
    setError('');
    onUploaded(false);
  };

  const handleUpload = async () => {
    if (!file) {
      setError('Please select a file to upload.');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    formData.append('symbol', symbol);
    const inferredSymbol = file.name.replace(/\.csv$/i, '');
    try {
      setUploading(true);
      await api.post('/api/marketdata/import', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      setError('');
      onUploaded(true, inferredSymbol);
    } catch (e) {
      if (axios.isAxiosError(e)) {
        setError(e.response?.data?.error || 'Upload failed.');
      } else {
        setError('Upload failed.');
      }
      onUploaded(false);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="upload">
      <input type="file" accept=".csv" onChange={handleFileChange} />
      <button onClick={handleUpload} disabled={uploading}>Upload</button>
      {error && <p className="error">{error}</p>}
    </div>
  );
}
