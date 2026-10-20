import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Sprout,
  Filter,
  Search,
  MapPin,
  Calendar,
  Layers,
  ArrowRight,
  TrendingUp,
  ShieldCheck,
  Droplets,
  Gavel
} from 'lucide-react';
import { lotApi, benchmarkApi } from '../api';
import { LotResponse, PriceBenchmarkResponse } from '../types';
import { StatusBadge } from '../components/common/StatusBadge';
import { MeritScoreBadge } from '../components/common/MeritScoreBadge';
import { useLanguage } from '../context/LanguageContext';

export const MarketplacePage: React.FC = () => {
  const { t } = useLanguage();
  const [lots, setLots] = useState<LotResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filters
  const [selectedCrop, setSelectedCrop] = useState<string>('');
  const [selectedDistrict, setSelectedDistrict] = useState<string>('');
  const [searchQuery, setSearchQuery] = useState('');

  const crops = ['Wheat', 'Paddy', 'Mustard'];
  const districts = ['Karnal', 'Hisar', 'Ambala', 'Rohtak', 'Sirsa', 'Panipat'];

  const fetchLots = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await lotApi.getOpenLots({
        crop: selectedCrop || undefined,
        district: selectedDistrict || undefined,
      });
      setLots(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load produce lots.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLots();
  }, [selectedCrop, selectedDistrict]);

  const filteredLots = lots.filter((lot) => {
    if (!searchQuery) return true;
    const q = searchQuery.toLowerCase();
    return (
      lot.crop.toLowerCase().includes(q) ||
      lot.variety.toLowerCase().includes(q) ||
      lot.district.toLowerCase().includes(q) ||
      lot.village.toLowerCase().includes(q) ||
      lot.farmerName.toLowerCase().includes(q)
    );
  });

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-slate-200">
        <div>
          <div className="flex items-center space-x-2 text-emerald-600 font-bold text-xs uppercase tracking-wider">
            <Sprout className="w-4 h-4" />
            <span>Digital Grain Mandi</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight mt-1">
            Open Agricultural Produce Lots
          </h1>
          <p className="text-slate-500 text-sm mt-0.5">
            Directly from verified Haryana farmers • Verified quality & simulated escrow protection
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <Link
            to="/benchmarks"
            className="inline-flex items-center space-x-1.5 px-4 py-2.5 rounded-xl bg-emerald-50 text-emerald-800 border border-emerald-200 text-xs font-bold hover:bg-emerald-100 transition-colors"
          >
            <TrendingUp className="w-4 h-4 text-emerald-600" />
            <span>APMC Mandi Benchmarks</span>
          </Link>
        </div>
      </div>

      {/* Filter Toolbar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs grid grid-cols-1 sm:grid-cols-12 gap-3">
        <div className="sm:col-span-5 relative">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search crop, variety, farmer or village..."
            className="w-full pl-9 pr-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-emerald-500 font-medium"
          />
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3.5" />
        </div>

        <div className="sm:col-span-3">
          <select
            value={selectedCrop}
            onChange={(e) => setSelectedCrop(e.target.value)}
            className="w-full p-2.5 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-emerald-500 font-medium text-slate-700"
          >
            <option value="">All Crops (Wheat, Paddy, Mustard)</option>
            {crops.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </div>

        <div className="sm:col-span-3">
          <select
            value={selectedDistrict}
            onChange={(e) => setSelectedDistrict(e.target.value)}
            className="w-full p-2.5 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-emerald-500 font-medium text-slate-700"
          >
            <option value="">All Haryana Districts</option>
            {districts.map((d) => (
              <option key={d} value={d}>{d}</option>
            ))}
          </select>
        </div>

        <div className="sm:col-span-1 flex items-center justify-center">
          <button
            type="button"
            onClick={() => {
              setSelectedCrop('');
              setSelectedDistrict('');
              setSearchQuery('');
            }}
            className="w-full py-2.5 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-600 text-xs font-semibold transition-colors"
          >
            Reset
          </button>
        </div>
      </div>

      {/* Lot Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[1, 2, 3, 4, 5, 6].map((n) => (
            <div key={n} className="bg-white rounded-2xl border border-slate-200 p-6 animate-pulse space-y-4">
              <div className="h-44 bg-slate-200 rounded-xl"></div>
              <div className="h-4 bg-slate-200 rounded w-2/3"></div>
              <div className="h-4 bg-slate-100 rounded w-1/2"></div>
            </div>
          ))}
        </div>
      ) : error ? (
        <div className="p-6 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-center">
          <p className="font-semibold">{error}</p>
          <button
            onClick={fetchLots}
            className="mt-3 px-4 py-2 bg-rose-600 text-white text-xs font-bold rounded-lg"
          >
            Retry
          </button>
        </div>
      ) : filteredLots.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center max-w-lg mx-auto space-y-4">
          <div className="w-12 h-12 rounded-full bg-emerald-100 text-emerald-700 flex items-center justify-center mx-auto">
            <Sprout className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-bold text-slate-800">No Open Produce Lots Found</h3>
          <p className="text-xs text-slate-500">
            Try resetting your filters or check back later as farmers upload new harvests.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredLots.map((lot) => {
            const imageUrl =
              lot.imageUrls && lot.imageUrls.length > 0
                ? lot.imageUrls[0]
                : 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=600';

            return (
              <div
                key={lot.id}
                className="bg-white rounded-2xl border border-slate-200 shadow-xs hover:shadow-lg transition-all duration-200 overflow-hidden flex flex-col justify-between group"
              >
                <div>
                  {/* Image & Badges */}
                  <div className="relative h-48 bg-slate-100 overflow-hidden">
                    <img
                      src={imageUrl}
                      alt={lot.crop}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                    <div className="absolute top-3 left-3">
                      <StatusBadge status={lot.status} />
                    </div>
                    <div className="absolute top-3 right-3 bg-slate-900/80 backdrop-blur-md text-white px-2.5 py-1 rounded-lg text-xs font-bold">
                      Grade {lot.grade}
                    </div>
                    <div className="absolute bottom-3 left-3 bg-slate-900/80 backdrop-blur-md text-white px-2.5 py-1 rounded-lg text-xs font-semibold flex items-center space-x-1">
                      <Droplets className="w-3.5 h-3.5 text-blue-400" />
                      <span>{lot.moisturePercentage}% Moisture</span>
                    </div>
                  </div>

                  {/* Body Content */}
                  <div className="p-5 space-y-3">
                    <div>
                      <div className="flex items-center justify-between">
                        <span className="text-xs font-bold uppercase tracking-wider text-emerald-600">
                          {lot.crop}
                        </span>
                        <span className="text-xs text-slate-500 flex items-center">
                          <MapPin className="w-3.5 h-3.5 mr-0.5 text-slate-400" />
                          {lot.district}, {lot.village}
                        </span>
                      </div>
                      <div className="flex items-center justify-between gap-2 mt-1">
                        <h3 className="text-lg font-bold text-slate-900 group-hover:text-emerald-700 transition-colors truncate">
                          {lot.variety} Variety
                        </h3>
                        <MeritScoreBadge
                          score={95}
                          tier="PLATINUM"
                          userName="Verified Farmer"
                          size="sm"
                        />
                      </div>
                      <p className="text-xs text-slate-500 line-clamp-2 mt-1">
                        {lot.description || 'Premium harvested produce ready for immediate inspection and pickup.'}
                      </p>
                    </div>

                    <div className="grid grid-cols-2 gap-2 bg-slate-50 p-2.5 rounded-xl border border-slate-100">
                      <div>
                        <p className="text-[11px] text-slate-500">Available Lot</p>
                        <p className="text-sm font-bold text-slate-800">
                          {lot.quantity} {lot.unit}
                        </p>
                      </div>
                      <div>
                        <p className="text-[11px] text-slate-500">Min Reserve Price</p>
                        <p className="text-sm font-extrabold text-emerald-700">
                          ₹{lot.minPricePerQuintal.toLocaleString('en-IN')}/qtl
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center justify-between text-xs text-slate-500 pt-1">
                      <span className="flex items-center">
                        <Calendar className="w-3.5 h-3.5 mr-1 text-slate-400" />
                        Bidding Closes: {new Date(lot.auctionClosingDate).toLocaleDateString('en-IN')}
                      </span>
                      <span className="font-semibold text-slate-700">
                        {lot.totalBids || 0} bids
                      </span>
                    </div>
                  </div>
                </div>

                {/* Footer Action */}
                <div className="p-4 bg-slate-50/80 border-t border-slate-100">
                  <Link
                    to={`/lots/${lot.id}`}
                    className="w-full py-2.5 px-4 rounded-xl bg-slate-900 hover:bg-emerald-600 text-white text-xs font-bold transition-colors flex items-center justify-center space-x-1.5 shadow-xs"
                  >
                    <Gavel className="w-4 h-4" />
                    <span>View Details & Bid</span>
                    <ArrowRight className="w-3.5 h-3.5 ml-1" />
                  </Link>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
