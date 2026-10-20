import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Gavel,
  FileCheck2,
  ShieldCheck,
  TrendingUp,
  Sprout,
  ArrowRight,
  Truck,
  AlertCircle
} from 'lucide-react';
import { bidApi, contractApi, benchmarkApi } from '../../api';
import { BidResponse, SaleContractResponse, PriceBenchmarkResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useAuth } from '../../context/AuthContext';

export const BuyerDashboard: React.FC = () => {
  const { user } = useAuth();
  const [bids, setBids] = useState<BidResponse[]>([]);
  const [contracts, setContracts] = useState<SaleContractResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchBuyerData = async () => {
    setLoading(true);
    try {
      const [bidsRes, contractsRes] = await Promise.allSettled([
        bidApi.getMyBids(),
        contractApi.getMyContracts(),
      ]);

      if (bidsRes.status === 'fulfilled') setBids(bidsRes.value.data);
      if (contractsRes.status === 'fulfilled') setContracts(contractsRes.value.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBuyerData();
  }, []);

  const activeBidsCount = bids.filter((b) => b.status === 'ACTIVE').length;
  const awaitingEscrowCount = contracts.filter((c) => c.status === 'AWAITING_ESCROW').length;
  const inTransitCount = contracts.filter((c) => c.status === 'DISPATCHED').length;
  const totalVolumePurchased = contracts
    .filter((c) => c.status === 'DELIVERED' || c.status === 'PAYMENT_RELEASED')
    .reduce((sum, c) => sum + (c.quantity || c.quantityQuintals || 0), 0);

  const displayName = user?.name || user?.fullName || 'Buyer';

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-blue-950 via-slate-900 to-emerald-950 text-white p-6 sm:p-8 rounded-3xl border border-blue-900 shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="space-y-1.5">
          <div className="inline-flex items-center space-x-1 px-3 py-1 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold">
            <Gavel className="w-3.5 h-3.5 mr-1" />
            <span>Buyer & Trader Console</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black">
            Welcome, {displayName}!
          </h1>
          <p className="text-xs sm:text-sm text-slate-300">
            Account: <strong className="text-white">{user?.accountStatus || user?.status}</strong> • Direct procurement from verified farmers.
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <Link
            to="/marketplace"
            className="px-5 py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-md transition-all flex items-center space-x-2"
          >
            <Sprout className="w-4 h-4" />
            <span>Browse Open Lots</span>
          </Link>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Active Bids</span>
            <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center">
              <Gavel className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-slate-900">{activeBidsCount}</p>
          <p className="text-[11px] text-slate-500">Awaiting farmer acceptance</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Escrow Pending</span>
            <div className="w-8 h-8 rounded-lg bg-amber-100 text-amber-700 flex items-center justify-center">
              <ShieldCheck className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-amber-700">{awaitingEscrowCount}</p>
          <p className="text-[11px] text-slate-500">Requires simulated deposit</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">In Transit</span>
            <div className="w-8 h-8 rounded-lg bg-blue-100 text-blue-700 flex items-center justify-center">
              <Truck className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-blue-700">{inTransitCount}</p>
          <p className="text-[11px] text-slate-500">Dispatched by farmers</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Procured Grain</span>
            <div className="w-8 h-8 rounded-lg bg-purple-100 text-purple-700 flex items-center justify-center">
              <Sprout className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-slate-900">{totalVolumePurchased} qtl</p>
          <p className="text-[11px] text-slate-500">Settled and inspected</p>
        </div>
      </div>

      {/* Grid Content */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        <div className="lg:col-span-7 space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="font-bold text-slate-900 text-base">Active Contracts</h3>
              <Link to="/buyer/contracts" className="text-xs font-bold text-emerald-700 hover:text-emerald-800">
                View All ({contracts.length})
              </Link>
            </div>

            {contracts.length === 0 ? (
              <div className="text-center py-8 text-slate-500 text-xs">
                No active purchase contracts yet. Explore lots in the marketplace to place bids.
              </div>
            ) : (
              <div className="space-y-3">
                {contracts.slice(0, 4).map((c) => (
                  <div key={c.id} className="p-4 rounded-xl bg-slate-50 border border-slate-200 flex items-center justify-between gap-4">
                    <div>
                      <div className="flex items-center space-x-2">
                        <span className="font-mono font-bold text-slate-900 text-xs">{c.contractNumber}</span>
                        <StatusBadge status={c.status} />
                      </div>
                      <p className="text-xs text-slate-600 mt-1">
                        Farmer: <strong className="text-slate-800">{c.farmerName}</strong> • {c.quantity || c.quantityQuintals} qtl @ ₹{c.pricePerUnit || c.pricePerQuintal}/qtl
                      </p>
                    </div>

                    <Link
                      to="/buyer/contracts"
                      className="px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs transition-colors flex-shrink-0"
                    >
                      Manage Contract
                    </Link>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="lg:col-span-5 space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="font-bold text-slate-900 text-base">Recent Bid Offers</h3>
              <Link to="/buyer/bids" className="text-xs font-bold text-emerald-700 hover:text-emerald-800">
                View All ({bids.length})
              </Link>
            </div>

            {bids.length === 0 ? (
              <div className="text-center py-8 text-slate-500 text-xs">
                You haven't placed any bids yet.
              </div>
            ) : (
              <div className="space-y-3">
                {bids.slice(0, 4).map((bid) => (
                  <div key={bid.id} className="p-3.5 rounded-xl border border-slate-200 bg-slate-50/50 space-y-1.5 text-xs">
                    <div className="flex justify-between items-center">
                      <span className="font-bold text-slate-900">{bid.crop} ({bid.variety})</span>
                      <StatusBadge status={bid.status} />
                    </div>
                    <div className="flex justify-between items-center text-slate-600">
                      <span>Bid: <strong className="text-emerald-700">₹{bid.bidPricePerQuintal}/qtl</strong> ({bid.quantityQuintals} qtl)</span>
                      <span className="font-bold text-slate-900">₹{bid.totalAmount.toLocaleString('en-IN')}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
