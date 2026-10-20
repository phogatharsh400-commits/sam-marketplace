import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import {
  Sprout,
  MapPin,
  Calendar,
  Layers,
  Droplets,
  Scale,
  ShieldCheck,
  TrendingUp,
  Gavel,
  CheckCircle2,
  AlertCircle,
  Clock,
  UserCheck,
  ArrowLeft,
  Coins,
  Wallet,
  Bell,
  Sparkles,
} from 'lucide-react';
import { lotApi, bidApi, benchmarkApi, walletApi, advisorApi } from '../api';
import { LotResponse, BidResponse, PriceBenchmarkResponse, WalletResponse, StorageAdviceDto } from '../types';
import { StatusBadge } from '../components/common/StatusBadge';
import { PriceBenchmarkCard } from '../components/common/PriceBenchmarkCard';
import { PriceTrendChart } from '../components/common/PriceTrendChart';
import { AcceptBidModal } from '../components/modals/AcceptBidModal';
import { WalletModal } from '../components/modals/WalletModal';
import { SellOrStoreComparisonModal } from '../components/modals/SellOrStoreComparisonModal';
import { PriceAlertModal } from '../components/modals/PriceAlertModal';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';

export const LotDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { t } = useLanguage();

  const [lot, setLot] = useState<LotResponse | null>(null);
  const [bids, setBids] = useState<BidResponse[]>([]);
  const [benchmark, setBenchmark] = useState<PriceBenchmarkResponse | null>(null);
  const [trendData, setTrendData] = useState<PriceBenchmarkResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Advisor states
  const [advice, setAdvice] = useState<StorageAdviceDto | null>(null);
  const [isAdviceModalOpen, setIsAdviceModalOpen] = useState(false);
  const [isAlertModalOpen, setIsAlertModalOpen] = useState(false);

  // Bid form
  const [bidPrice, setBidPrice] = useState<number>(0);
  const [bidQuantity, setBidQuantity] = useState<number>(0);
  const [bidRemarks, setBidRemarks] = useState('');
  const [bidSubmitting, setBidSubmitting] = useState(false);
  const [bidError, setBidError] = useState<string | null>(null);
  const [bidSuccess, setBidSuccess] = useState(false);

  // Modal for farmer accepting bid
  const [selectedBidForAccept, setSelectedBidForAccept] = useState<BidResponse | null>(null);

  // Wallet and deposit state
  const [wallet, setWallet] = useState<WalletResponse | null>(null);
  const [isWalletOpen, setIsWalletOpen] = useState(false);

  const lotId = id || '1';

  const fetchWallet = async () => {
    if (isAuthenticated && user?.role === 'BUYER') {
      try {
        const wRes = await walletApi.getWallet();
        setWallet(wRes.data);
      } catch (e) {}
    }
  };

  const fetchLotDetails = async () => {
    setLoading(true);
    setError(null);
    try {
      const lotRes = await lotApi.getLotById(lotId);
      const lotData = lotRes.data;
      setLot(lotData);
      setBidPrice(lotData.minPricePerQuintal);
      setBidQuantity(lotData.quantity || lotData.quantityQuintals || 100);

      // Load bids if farmer or buyer
      try {
        const bidsRes = await bidApi.getBidsByLot(lotId);
        setBids(bidsRes.data);
      } catch (err) {
        // May be empty or unauthenticated for public view
      }

      // Load benchmark & trend
      try {
        const benchRes = await benchmarkApi.getLatestBenchmark(lotData.crop, lotData.district);
        setBenchmark(benchRes.data);
      } catch (err) {
        // Benchmarks not available
      }

      try {
        const trendRes = await benchmarkApi.getBenchmarkTrends(lotData.crop, lotData.district, 30);
        setTrendData(trendRes.data);
      } catch (err) {
        // Trend not available
      }

      // Load Smart Sell-or-Store advice
      try {
        const advRes = await advisorApi.getAdvice(lotId);
        if (advRes.data) {
          setAdvice(advRes.data);
        }
      } catch (err) {
        // Advisor not available
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load produce lot details.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (lotId) {
      fetchLotDetails();
      fetchWallet();
    }
  }, [lotId]);

  const handlePlaceBid = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!lot) return;

    const availableQty = lot.quantity || lot.quantityQuintals || 0;

    if (bidPrice < lot.minPricePerQuintal) {
      setBidError(`Bid price cannot be lower than farmer's minimum reserve of ₹${lot.minPricePerQuintal}/qtl`);
      return;
    }

    if (bidQuantity > availableQty) {
      setBidError(`Bid quantity cannot exceed available lot quantity of ${availableQty} ${lot.unit}`);
      return;
    }

    setBidSubmitting(true);
    setBidError(null);

    try {
      await bidApi.placeBid(lot.id, {
        bidPricePerQuintal: bidPrice,
        quantityQuintals: bidQuantity,
      });

      setBidSuccess(true);
      setTimeout(() => setBidSuccess(false), 4000);
      fetchLotDetails();
      fetchWallet();
    } catch (err: any) {
      setBidError(err.response?.data?.message || 'Failed to submit bid.');
    } finally {
      setBidSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-12 flex items-center justify-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-4 border-emerald-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error || !lot) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-16 text-center space-y-4">
        <AlertCircle className="w-12 h-12 text-rose-600 mx-auto" />
        <h2 className="text-xl font-bold text-slate-800">{error || 'Lot not found'}</h2>
        <Link
          to="/marketplace"
          className="inline-flex items-center space-x-1 px-4 py-2 bg-emerald-600 text-white text-xs font-bold rounded-xl"
        >
          <ArrowLeft className="w-4 h-4 mr-1" />
          <span>Back to Marketplace</span>
        </Link>
      </div>
    );
  }

  const isFarmerOwner = user && user.role === 'FARMER' && String(lot.farmerId) === String(user.id);
  const isBuyer = user && user.role === 'BUYER';
  const isVerifiedBuyer = isBuyer && (user.accountStatus || user.status) === 'ACTIVE';
  const isLotOpen = lot.status === 'OPEN';
  const availableQty = lot.quantity || lot.quantityQuintals || 0;

  const imageUrl =
    lot.imageUrls && lot.imageUrls.length > 0
      ? lot.imageUrls[0]
      : 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=600';

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Back button & Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="space-y-1">
          <Link
            to="/marketplace"
            className="inline-flex items-center text-xs font-semibold text-emerald-700 hover:text-emerald-800 transition-colors"
          >
            <ArrowLeft className="w-4 h-4 mr-1" />
            <span>Back to All Lots</span>
          </Link>
          <div className="flex items-center space-x-3 mt-1">
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
              {lot.crop} ({lot.variety} Grade {lot.grade})
            </h1>
            <StatusBadge status={lot.status} />
          </div>
          <p className="text-xs text-slate-500 flex items-center space-x-1">
            <MapPin className="w-3.5 h-3.5 text-slate-400" />
            <span>
              {lot.village}, {lot.district}, {lot.state} • Listed by Farmer <strong className="text-slate-700">{lot.farmerName}</strong>
            </span>
          </p>
        </div>

        {/* Advisor Quick Action Bar */}
        <div className="flex flex-wrap items-center gap-2.5">
          {advice && (
            <div className={`px-3 py-1.5 rounded-xl border text-xs font-bold flex items-center space-x-1.5 shadow-xs ${
              advice.recommendation === 'STORE'
                ? 'bg-blue-50 text-blue-800 border-blue-200'
                : advice.recommendation === 'SELL_NOW'
                ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                : 'bg-amber-50 text-amber-800 border-amber-200'
            }`}>
              <Scale className="w-3.5 h-3.5" />
              <span>Decision: {advice.recommendation} ({advice.confidence})</span>
            </div>
          )}

          <button
            type="button"
            onClick={() => setIsAdviceModalOpen(true)}
            className="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs shadow-sm transition-all flex items-center space-x-1.5"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>Sell vs. Store Advisor</span>
          </button>

          <button
            type="button"
            onClick={() => setIsAlertModalOpen(true)}
            className="px-3 py-2 rounded-xl bg-white hover:bg-slate-50 text-slate-700 border border-slate-300 font-bold text-xs shadow-xs transition-all flex items-center space-x-1.5"
          >
            <Bell className="w-3.5 h-3.5 text-amber-500" />
            <span>Set Price Alert</span>
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Left Column: Image, Specifications, Mandi Trend */}
        <div className="lg:col-span-7 space-y-6">
          {/* Main Lot Image Banner */}
          <div className="relative rounded-3xl overflow-hidden bg-slate-900 h-72 sm:h-96 shadow-md border border-slate-200">
            <img src={imageUrl} alt={lot.crop} className="w-full h-full object-cover" />
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-transparent to-transparent"></div>
            <div className="absolute bottom-4 left-4 right-4 flex flex-wrap items-center justify-between gap-2 text-white text-xs">
              <div className="flex items-center space-x-2">
                <span className="bg-emerald-600 px-3 py-1 rounded-lg font-bold">
                  {availableQty} {lot.unit} Available
                </span>
                <span className="bg-slate-900/80 backdrop-blur-md px-3 py-1 rounded-lg font-semibold flex items-center space-x-1">
                  <Droplets className="w-3.5 h-3.5 text-blue-400" />
                  <span>{lot.moisturePercentage}% Moisture</span>
                </span>
              </div>
              <span className="bg-slate-900/80 backdrop-blur-md px-3 py-1 rounded-lg">
                Closes: {new Date(lot.auctionClosingDate).toLocaleDateString('en-IN')}
              </span>
            </div>
          </div>

          {/* Description & Specs */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <h3 className="text-base font-bold text-slate-900">Lot Specifications & Logistics</h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              {lot.description || 'Cleaned, graded, and bagged farm produce available for direct farm-gate pickup or mandi delivery.'}
            </p>

            <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 pt-2">
              <div className="bg-slate-50 p-3 rounded-xl border border-slate-100">
                <p className="text-[11px] text-slate-500">Quality Grade</p>
                <p className="text-sm font-bold text-slate-800">Grade {lot.grade}</p>
              </div>

              <div className="bg-slate-50 p-3 rounded-xl border border-slate-100">
                <p className="text-[11px] text-slate-500">Min Reserve Price</p>
                <p className="text-sm font-extrabold text-emerald-700">₹{lot.minPricePerQuintal}/qtl</p>
              </div>

              <div className="bg-slate-50 p-3 rounded-xl border border-slate-100">
                <p className="text-[11px] text-slate-500">Storage Type</p>
                <p className="text-sm font-bold text-slate-800">{lot.storageRequirement || 'DRY_STORAGE'}</p>
              </div>

              <div className="bg-slate-50 p-3 rounded-xl border border-slate-100">
                <p className="text-[11px] text-slate-500">Expected Dispatch</p>
                <p className="text-sm font-semibold text-slate-800">
                  {lot.expectedDispatchDate ? new Date(lot.expectedDispatchDate).toLocaleDateString('en-IN') : 'Immediate'}
                </p>
              </div>

              <div className="bg-slate-50 p-3 rounded-xl border border-slate-100 sm:col-span-2">
                <p className="text-[11px] text-slate-500">Pickup Location</p>
                <p className="text-xs font-semibold text-slate-800 truncate">
                  {lot.pickupLocation || `${lot.village}, District ${lot.district}, Haryana`}
                </p>
              </div>
            </div>
          </div>

          {/* 30-Day Mandi Price Chart */}
          <PriceTrendChart
            data={trendData}
            msp={benchmark?.msp}
            cropName={lot.crop}
            districtName={lot.district}
          />
        </div>

        {/* Right Column: Mandi Benchmark Card, Bidding or Bids List */}
        <div className="lg:col-span-5 space-y-6">
          {/* APMC Mandi Benchmark Widget */}
          <PriceBenchmarkCard
            benchmark={benchmark}
            cropName={lot.crop}
            districtName={lot.district}
          />

          {/* Bidding Panel for Buyers */}
          {isBuyer && isLotOpen && (
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-md space-y-4">
              <div className="flex items-center space-x-2">
                <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-800 flex items-center justify-center">
                  <Gavel className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="font-bold text-slate-900 text-base">Place Your Digital Bid</h3>
                  <p className="text-xs text-slate-500">Submit competitive bid for farmer acceptance</p>
                </div>
              </div>

              {!isVerifiedBuyer && (
                <div className="p-3 bg-amber-50 border border-amber-200 rounded-xl text-xs text-amber-800 flex items-start space-x-2">
                  <AlertCircle className="w-4 h-4 text-amber-600 flex-shrink-0 mt-0.5" />
                  <p>
                    Your account is currently <strong className="text-amber-900">{user?.accountStatus || user?.status}</strong>. Only verified buyers can submit live bids.
                  </p>
                </div>
              )}

              {bidError && (
                <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-800">
                  {bidError}
                </div>
              )}

              {bidSuccess && (
                <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-800 flex items-center space-x-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                  <span>Bid successfully placed! The farmer has been notified.</span>
                </div>
              )}

              <form onSubmit={handlePlaceBid} className="space-y-4">
                <div>
                  <div className="flex justify-between items-center mb-1">
                    <label className="text-xs font-semibold text-slate-700 uppercase tracking-wider">
                      Bid Price (₹ / Quintal)
                    </label>
                    <span className="text-[11px] text-emerald-700 font-semibold">
                      Min: ₹{lot.minPricePerQuintal}
                    </span>
                  </div>
                  <div className="relative">
                    <input
                      type="number"
                      min={lot.minPricePerQuintal}
                      step="1"
                      required
                      value={bidPrice}
                      onChange={(e) => setBidPrice(parseFloat(e.target.value) || 0)}
                      className="w-full pl-9 pr-3 py-2.5 rounded-xl border border-slate-300 font-bold text-base focus:ring-2 focus:ring-emerald-500 text-slate-900"
                    />
                    <span className="absolute left-3.5 top-3 text-slate-400 font-bold text-sm">₹</span>
                  </div>
                </div>

                <div>
                  <div className="flex justify-between items-center mb-1">
                    <label className="text-xs font-semibold text-slate-700 uppercase tracking-wider">
                      Quantity (Quintals)
                    </label>
                    <span className="text-[11px] text-slate-500 font-medium">
                      Max: {availableQty} {lot.unit}
                    </span>
                  </div>
                  <div className="relative">
                    <input
                      type="number"
                      min="1"
                      max={availableQty}
                      step="1"
                      required
                      value={bidQuantity}
                      onChange={(e) => setBidQuantity(parseFloat(e.target.value) || 0)}
                      className="w-full pl-9 pr-3 py-2.5 rounded-xl border border-slate-300 font-bold text-base focus:ring-2 focus:ring-emerald-500 text-slate-900"
                    />
                    <Scale className="w-4 h-4 text-slate-400 absolute left-3 top-3.5" />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                    Bid Terms / Remarks
                  </label>
                  <input
                    type="text"
                    value={bidRemarks}
                    onChange={(e) => setBidRemarks(e.target.value)}
                    placeholder="e.g. Willing to pick up from farm gate within 48h."
                    className="w-full p-2.5 rounded-xl border border-slate-300 text-xs focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                {/* Token Balance & Deposit Helper */}
                {isVerifiedBuyer && (
                  <div className="p-3 bg-emerald-50/70 border border-emerald-200 rounded-xl flex items-center justify-between text-xs">
                    <div className="flex items-center space-x-2">
                      <Coins className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                      <div>
                        <span className="text-[10px] text-slate-500 font-semibold block uppercase">Available Tokens</span>
                        <span className="font-extrabold text-emerald-800">
                          ₹{(wallet?.availableBalance ?? 0).toLocaleString('en-IN')}
                        </span>
                      </div>
                    </div>
                    <button
                      type="button"
                      onClick={() => setIsWalletOpen(true)}
                      className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-[11px] rounded-lg shadow-xs transition-colors"
                    >
                      + Deposit Tokens
                    </button>
                  </div>
                )}

                <div className="bg-slate-50 p-3 rounded-xl border border-slate-200 flex items-center justify-between">
                  <span className="text-xs text-slate-600 font-medium">Total Bid Value:</span>
                  <span className="text-lg font-black text-slate-900">
                    ₹{(bidPrice * bidQuantity).toLocaleString('en-IN')}
                  </span>
                </div>

                <button
                  type="submit"
                  disabled={!isVerifiedBuyer || bidSubmitting}
                  className="w-full py-3 px-4 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm shadow-md shadow-emerald-700/20 transition-all flex items-center justify-center space-x-2 disabled:opacity-50 cursor-pointer"
                >
                  {bidSubmitting ? (
                    <span>Submitting Bid...</span>
                  ) : (
                    <>
                      <Gavel className="w-4 h-4" />
                      <span>Authorize & Submit Bid (Lock Tokens)</span>
                    </>
                  )}
                </button>
              </form>
            </div>
          )}

          {/* Prompt to login for non-authenticated or farmer */}
          {!isAuthenticated && (
            <div className="bg-gradient-to-br from-slate-900 to-emerald-950 text-white p-6 rounded-2xl border border-slate-800 shadow-md text-center space-y-4">
              <Gavel className="w-10 h-10 text-emerald-400 mx-auto" />
              <div>
                <h3 className="font-bold text-lg">Want to Bid on this Lot?</h3>
                <p className="text-xs text-slate-300 mt-1">
                  Login or create a verified Buyer account to place offers with simulated escrow protection.
                </p>
              </div>
              <Link
                to="/login"
                className="inline-flex items-center space-x-2 px-6 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-colors"
              >
                <span>Login to Bid</span>
              </Link>
            </div>
          )}

          {/* Live Bids List */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <div className="flex items-center space-x-2">
                <Gavel className="w-5 h-5 text-emerald-600" />
                <h3 className="font-bold text-slate-900 text-base">
                  Current Bids ({bids.length})
                </h3>
              </div>
              <span className="text-xs text-slate-500">Ranked by Price</span>
            </div>

            {bids.length === 0 ? (
              <div className="text-center py-6 text-slate-500 text-xs">
                No bids placed on this lot yet. Be the first to place an offer!
              </div>
            ) : (
              <div className="space-y-3 max-h-96 overflow-y-auto pr-1">
                {bids.map((bid, index) => (
                  <div
                    key={bid.id}
                    className={`p-3.5 rounded-xl border text-xs transition-all ${
                      bid.status === 'ACCEPTED'
                        ? 'bg-emerald-50 border-emerald-300 ring-2 ring-emerald-500/20'
                        : index === 0
                        ? 'bg-slate-50/80 border-slate-200'
                        : 'bg-white border-slate-100'
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-2">
                        <span className="w-5 h-5 rounded-full bg-slate-200 text-slate-700 font-bold flex items-center justify-center text-[10px]">
                          #{index + 1}
                        </span>
                        <span className="font-bold text-slate-900">{bid.buyerName}</span>
                      </div>
                      <StatusBadge status={bid.status} />
                    </div>

                    <div className="grid grid-cols-2 gap-2 mt-2 pt-2 border-t border-slate-100">
                      <div>
                        <p className="text-[10px] text-slate-500">Offer Price</p>
                        <p className="font-extrabold text-emerald-700 text-sm">
                          ₹{bid.bidPricePerQuintal.toLocaleString('en-IN')} / qtl
                        </p>
                      </div>
                      <div>
                        <p className="text-[10px] text-slate-500">Quantity & Total</p>
                        <p className="font-semibold text-slate-800">
                          {bid.quantityQuintals} qtl • ₹{bid.totalAmount.toLocaleString('en-IN')}
                        </p>
                      </div>
                    </div>

                    {bid.remarks && (
                      <p className="text-[11px] text-slate-500 italic mt-1.5">
                        "{bid.remarks}"
                      </p>
                    )}

                    {/* Farmer Action: Accept Bid */}
                    {isFarmerOwner && isLotOpen && bid.status === 'ACTIVE' && (
                      <div className="mt-3 pt-2 border-t border-slate-200 flex justify-end">
                        <button
                          type="button"
                          onClick={() => setSelectedBidForAccept(bid)}
                          className="px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs transition-colors flex items-center space-x-1"
                        >
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          <span>Accept Offer & Create Contract</span>
                        </button>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Accept Bid Modal */}
      {selectedBidForAccept && (
        <AcceptBidModal
          bid={selectedBidForAccept}
          isOpen={!!selectedBidForAccept}
          onClose={() => setSelectedBidForAccept(null)}
          onSuccess={() => {
            fetchLotDetails();
            navigate('/farmer/contracts');
          }}
        />
      )}

      {/* Security Vault Deposit & Token Modal */}
      <WalletModal
        isOpen={isWalletOpen}
        onClose={() => {
          setIsWalletOpen(false);
          fetchWallet();
        }}
        defaultTab="DEPOSIT"
      />

      {/* Smart Sell-or-Store Advisor Modal */}
      {lot && (
        <SellOrStoreComparisonModal
          lot={lot}
          isOpen={isAdviceModalOpen}
          onClose={() => setIsAdviceModalOpen(false)}
          onAcceptBid={(lot) => {
            if (bids.length > 0) {
              setSelectedBidForAccept(bids[0]);
            }
          }}
          onBookWarehouse={() => navigate('/warehouses')}
          onOpenPriceAlert={(crop, district, targetPrice) => {
            setIsAlertModalOpen(true);
          }}
        />
      )}

      {/* Target-Price Alert Modal */}
      {lot && (
        <PriceAlertModal
          isOpen={isAlertModalOpen}
          onClose={() => setIsAlertModalOpen(false)}
          defaultCrop={lot.crop}
          defaultDistrict={lot.district}
          defaultTargetPrice={advice ? Math.round(advice.estimatedFuturePrice) : lot.minPricePerQuintal}
        />
      )}
    </div>
  );
};
