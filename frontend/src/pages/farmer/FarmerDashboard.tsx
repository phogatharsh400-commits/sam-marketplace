import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Sprout,
  PlusCircle,
  FileCheck2,
  Warehouse,
  TrendingUp,
  Landmark,
  ShieldCheck,
  ArrowRight,
  Clock,
  CheckCircle2,
  Truck,
  Scale,
  Bell,
  Sparkles,
  AlertCircle,
  HelpCircle
} from 'lucide-react';
import { lotApi, contractApi, warehouseApi, benchmarkApi, advisorApi } from '../../api';
import {
  LotResponse,
  SaleContractResponse,
  WarehouseBookingResponse,
  PriceBenchmarkResponse,
  StorageAdviceDto,
} from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';
import { SellOrStoreComparisonModal } from '../../components/modals/SellOrStoreComparisonModal';
import { PriceAlertModal } from '../../components/modals/PriceAlertModal';
import { useAuth } from '../../context/AuthContext';
import { useLanguage } from '../../context/LanguageContext';

export const FarmerDashboard: React.FC = () => {
  const { user } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();

  const [lots, setLots] = useState<LotResponse[]>([]);
  const [contracts, setContracts] = useState<SaleContractResponse[]>([]);
  const [bookings, setBookings] = useState<WarehouseBookingResponse[]>([]);
  const [benchmark, setBenchmark] = useState<PriceBenchmarkResponse | null>(null);
  const [loading, setLoading] = useState(true);

  // Advisor states
  const [lotAdviceMap, setLotAdviceMap] = useState<Record<string, StorageAdviceDto>>({});
  const [selectedLotForAdvice, setSelectedLotForAdvice] = useState<LotResponse | null>(null);
  const [isAdviceModalOpen, setIsAdviceModalOpen] = useState(false);

  // Alert modal state
  const [isAlertModalOpen, setIsAlertModalOpen] = useState(false);
  const [alertParams, setAlertParams] = useState<{ crop: string; district: string; targetPrice: number }>({
    crop: 'WHEAT',
    district: 'Karnal',
    targetPrice: 2450,
  });

  const fetchFarmerData = async () => {
    setLoading(true);
    try {
      const [lotsRes, contractsRes, bookingsRes, benchRes] = await Promise.allSettled([
        lotApi.getMyLots(),
        contractApi.getMyContracts(),
        warehouseApi.getMyBookings(),
        benchmarkApi.getLatestBenchmark('Wheat', 'Karnal'),
      ]);

      let fetchedLots: LotResponse[] = [];
      if (lotsRes.status === 'fulfilled') {
        fetchedLots = lotsRes.value.data;
        setLots(fetchedLots);
      }
      if (contractsRes.status === 'fulfilled') setContracts(contractsRes.value.data);
      if (bookingsRes.status === 'fulfilled') setBookings(bookingsRes.value.data);
      if (benchRes.status === 'fulfilled') setBenchmark(benchRes.value.data);

      // Proactively fetch advice for the farmer's top lots
      if (fetchedLots.length > 0) {
        const topLots = fetchedLots.slice(0, 3);
        const advicePromises = topLots.map(async (lot) => {
          try {
            const advRes = await advisorApi.getAdvice(lot.id);
            return { lotId: String(lot.id), advice: advRes.data };
          } catch (e) {
            return null;
          }
        });
        const adviceResults = await Promise.all(advicePromises);
        const map: Record<string, StorageAdviceDto> = {};
        adviceResults.forEach((res) => {
          if (res && res.advice) {
            map[res.lotId] = res.advice;
          }
        });
        setLotAdviceMap(map);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFarmerData();
  }, []);

  const totalEarnings = contracts
    .filter((c) => c.status === 'PAYMENT_RELEASED' || c.status === 'DELIVERED')
    .reduce((sum, c) => sum + c.totalAmount, 0);

  const openLotsCount = lots.filter((l) => l.status === 'OPEN').length;
  const pendingDispatchCount = contracts.filter((c) => c.status === 'ESCROW_FUNDED' || c.status === 'READY_FOR_DISPATCH').length;

  const handleOpenAdvisor = (lot: LotResponse) => {
    setSelectedLotForAdvice(lot);
    setIsAdviceModalOpen(true);
  };

  const handleOpenAlertFromAdvisor = (crop: string, district: string, targetPrice: number) => {
    setAlertParams({ crop, district, targetPrice });
    setIsAlertModalOpen(true);
  };

  const primaryLot = lots[0];
  const primaryAdvice = primaryLot ? lotAdviceMap[String(primaryLot.id)] : null;

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-emerald-900 to-slate-900 text-white p-6 sm:p-8 rounded-3xl border border-emerald-800 shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="space-y-1.5">
          <div className="inline-flex items-center space-x-1 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-xs font-semibold">
            <Sprout className="w-3.5 h-3.5 mr-1" />
            <span>Farmer Control Center</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black">
            Welcome back, {user?.name}!
          </h1>
          <p className="text-xs sm:text-sm text-emerald-200/80">
            Account: <strong className="text-white">{user?.accountStatus}</strong> • Direct trade portal for verified crop lots.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          <button
            onClick={() => setIsAlertModalOpen(true)}
            className="px-4 py-3 rounded-xl bg-slate-800 hover:bg-slate-700 text-amber-300 font-bold text-xs border border-amber-500/30 transition-all flex items-center space-x-2 shadow-xs"
          >
            <Bell className="w-4 h-4 text-amber-400" />
            <span>Target-Price Alerts</span>
          </button>
          <Link
            to="/farmer/create-lot"
            className="px-5 py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-md transition-all flex items-center space-x-2"
          >
            <PlusCircle className="w-4 h-4" />
            <span>List New Produce Lot</span>
          </Link>
        </div>
      </div>

      {/* TODAY'S CROP DECISION CARD (Smart Sell-or-Store Advisor) */}
      {primaryLot && (
        <div className="bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 text-white rounded-3xl p-6 sm:p-7 border border-indigo-800/60 shadow-xl relative overflow-hidden">
          <div className="absolute top-0 right-0 w-96 h-96 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />
          
          <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6 relative z-10">
            <div className="space-y-2.5 max-w-2xl">
              <div className="flex flex-wrap items-center gap-2">
                <div className="w-7 h-7 rounded-lg bg-indigo-500/30 text-indigo-300 flex items-center justify-center border border-indigo-400/40">
                  <Sparkles className="w-4 h-4" />
                </div>
                <span className="text-xs uppercase tracking-widest font-extrabold text-indigo-300">
                  Today's Crop Decision • Smart Sell-or-Store Advisor
                </span>
                <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30">
                  Prototype Support
                </span>
              </div>

              <h2 className="text-xl sm:text-2xl font-black text-white">
                Recommendation for {primaryLot.crop} ({primaryLot.variety}):{' '}
                <span className={
                  primaryAdvice?.recommendation === 'STORE'
                    ? 'text-indigo-400 underline decoration-indigo-500'
                    : primaryAdvice?.recommendation === 'SELL_NOW'
                    ? 'text-emerald-400 underline decoration-emerald-500'
                    : 'text-amber-400 underline decoration-amber-500'
                }>
                  {primaryAdvice?.recommendation === 'STORE'
                    ? 'Store in Certified Warehouse & Hold'
                    : primaryAdvice?.recommendation === 'SELL_NOW'
                    ? 'Sell Now to Highest Bidder'
                    : 'Wait & Monitor Mandi Trends'}
                </span>
              </h2>

              <p className="text-xs sm:text-sm text-slate-300 leading-relaxed">
                {primaryAdvice
                  ? primaryAdvice.reasons?.[0] || 'Comparing spot bids with 30-day moving average price forecast and nearby WDRA warehouse costs.'
                  : 'Analyzing real-time buyer bids, APMC mandi arrivals, and local warehouse storage tariffs to maximize your net realization.'}
              </p>

              {primaryAdvice && (
                <div className="flex flex-wrap items-center gap-4 pt-1 text-xs">
                  <div className="bg-white/10 px-3 py-1.5 rounded-xl backdrop-blur-xs border border-white/10">
                    <span className="text-slate-400">Current Best: </span>
                    <strong className="text-white">₹{primaryAdvice.currentBestBidPrice > 0 ? primaryAdvice.currentBestBidPrice : primaryAdvice.currentMandiModalPrice}/qtl</strong>
                  </div>
                  <div className="bg-white/10 px-3 py-1.5 rounded-xl backdrop-blur-xs border border-white/10">
                    <span className="text-slate-400">30-Day Est. Future: </span>
                    <strong className="text-indigo-300">₹{Math.round(primaryAdvice.estimatedFuturePrice)}/qtl</strong>
                  </div>
                  <div className="bg-white/10 px-3 py-1.5 rounded-xl backdrop-blur-xs border border-white/10">
                    <span className="text-slate-400">Net Est. Benefit: </span>
                    <strong className={primaryAdvice.netExpectedGainPerQuintal >= 0 ? 'text-emerald-400' : 'text-rose-400'}>
                      {primaryAdvice.netExpectedGainPerQuintal >= 0 ? '+' : ''}₹{Math.round(primaryAdvice.totalEstimatedBenefit).toLocaleString('en-IN')}
                    </strong>
                  </div>
                </div>
              )}
            </div>

            <div className="flex flex-col sm:flex-row lg:flex-col items-stretch gap-3 shrink-0">
              <button
                onClick={() => handleOpenAdvisor(primaryLot)}
                className="px-6 py-3 rounded-2xl bg-indigo-500 hover:bg-indigo-400 text-slate-950 font-extrabold text-xs shadow-lg shadow-indigo-500/25 transition-all flex items-center justify-center space-x-2 cursor-pointer"
              >
                <Scale className="w-4 h-4" />
                <span>Open Sell vs Store Analysis</span>
              </button>
              <button
                onClick={() => handleOpenAlertFromAdvisor(primaryLot.crop, primaryLot.district, primaryAdvice ? Math.round(primaryAdvice.estimatedFuturePrice) : 2450)}
                className="px-5 py-2.5 rounded-2xl bg-white/10 hover:bg-white/20 text-white font-bold text-xs border border-white/20 transition-all flex items-center justify-center space-x-2"
              >
                <Bell className="w-4 h-4 text-amber-400" />
                <span>Set Price Alert</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Active Lots</span>
            <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center">
              <Sprout className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-slate-900">{openLotsCount}</p>
          <p className="text-[11px] text-slate-500">Listed across Haryana markets</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Dispatch Pending</span>
            <div className="w-8 h-8 rounded-lg bg-amber-100 text-amber-700 flex items-center justify-center">
              <Truck className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-amber-700">{pendingDispatchCount}</p>
          <p className="text-[11px] text-slate-500">Escrow funded, awaiting truck</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Total Settled</span>
            <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center">
              <Landmark className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-emerald-700">₹{totalEarnings.toLocaleString('en-IN')}</p>
          <p className="text-[11px] text-emerald-600">Released to bank account</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Warehouse Slots</span>
            <div className="w-8 h-8 rounded-lg bg-blue-100 text-blue-700 flex items-center justify-center">
              <Warehouse className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-slate-900">{bookings.length}</p>
          <p className="text-[11px] text-slate-500">Stored lot reservations</p>
        </div>
      </div>

      {/* Main Grid: Active Lots & Recent Contracts */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Left Column: Lots & Bids */}
        <div className="lg:col-span-7 space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="font-bold text-slate-900 text-base">My Listed Produce Lots</h3>
              <Link to="/farmer/lots" className="text-xs font-bold text-emerald-700 hover:text-emerald-800">
                View All ({lots.length})
              </Link>
            </div>

            {lots.length === 0 ? (
              <div className="text-center py-8 text-slate-500 text-xs">
                You haven't listed any produce lots yet. Click "List New Produce Lot" to begin.
              </div>
            ) : (
              <div className="space-y-3">
                {lots.slice(0, 4).map((lot) => {
                  const lotAdvice = lotAdviceMap[String(lot.id)];
                  return (
                    <div
                      key={lot.id}
                      className="p-4 rounded-xl bg-slate-50 border border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-4"
                    >
                      <div className="space-y-1">
                        <div className="flex flex-wrap items-center gap-2">
                          <span className="font-bold text-slate-900 text-sm">{lot.crop} ({lot.variety})</span>
                          <StatusBadge status={lot.status} />
                          {lotAdvice && (
                            <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold border ${
                              lotAdvice.recommendation === 'STORE'
                                ? 'bg-blue-100 text-blue-800 border-blue-200'
                                : lotAdvice.recommendation === 'SELL_NOW'
                                ? 'bg-emerald-100 text-emerald-800 border-emerald-200'
                                : 'bg-amber-100 text-amber-800 border-amber-200'
                            }`}>
                              Advisor: {lotAdvice.recommendation}
                            </span>
                          )}
                        </div>
                        <p className="text-xs text-slate-500">
                          {lot.quantity || lot.quantityQuintals} {lot.unit || 'Quintals'} • Reserve: ₹{lot.minPricePerQuintal}/qtl • {lot.totalBids || 0} bids
                        </p>
                      </div>

                      <div className="flex items-center space-x-2 shrink-0">
                        <button
                          type="button"
                          onClick={() => handleOpenAdvisor(lot)}
                          title="View Sell vs Store Advisor"
                          className="px-3 py-1.5 rounded-lg bg-indigo-50 hover:bg-indigo-100 text-indigo-700 border border-indigo-200 font-bold text-xs transition-colors flex items-center space-x-1"
                        >
                          <Scale className="w-3.5 h-3.5" />
                          <span>Advisor</span>
                        </button>
                        <Link
                          to={`/lots/${lot.id}`}
                          className="px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs transition-colors"
                        >
                          View & Accept Bids
                        </Link>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Contracts */}
        <div className="lg:col-span-5 space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="font-bold text-slate-900 text-base">Sale Contracts & Escrow</h3>
              <Link to="/farmer/contracts" className="text-xs font-bold text-emerald-700 hover:text-emerald-800">
                View All ({contracts.length})
              </Link>
            </div>

            {contracts.length === 0 ? (
              <div className="text-center py-8 text-slate-500 text-xs">
                No active sale contracts. Once you accept a buyer's bid, the contract appears here.
              </div>
            ) : (
              <div className="space-y-3">
                {contracts.slice(0, 4).map((c) => (
                  <div key={c.id} className="p-3.5 rounded-xl border border-slate-200 bg-slate-50/50 space-y-2 text-xs">
                    <div className="flex justify-between items-center">
                      <span className="font-mono font-bold text-slate-700">{c.contractNumber}</span>
                      <StatusBadge status={c.status} />
                    </div>
                    <div className="flex justify-between items-center text-slate-600">
                      <span>Buyer: <strong className="text-slate-800">{c.buyerName}</strong></span>
                      <span className="font-extrabold text-emerald-700">₹{c.totalAmount.toLocaleString('en-IN')}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Sell vs Store Comparison Modal */}
      {selectedLotForAdvice && (
        <SellOrStoreComparisonModal
          lot={selectedLotForAdvice}
          isOpen={isAdviceModalOpen}
          onClose={() => {
            setIsAdviceModalOpen(false);
            setSelectedLotForAdvice(null);
          }}
          onAcceptBid={(lot) => navigate(`/lots/${lot.id}`)}
          onBookWarehouse={() => navigate('/warehouses')}
          onOpenPriceAlert={(crop, district, targetPrice) => {
            handleOpenAlertFromAdvisor(crop, district, targetPrice);
          }}
        />
      )}

      {/* Target-Price Alert Modal */}
      <PriceAlertModal
        isOpen={isAlertModalOpen}
        onClose={() => setIsAlertModalOpen(false)}
        defaultCrop={alertParams.crop}
        defaultDistrict={alertParams.district}
        defaultTargetPrice={alertParams.targetPrice}
      />
    </div>
  );
};
