import React from 'react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ReferenceLine
} from 'recharts';
import { PriceBenchmarkResponse } from '../../types';

interface PriceTrendChartProps {
  data: PriceBenchmarkResponse[];
  msp?: number;
  cropName?: string;
  districtName?: string;
}

export const PriceTrendChart: React.FC<PriceTrendChartProps> = ({
  data,
  msp,
  cropName = 'Crop',
  districtName = 'District'
}) => {
  if (!data || data.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-64 bg-slate-50 rounded-xl border border-dashed border-slate-200 text-slate-500 text-sm">
        <p>No historical trend data available for {cropName} in {districtName}.</p>
      </div>
    );
  }

  // Format data for chart
  const chartData = [...data]
    .sort((a, b) => new Date(a.priceDate).getTime() - new Date(b.priceDate).getTime())
    .map((item) => ({
      date: new Date(item.priceDate).toLocaleDateString('en-IN', { month: 'short', day: 'numeric' }),
      modalPrice: item.modalPrice,
      minPrice: item.minPrice,
      maxPrice: item.maxPrice,
      msp: item.msp || msp,
    }));

  return (
    <div className="w-full bg-white p-4 rounded-xl border border-slate-200 shadow-xs">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between pb-4 border-b border-slate-100 gap-2">
        <div>
          <h3 className="text-base font-semibold text-slate-900">
            30-Day Mandi Price Trend
          </h3>
          <p className="text-xs text-slate-500">
            {cropName} in {districtName} Mandi (Prices in ₹ / Quintal)
          </p>
        </div>
        <div className="flex items-center space-x-3 text-xs">
          <span className="flex items-center space-x-1 text-emerald-700 font-medium">
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 inline-block"></span>
            <span>Modal Price</span>
          </span>
          {msp && (
            <span className="flex items-center space-x-1 text-amber-700 font-medium">
              <span className="w-2.5 h-2.5 rounded-full bg-amber-500 inline-block"></span>
              <span>MSP: ₹{msp.toLocaleString('en-IN')}</span>
            </span>
          )}
        </div>
      </div>

      <div className="h-72 mt-4">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
            <defs>
              <linearGradient id="modalGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#10b981" stopOpacity={0.3} />
                <stop offset="95%" stopColor="#10b981" stopOpacity={0.0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
            <XAxis dataKey="date" stroke="#94a3b8" fontSize={11} tickLine={false} />
            <YAxis stroke="#94a3b8" fontSize={11} tickLine={false} domain={['auto', 'auto']} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#0f172a',
                color: '#fff',
                borderRadius: '8px',
                border: 'none',
                fontSize: '12px',
                boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)',
              }}
              formatter={(value: any, name: string) => {
                if (name === 'modalPrice') return [`₹${Number(value).toLocaleString('en-IN')}`, 'Modal Price'];
                if (name === 'minPrice') return [`₹${Number(value).toLocaleString('en-IN')}`, 'Min Price'];
                if (name === 'maxPrice') return [`₹${Number(value).toLocaleString('en-IN')}`, 'Max Price'];
                if (name === 'msp') return [`₹${Number(value).toLocaleString('en-IN')}`, 'Govt MSP'];
                return [value, name];
              }}
            />
            {msp && (
              <ReferenceLine
                y={msp}
                stroke="#f59e0b"
                strokeDasharray="4 4"
                label={{ value: 'MSP', position: 'insideTopRight', fill: '#d97706', fontSize: 10 }}
              />
            )}
            <Area
              type="monotone"
              dataKey="modalPrice"
              stroke="#059669"
              strokeWidth={2.5}
              fillOpacity={1}
              fill="url(#modalGradient)"
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};
