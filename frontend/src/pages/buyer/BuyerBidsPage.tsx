import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Gavel, Sprout, ArrowRight, CheckCircle2, Clock, AlertCircle } from 'lucide-react';
import { bidApi } from '../../api';
import { BidResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';

export const BuyerBidsPage: React.FC = () => {
  const [bids, setBids] = useState<BidResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchBids = async () => {
    setLoading(true);
    try {
      const res = await bidApi.getMyBids();
      setBids(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load your bids.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBids();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-black text-slate-900">My Bid Offers</h1>
          <p className="text-xs text-slate-500">
            Track your bids across active produce lots in Haryana
          </p>
        </div>
        <Link
          to="/marketplace"
          className="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold shadow-xs transition-colors"
        >
          <Sprout className="w-4 h-4" />
          <span>Explore Open Lots</span>
        </Link>
      </div>

      {loading ? (
        <div className="py-12 flex justify-center">
          <div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-600 border-t-transparent"></div>
        </div>
      ) : bids.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center max-w-md mx-auto space-y-3">
          <Gavel className="w-12 h-12 text-slate-400 mx-auto" />
          <h3 className="text-base font-bold text-slate-800">No Bids Placed</h3>
          <p className="text-xs text-slate-500">
            Browse available produce lots on the digital marketplace and submit your purchase bids.
          </p>
          <Link
            to="/marketplace"
            className="inline-flex items-center space-x-1.5 px-4 py-2 rounded-xl bg-emerald-600 text-white text-xs font-bold mt-2"
          >
            <span>Browse Lots</span>
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {bids.map((bid) => {
            const dateStr = bid.createdAt || bid.bidTime || new Date().toISOString();
            return (
              <div
                key={bid.id}
                className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-3 flex flex-col md:flex-row md:items-center justify-between gap-4"
              >
                <div className="space-y-2">
                  <div className="flex items-center space-x-3">
                    <span className="font-bold text-slate-900 text-base">
                      {bid.crop} ({bid.variety})
                    </span>
                    <StatusBadge status={bid.status} />
                  </div>
                  <div className="flex flex-wrap gap-4 text-xs text-slate-600">
                    <span>Bid Price: <strong className="text-emerald-700">₹{bid.bidPricePerQuintal}/qtl</strong></span>
                    <span>Quantity: <strong className="text-slate-800">{bid.quantityQuintals} Quintals</strong></span>
                    <span>Total Bid Value: <strong className="text-slate-900">₹{bid.totalAmount.toLocaleString('en-IN')}</strong></span>
                    <span>Placed: <strong>{new Date(dateStr).toLocaleDateString('en-IN')}</strong></span>
                  </div>
                  {bid.remarks && (
                    <p className="text-[11px] text-slate-500 italic">
                      Terms: "{bid.remarks}"
                    </p>
                  )}
                </div>

                <div className="flex items-center space-x-3 flex-shrink-0">
                  <Link
                    to={`/lots/${bid.lotId}`}
                    className="px-4 py-2 rounded-xl bg-slate-900 hover:bg-emerald-600 text-white text-xs font-bold transition-colors flex items-center space-x-1.5"
                  >
                    <span>View Lot Details</span>
                    <ArrowRight className="w-3.5 h-3.5" />
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
