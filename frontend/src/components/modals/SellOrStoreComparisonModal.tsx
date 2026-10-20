import React, { useState, useEffect } from 'react';
import {
  X,
  TrendingUp,
  Warehouse,
  IndianRupee,
  AlertTriangle,
  CheckCircle2,
  Clock,
  ArrowRight,
  ShieldCheck,
  RefreshCw,
  Bell,
  Scale,
  Sparkles,
  Info,
  Calendar
} from 'lucide-react';
import { advisorApi } from '../../api';
import { LotDto, StorageAdviceDto, AdvisorRecommendation } from '../../types';

interface SellOrStoreComparisonModalProps {
  lot: LotDto;
  isOpen: boolean;
  onClose: () => void;
  onAcceptBid?: (lot: LotDto) => void;
  onBookWarehouse?: (lot: LotDto) => void;
  onOpenPriceAlert?: (crop: string, district: string, targetPrice: number) => void;
}

export const SellOrStoreComparisonModal: React.FC<SellOrStoreComparisonModalProps> = ({
  lot,
  isOpen,
  onClose,
  onAcceptBid,
  onBookWarehouse,
  onOpenPriceAlert,
}) => {
  const [advice, setAdvice] = useState<StorageAdviceDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (isOpen && lot?.id) {
      loadAdvice();
    }
  }, [isOpen, lot?.id]);

  const loadAdvice = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await advisorApi.getAdvice(lot.id);
      if (res.data) {
        setAdvice(res.data);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to fetch crop advice.');
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    setRefreshing(true);
    setError(null);
    try {
      const res = await advisorApi.generateAdvice(lot.id);
      if (res.data) {
        setAdvice(res.data);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to recalculate advice.');
    } finally {
      setRefreshing(false);
    }
  };

  if (!isOpen) return null;

  const getRecommendationBadge = (rec?: AdvisorRecommendation) => {
    switch (rec) {
      case 'STORE':
        return {
          title: 'RECOMMENDATION: STORE IN WAREHOUSE',
          subtitle: 'Strong price upside forecasted — hold produce for higher off-season returns.',
          bg: 'bg-gradient-to-r from-blue-700 via-indigo-700 to-indigo-800 text-white',
          badge: 'bg-blue-500/30 text-blue-100 border-blue-400/40',
          icon: Warehouse,
        };
      case 'SELL_NOW':
        return {
          title: 'RECOMMENDATION: SELL NOW',
          subtitle: 'Active market bids offer peak value — lock in guaranteed cash without storage cost.',
          bg: 'bg-gradient-to-r from-emerald-700 via-teal-700 to-emerald-800 text-white',
          badge: 'bg-emerald-500/30 text-emerald-100 border-emerald-400/40',
          icon: CheckCircle2,
        };
      case 'WAIT_AND_MONITOR':
      default:
        return {
          title: 'RECOMMENDATION: WAIT & MONITOR',
          subtitle: 'Market is in consolidation — observe mandi arrival trends for 3-5 days before deciding.',
          bg: 'bg-gradient-to-r from-amber-600 via-orange-600 to-amber-700 text-white',
          badge: 'bg-amber-500/30 text-amber-100 border-amber-400/40',
          icon: Clock,
        };
    }
  };

  const recMeta = getRecommendationBadge(advice?.recommendation);
  const IconComponent = recMeta.icon;

  const currentSellPrice = advice?.currentBestBidPrice && advice.currentBestBidPrice > 0
    ? advice.currentBestBidPrice
    : (advice?.currentMandiModalPrice || lot.minPricePerQuintal || 0);

  const futurePrice = advice?.estimatedFuturePrice || currentSellPrice * 1.05;
  const storageAndHandling = advice?.totalStorageAndHandlingCost || 35;
  const netStorageRealization = futurePrice - storageAndHandling;
  const netGainPerQuintal = advice?.netExpectedGainPerQuintal ?? (netStorageRealization - currentSellPrice);
  const totalBenefit = advice?.totalEstimatedBenefit ?? (netGainPerQuintal * (lot.quantityQuintals || 1));

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl max-w-4xl w-full shadow-2xl border border-slate-200 overflow-hidden animate-in fade-in zoom-in duration-200">
        {/* Top Header */}
        <div className="bg-slate-900 text-white p-5 flex items-center justify-between border-b border-slate-800">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center border border-emerald-500/30">
              <Scale className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <h3 className="font-bold text-lg">Smart Sell-or-Store Advisor</h3>
                <span className="px-2 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                  Decision Engine
                </span>
              </div>
              <p className="text-xs text-slate-400">
                Produce Lot #{lot.lotNumber} • {lot.crop} ({lot.variety}) • {lot.quantityQuintals} Quintals • {lot.district}
              </p>
            </div>
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={handleRefresh}
              disabled={refreshing || loading}
              title="Recalculate with live bids"
              className="px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium flex items-center space-x-1.5 transition-colors border border-slate-700 disabled:opacity-50"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${refreshing ? 'animate-spin' : ''}`} />
              <span>{refreshing ? 'Analyzing...' : 'Recalculate'}</span>
            </button>
            <button
              onClick={onClose}
              className="p-1.5 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        <div className="p-6 max-h-[80vh] overflow-y-auto space-y-6">
          {loading ? (
            <div className="py-16 text-center space-y-4">
              <div className="w-12 h-12 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin mx-auto" />
              <p className="text-sm font-medium text-slate-600">Analyzing mandi arrivals, 30-day trends, and warehouse costs...</p>
            </div>
          ) : error ? (
            <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl flex items-center space-x-3">
              <AlertTriangle className="w-5 h-5 shrink-0" />
              <div className="text-sm font-medium">{error}</div>
            </div>
          ) : (
            <>
              {/* Recommendation Banner */}
              <div className={`rounded-2xl p-5 ${recMeta.bg} shadow-lg relative overflow-hidden`}>
                <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 relative z-10">
                  <div className="flex items-start space-x-3.5">
                    <div className="w-12 h-12 rounded-2xl bg-white/10 backdrop-blur-xs flex items-center justify-center border border-white/20 shrink-0">
                      <IconComponent className="w-6 h-6 text-white" />
                    </div>
                    <div>
                      <div className="flex flex-wrap items-center gap-2 mb-1">
                        <span className="font-extrabold text-lg tracking-wide">{recMeta.title}</span>
                        <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold border uppercase ${recMeta.badge}`}>
                          {advice?.confidence || 'HIGH'} Confidence
                        </span>
                      </div>
                      <p className="text-xs text-white/90 leading-relaxed max-w-2xl">{recMeta.subtitle}</p>
                    </div>
                  </div>

                  <div className="bg-black/20 backdrop-blur-xs rounded-xl p-3 border border-white/15 shrink-0 text-right md:min-w-[170px]">
                    <div className="text-[11px] uppercase tracking-wider text-white/75 font-semibold">
                      Estimated Net Benefit
                    </div>
                    <div className="text-2xl font-black text-white flex items-center justify-end mt-0.5">
                      <span>{netGainPerQuintal >= 0 ? '+' : ''}₹{Math.abs(Math.round(totalBenefit)).toLocaleString('en-IN')}</span>
                    </div>
                    <div className="text-[11px] text-white/80 font-medium">
                      ({netGainPerQuintal >= 0 ? '+' : ''}₹{Math.round(netGainPerQuintal).toLocaleString('en-IN')}/qtl)
                    </div>
                  </div>
                </div>
              </div>

              {/* Side-by-Side Comparison */}
              <div>
                <h4 className="text-sm font-bold text-slate-800 mb-3 flex items-center space-x-2">
                  <Sparkles className="w-4 h-4 text-emerald-600" />
                  <span>Comprehensive Sell vs. Store Breakdown</span>
                </h4>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {/* Option A: Sell Now */}
                  <div className={`rounded-2xl p-5 border transition-all ${
                    advice?.recommendation === 'SELL_NOW'
                      ? 'bg-emerald-50/50 border-emerald-300 ring-2 ring-emerald-500/20 shadow-sm'
                      : 'bg-slate-50/80 border-slate-200'
                  }`}>
                    <div className="flex items-center justify-between mb-4">
                      <div className="flex items-center space-x-2">
                        <div className="w-7 h-7 rounded-lg bg-emerald-600 text-white flex items-center justify-center font-bold text-xs">
                          A
                        </div>
                        <div>
                          <h5 className="font-bold text-slate-800 text-sm">Option A: Sell Now</h5>
                          <span className="text-[11px] text-slate-500">Immediate spot settlement</span>
                        </div>
                      </div>
                      {advice?.recommendation === 'SELL_NOW' && (
                        <span className="px-2 py-0.5 bg-emerald-100 text-emerald-800 font-bold text-[11px] rounded-full border border-emerald-300">
                          Recommended
                        </span>
                      )}
                    </div>

                    <div className="space-y-2.5 text-xs">
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Effective Sell Price:</span>
                        <span className="font-bold text-slate-900">
                          ₹{currentSellPrice.toLocaleString('en-IN')} / qtl
                        </span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Highest Buyer Bid:</span>
                        <span className="font-semibold text-emerald-700">
                          {advice?.currentBestBidPrice && advice.currentBestBidPrice > 0
                            ? `₹${advice.currentBestBidPrice.toLocaleString('en-IN')} / qtl`
                            : 'No bids yet (Mandi Base)'}
                        </span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Mandi Modal Price:</span>
                        <span className="font-semibold text-slate-800">
                          ₹{(advice?.currentMandiModalPrice || 0).toLocaleString('en-IN')} / qtl
                        </span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Government MSP:</span>
                        <span className="font-semibold text-slate-800">
                          ₹{(advice?.mspPrice || 0).toLocaleString('en-IN')} / qtl
                        </span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Warehouse Storage Cost:</span>
                        <span className="font-bold text-emerald-600">₹0.00 (Zero)</span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Payment Timeline:</span>
                        <span className="font-semibold text-slate-800">T+2 Days via Buyer Escrow</span>
                      </div>
                      <div className="flex justify-between pt-2">
                        <span className="font-bold text-slate-800 text-sm">Gross Realization:</span>
                        <span className="font-extrabold text-slate-900 text-sm">
                          ₹{(currentSellPrice * (lot.quantityQuintals || 1)).toLocaleString('en-IN')}
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* Option B: Store in Warehouse */}
                  <div className={`rounded-2xl p-5 border transition-all ${
                    advice?.recommendation === 'STORE'
                      ? 'bg-blue-50/50 border-blue-300 ring-2 ring-blue-500/20 shadow-sm'
                      : 'bg-slate-50/80 border-slate-200'
                  }`}>
                    <div className="flex items-center justify-between mb-4">
                      <div className="flex items-center space-x-2">
                        <div className="w-7 h-7 rounded-lg bg-indigo-600 text-white flex items-center justify-center font-bold text-xs">
                          B
                        </div>
                        <div>
                          <h5 className="font-bold text-slate-800 text-sm">Option B: Store & Sell Later</h5>
                          <span className="text-[11px] text-slate-500">Hold for 30 days & pledge e-NWR</span>
                        </div>
                      </div>
                      {advice?.recommendation === 'STORE' && (
                        <span className="px-2 py-0.5 bg-blue-100 text-blue-800 font-bold text-[11px] rounded-full border border-blue-300">
                          Recommended
                        </span>
                      )}
                    </div>

                    <div className="space-y-2.5 text-xs">
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">30-Day Forecast Price:</span>
                        <span className="font-bold text-indigo-700">
                          ₹{Math.round(futurePrice).toLocaleString('en-IN')} / qtl
                        </span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Est. Storage Fee (1 mo):</span>
                        <span className="font-semibold text-slate-700">
                          -₹{(advice?.monthlyStorageCost || 20).toLocaleString('en-IN')} / qtl
                        </span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Handling & In/Out Fee:</span>
                        <span className="font-semibold text-slate-700">
                          -₹{(advice?.handlingCost || 15).toLocaleString('en-IN')} / qtl
                        </span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Net Expected Realization:</span>
                        <span className="font-bold text-slate-900">
                          ₹{Math.round(netStorageRealization).toLocaleString('en-IN')} / qtl
                        </span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">e-NWR Advance Loan:</span>
                        <span className="font-semibold text-emerald-700">
                          Up to 70% Instant Cash (₹{Math.round(currentSellPrice * 0.70 * (lot.quantityQuintals || 1)).toLocaleString('en-IN')})
                        </span>
                      </div>
                      <div className="flex justify-between py-1.5 border-b border-slate-200">
                        <span className="text-slate-600">Net Advantage vs Option A:</span>
                        <span className={`font-extrabold ${netGainPerQuintal >= 0 ? 'text-emerald-700' : 'text-rose-700'}`}>
                          {netGainPerQuintal >= 0 ? '+' : ''}₹{Math.round(netGainPerQuintal).toLocaleString('en-IN')} / qtl
                        </span>
                      </div>
                      <div className="flex justify-between pt-2">
                        <span className="font-bold text-slate-800 text-sm">Est. Net Final Value:</span>
                        <span className="font-extrabold text-slate-900 text-sm">
                          ₹{Math.round(netStorageRealization * (lot.quantityQuintals || 1)).toLocaleString('en-IN')}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* Rationale & Insights */}
              {advice?.reasons && advice.reasons.length > 0 && (
                <div className="bg-slate-50 rounded-2xl p-4 border border-slate-200">
                  <h5 className="font-bold text-xs text-slate-700 uppercase tracking-wider mb-2 flex items-center space-x-1.5">
                    <Info className="w-3.5 h-3.5 text-slate-500" />
                    <span>Key Decision Factors</span>
                  </h5>
                  <ul className="space-y-1.5 text-xs text-slate-600">
                    {advice.reasons.map((reason, idx) => (
                      <li key={idx} className="flex items-start space-x-2">
                        <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 mt-1.5 shrink-0" />
                        <span>{reason}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* Disclaimer */}
              <div className="bg-amber-50/80 border border-amber-200 rounded-2xl p-4 text-xs text-amber-900 flex items-start space-x-3">
                <AlertTriangle className="w-4 h-4 text-amber-600 shrink-0 mt-0.5" />
                <p className="leading-relaxed">
                  <span className="font-bold">Prototype Decision Support Disclaimer: </span>
                  {advice?.disclaimer ||
                    'This decision recommendation is generated using 7-day moving average trend modeling and estimated WDRA storage tariffs for prototype demonstration. It does not constitute guaranteed financial or trading advice.'}
                </p>
              </div>
            </>
          )}
        </div>

        {/* Footer Actions */}
        <div className="bg-slate-50 p-5 border-t border-slate-200 flex flex-wrap items-center justify-between gap-3">
          <button
            onClick={() => {
              if (onOpenPriceAlert) {
                onOpenPriceAlert(lot.crop, lot.district, Math.round(futurePrice));
              }
            }}
            className="px-4 py-2.5 rounded-xl bg-white border border-slate-300 hover:bg-slate-100 text-slate-700 text-xs font-bold flex items-center space-x-2 transition-colors shadow-xs"
          >
            <Bell className="w-4 h-4 text-amber-500" />
            <span>Set Target Price Alert</span>
          </button>

          <div className="flex items-center space-x-3">
            {onBookWarehouse && (
              <button
                onClick={() => {
                  onBookWarehouse(lot);
                  onClose();
                }}
                className={`px-4 py-2.5 rounded-xl text-xs font-bold flex items-center space-x-1.5 transition-all shadow-xs ${
                  advice?.recommendation === 'STORE'
                    ? 'bg-indigo-600 hover:bg-indigo-700 text-white shadow-indigo-200'
                    : 'bg-white border border-slate-300 hover:bg-slate-100 text-slate-800'
                }`}
              >
                <Warehouse className="w-4 h-4" />
                <span>Book Certified Warehouse</span>
              </button>
            )}

            {onAcceptBid && lot.highestBid && lot.highestBid > 0 && (
              <button
                onClick={() => {
                  onAcceptBid(lot);
                  onClose();
                }}
                className={`px-4 py-2.5 rounded-xl text-xs font-bold flex items-center space-x-1.5 transition-all shadow-xs ${
                  advice?.recommendation === 'SELL_NOW'
                    ? 'bg-emerald-600 hover:bg-emerald-700 text-white shadow-emerald-200'
                    : 'bg-white border border-slate-300 hover:bg-slate-100 text-slate-800'
                }`}
              >
                <CheckCircle2 className="w-4 h-4" />
                <span>Accept Highest Bid</span>
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
