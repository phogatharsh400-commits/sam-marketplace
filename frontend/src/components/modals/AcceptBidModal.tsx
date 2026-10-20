import React, { useState } from 'react';
import { X, CheckCircle2, ShieldCheck, Calendar, Truck, FileText } from 'lucide-react';
import { bidApi } from '../../api';
import { BidResponse } from '../../types';

interface AcceptBidModalProps {
  bid: BidResponse;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const AcceptBidModal: React.FC<AcceptBidModalProps> = ({
  bid,
  isOpen,
  onClose,
  onSuccess,
}) => {
  const [deliveryTerms, setDeliveryTerms] = useState(
    'Ex-Farm Gate pickup within 5 business days after escrow verification.'
  );
  const [dispatchDeadlineDays, setDispatchDeadlineDays] = useState(5);
  const [deliveryDeadlineDays, setDeliveryDeadlineDays] = useState(10);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const dispatchDeadline = new Date(Date.now() + dispatchDeadlineDays * 24 * 60 * 60 * 1000)
    .toISOString()
    .split('T')[0];
  const deliveryDeadline = new Date(Date.now() + deliveryDeadlineDays * 24 * 60 * 60 * 1000)
    .toISOString()
    .split('T')[0];

  const handleAccept = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await bidApi.acceptBid(bid.lotId, bid.id, {
        deliveryTerms,
        dispatchDeadline: `${dispatchDeadline}T18:00:00`,
        deliveryDeadline: `${deliveryDeadline}T18:00:00`,
      });

      onSuccess();
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to accept bid.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl max-w-lg w-full shadow-2xl border border-slate-200 overflow-hidden animate-in fade-in zoom-in duration-200">
        <div className="bg-emerald-950 text-white p-5 flex items-center justify-between">
          <div className="flex items-center space-x-2.5">
            <div className="w-8 h-8 rounded-lg bg-emerald-500/20 text-emerald-400 flex items-center justify-center border border-emerald-500/30">
              <CheckCircle2 className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-base">Accept Buyer Bid</h3>
              <p className="text-xs text-emerald-300">Generate Formal Sale Contract</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-emerald-300 hover:text-white hover:bg-emerald-900 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleAccept} className="p-6 space-y-4">
          {error && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 rounded-lg text-xs">
              {error}
            </div>
          )}

          {/* Bid details card */}
          <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-2">
            <div className="flex justify-between items-center text-sm">
              <span className="text-slate-500">Buyer Name:</span>
              <span className="font-bold text-slate-800">{bid.buyerName}</span>
            </div>
            <div className="flex justify-between items-center text-sm">
              <span className="text-slate-500">Bid Offer Price:</span>
              <span className="font-extrabold text-emerald-700">₹{bid.bidPricePerQuintal.toLocaleString('en-IN')} / Quintal</span>
            </div>
            <div className="flex justify-between items-center text-sm">
              <span className="text-slate-500">Bid Quantity:</span>
              <span className="font-bold text-slate-800">{bid.quantityQuintals} Quintals</span>
            </div>
            <div className="border-t border-slate-200 pt-2 flex justify-between items-center">
              <span className="text-xs text-slate-500 uppercase font-semibold">Total Contract Value:</span>
              <span className="text-lg font-black text-slate-900">₹{bid.totalAmount.toLocaleString('en-IN')}</span>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              Delivery Terms
            </label>
            <textarea
              rows={2}
              value={deliveryTerms}
              onChange={(e) => setDeliveryTerms(e.target.value)}
              required
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
                Dispatch Deadline
              </label>
              <div className="relative">
                <input
                  type="date"
                  value={dispatchDeadline}
                  onChange={() => {}}
                  disabled
                  className="w-full pl-8 pr-2 py-2 rounded-xl bg-slate-100 border border-slate-300 text-xs font-medium text-slate-700"
                />
                <Calendar className="w-3.5 h-3.5 text-slate-400 absolute left-2.5 top-2.5" />
              </div>
              <p className="text-[10px] text-slate-500 mt-1">+5 days from escrow funding</p>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
                Delivery Deadline
              </label>
              <div className="relative">
                <input
                  type="date"
                  value={deliveryDeadline}
                  onChange={() => {}}
                  disabled
                  className="w-full pl-8 pr-2 py-2 rounded-xl bg-slate-100 border border-slate-300 text-xs font-medium text-slate-700"
                />
                <Calendar className="w-3.5 h-3.5 text-slate-400 absolute left-2.5 top-2.5" />
              </div>
              <p className="text-[10px] text-slate-500 mt-1">+10 days from escrow funding</p>
            </div>
          </div>

          <div className="p-3 bg-emerald-50 rounded-xl border border-emerald-100 text-xs text-emerald-900 flex items-start space-x-2">
            <ShieldCheck className="w-4 h-4 text-emerald-600 flex-shrink-0 mt-0.5" />
            <p>
              Accepting this bid locks the lot, rejects other open bids, and notifies the buyer to fund the simulated escrow account.
            </p>
          </div>

          <div className="flex items-center space-x-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="w-1/3 py-2.5 px-4 rounded-xl border border-slate-300 text-slate-700 font-medium text-sm hover:bg-slate-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="w-2/3 py-2.5 px-4 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm shadow-md shadow-emerald-700/20 transition-all flex items-center justify-center space-x-2 disabled:opacity-50"
            >
              {loading ? <span>Generating Contract...</span> : <span>Confirm & Accept Bid</span>}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
