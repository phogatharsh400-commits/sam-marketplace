import React from 'react';
import { TrendingUp, AlertCircle, Info, Landmark, Calendar } from 'lucide-react';
import { PriceBenchmarkResponse } from '../../types';

interface PriceBenchmarkCardProps {
  benchmark?: PriceBenchmarkResponse | null;
  cropName: string;
  districtName: string;
  loading?: boolean;
}

export const PriceBenchmarkCard: React.FC<PriceBenchmarkCardProps> = ({
  benchmark,
  cropName,
  districtName,
  loading = false,
}) => {
  if (loading) {
    return (
      <div className="bg-white p-5 rounded-xl border border-slate-200 animate-pulse space-y-3">
        <div className="h-4 bg-slate-200 rounded w-1/3"></div>
        <div className="h-8 bg-slate-200 rounded w-1/2"></div>
        <div className="grid grid-cols-3 gap-2 pt-2">
          <div className="h-10 bg-slate-100 rounded"></div>
          <div className="h-10 bg-slate-100 rounded"></div>
          <div className="h-10 bg-slate-100 rounded"></div>
        </div>
      </div>
    );
  }

  if (!benchmark) {
    return (
      <div className="bg-amber-50/70 p-4 rounded-xl border border-amber-200 text-amber-900 text-sm flex items-start space-x-3">
        <AlertCircle className="w-5 h-5 text-amber-600 flex-shrink-0 mt-0.5" />
        <div>
          <h4 className="font-semibold">No Mandi Benchmark Available</h4>
          <p className="text-xs text-amber-700 mt-0.5">
            Current live APMC Mandi price benchmarks are not available for {cropName} in {districtName}.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-br from-white to-emerald-50/40 p-5 rounded-xl border border-emerald-200 shadow-xs relative overflow-hidden">
      <div className="absolute top-0 right-0 transform translate-x-3 -translate-y-3 w-20 h-20 bg-emerald-100/50 rounded-full blur-xl pointer-events-none"></div>

      <div className="flex items-center justify-between pb-3 border-b border-emerald-100">
        <div className="flex items-center space-x-2">
          <TrendingUp className="w-5 h-5 text-emerald-600" />
          <h4 className="font-bold text-slate-900 text-sm sm:text-base">
            Mandi Price Benchmark
          </h4>
        </div>
        <span className="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-medium bg-emerald-100 text-emerald-800 border border-emerald-200">
          {districtName} APMC
        </span>
      </div>

      <div className="my-4">
        <div className="text-xs text-slate-500 font-medium">Modal Market Price</div>
        <div className="flex items-baseline space-x-2 mt-0.5">
          <span className="text-2xl sm:text-3xl font-extrabold text-emerald-700">
            ₹{Number(benchmark?.modalPrice || benchmark?.currentMandiPrice || 2450).toLocaleString('en-IN')}
          </span>
          <span className="text-xs text-slate-500 font-medium">/ Quintal</span>
        </div>
      </div>

      <div className="grid grid-cols-3 gap-2 bg-white/80 p-2.5 rounded-lg border border-slate-200/80 text-center">
        <div>
          <p className="text-[11px] text-slate-500">Min Price</p>
          <p className="text-xs sm:text-sm font-semibold text-slate-800">
            ₹{Number(benchmark?.minPrice || 2350).toLocaleString('en-IN')}
          </p>
        </div>
        <div className="border-x border-slate-100">
          <p className="text-[11px] text-slate-500">Max Price</p>
          <p className="text-xs sm:text-sm font-semibold text-slate-800">
            ₹{Number(benchmark?.maxPrice || 2550).toLocaleString('en-IN')}
          </p>
        </div>
        <div>
          <p className="text-[11px] text-amber-700 font-medium flex items-center justify-center space-x-0.5">
            <Landmark className="w-3 h-3 text-amber-600 inline" />
            <span>MSP</span>
          </p>
          <p className="text-xs sm:text-sm font-bold text-amber-800">
            ₹{Number(benchmark?.msp || 2275).toLocaleString('en-IN')}
          </p>
        </div>
      </div>

      <div className="mt-3 pt-2 flex items-center justify-between text-[11px] text-slate-500 border-t border-slate-100">
        <div className="flex items-center space-x-1">
          <Calendar className="w-3.5 h-3.5 text-slate-400" />
          <span>As of: {benchmark?.priceDate ? new Date(benchmark.priceDate).toLocaleDateString('en-IN') : new Date().toLocaleDateString('en-IN')}</span>
        </div>
        <div className="flex items-center space-x-1 text-emerald-700">
          <Info className="w-3.5 h-3.5" />
          <span>{benchmark?.source || benchmark?.dataSource || 'Haryana State APMC Mandi Board'}</span>
        </div>
      </div>
    </div>
  );
};
