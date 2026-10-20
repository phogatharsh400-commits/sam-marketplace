import React, { useState, useEffect } from 'react';
import { Scale, PlusCircle, CheckCircle2, AlertCircle, TrendingUp } from 'lucide-react';
import { adminApi, benchmarkApi } from '../../api';
import { PriceBenchmarkResponse } from '../../types';

export const AdminBenchmarksPage: React.FC = () => {
  const [benchmarks, setBenchmarks] = useState<PriceBenchmarkResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // New benchmark form
  const [crop, setCrop] = useState('Wheat');
  const [district, setDistrict] = useState('Karnal');
  const [state, setState] = useState('Haryana');
  const [minPrice, setMinPrice] = useState<number>(2380);
  const [maxPrice, setMaxPrice] = useState<number>(2520);
  const [modalPrice, setModalPrice] = useState<number>(2450);
  const [msp, setMsp] = useState<number>(2275);
  const [priceDate, setPriceDate] = useState(new Date().toISOString().split('T')[0]);
  const [submitting, setSubmitting] = useState(false);

  const districts = ['Karnal', 'Hisar', 'Ambala', 'Rohtak', 'Sirsa', 'Panipat'];
  const crops = ['Wheat', 'Paddy', 'Mustard'];

  const fetchBenchmarks = async () => {
    setLoading(true);
    try {
      const res = await benchmarkApi.getAllBenchmarks();
      setBenchmarks(res.data || []);
    } catch (err: any) {
      // Fallback gracefully
      try {
        const fallback = await benchmarkApi.getBenchmarkTrends('Wheat', 'Karnal', 30);
        setBenchmarks(fallback.data || []);
      } catch {
        setError('Failed to load benchmarks.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBenchmarks();
  }, []);

  const handleCreateBenchmark = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(false);
    setError(null);

    try {
      await adminApi.createBenchmark({
        crop,
        district,
        state,
        currentMandiPrice: modalPrice,
        minPrice,
        maxPrice,
        modalPrice,
        msp,
        priceDate,
        source: 'Haryana State APMC Mandi Board',
      });

      setSuccess('Mandi benchmark recorded successfully!');
      setTimeout(() => setSuccess(null), 4000);
      fetchBenchmarks();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create price benchmark.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Mandi Benchmark Administration</h1>
          <p className="text-xs text-slate-500">
            Publish daily modal APMC market rates and official MSP minimums for Haryana mandis
          </p>
        </div>
      </div>

      {success && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-2xl text-xs flex items-center space-x-2">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0" />
          <span>{success}</span>
        </div>
      )}

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs flex items-center space-x-2">
          <AlertCircle className="w-4 h-4 text-rose-600 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Form to add/update benchmark */}
        <div className="lg:col-span-6 bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
          <div className="flex items-center space-x-2 pb-2 border-b border-slate-100">
            <PlusCircle className="w-5 h-5 text-purple-600" />
            <h3 className="font-bold text-slate-900 text-base">Record Mandi APMC Price</h3>
          </div>

          <form onSubmit={handleCreateBenchmark} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Crop</label>
                <select
                  value={crop}
                  onChange={(e) => setCrop(e.target.value)}
                  className="w-full p-2 rounded-xl border border-slate-300 text-xs font-medium"
                >
                  {crops.map((c) => (
                    <option key={c} value={c}>{c}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">District APMC</label>
                <select
                  value={district}
                  onChange={(e) => setDistrict(e.target.value)}
                  className="w-full p-2 rounded-xl border border-slate-300 text-xs font-medium"
                >
                  {districts.map((d) => (
                    <option key={d} value={d}>{d}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-3 gap-2">
              <div>
                <label className="block text-[11px] font-semibold text-slate-700 mb-1">Min Price (₹)</label>
                <input
                  type="number"
                  required
                  value={minPrice}
                  onChange={(e) => setMinPrice(parseFloat(e.target.value) || 0)}
                  className="w-full p-2 rounded-xl border border-slate-300 text-xs font-bold"
                />
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-emerald-800 mb-1">Modal Price (₹)</label>
                <input
                  type="number"
                  required
                  value={modalPrice}
                  onChange={(e) => setModalPrice(parseFloat(e.target.value) || 0)}
                  className="w-full p-2 rounded-xl border border-emerald-300 text-xs font-black text-emerald-700"
                />
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-700 mb-1">Max Price (₹)</label>
                <input
                  type="number"
                  required
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(parseFloat(e.target.value) || 0)}
                  className="w-full p-2 rounded-xl border border-slate-300 text-xs font-bold"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-amber-800 mb-1">Official MSP (₹)</label>
                <input
                  type="number"
                  required
                  value={msp}
                  onChange={(e) => setMsp(parseFloat(e.target.value) || 0)}
                  className="w-full p-2 rounded-xl border border-amber-300 text-xs font-bold text-amber-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Price Date</label>
                <input
                  type="date"
                  required
                  value={priceDate}
                  onChange={(e) => setPriceDate(e.target.value)}
                  className="w-full p-2 rounded-xl border border-slate-300 text-xs"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="w-full py-2.5 px-4 rounded-xl bg-purple-600 hover:bg-purple-700 text-white font-bold text-xs shadow-md transition-all flex items-center justify-center space-x-1.5 disabled:opacity-50"
            >
              {submitting ? <span>Saving...</span> : <span>Publish Mandi Benchmark</span>}
            </button>
          </form>
        </div>

        {/* Existing benchmarks list */}
        <div className="lg:col-span-6 bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
          <div className="flex items-center space-x-2 pb-2 border-b border-slate-100">
            <TrendingUp className="w-5 h-5 text-emerald-600" />
            <h3 className="font-bold text-slate-900 text-base">Recent Price Records</h3>
          </div>

          <div className="space-y-2 max-h-80 overflow-y-auto pr-1">
            {benchmarks.slice(0, 8).map((b) => (
              <div key={b.id} className="p-3 bg-slate-50 rounded-xl border border-slate-100 text-xs flex justify-between items-center">
                <div>
                  <span className="font-bold text-slate-800">{b.crop} - {b.district}</span>
                  <p className="text-[11px] text-slate-500">{new Date(b.priceDate).toLocaleDateString('en-IN')}</p>
                </div>
                <div className="text-right">
                  <span className="font-black text-emerald-700 text-sm">₹{b.modalPrice}/qtl</span>
                  <p className="text-[10px] text-amber-700 font-semibold">MSP: ₹{b.msp}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
