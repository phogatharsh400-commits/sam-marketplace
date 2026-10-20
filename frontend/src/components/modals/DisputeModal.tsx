import React, { useState } from 'react';
import { X, AlertTriangle, ShieldAlert } from 'lucide-react';
import { disputeApi } from '../../api';

interface DisputeModalProps {
  contractId: string | number;
  contractNumber: string;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const DisputeModal: React.FC<DisputeModalProps> = ({
  contractId,
  contractNumber,
  isOpen,
  onClose,
  onSuccess,
}) => {
  const [reason, setReason] = useState('QUALITY_MISMATCH');
  const [description, setDescription] = useState('');
  const [requestedResolution, setRequestedResolution] = useState('FULL_REFUND');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await disputeApi.createDispute({
        contractId,
        reason: `${reason}: ${description.substring(0, 50)}`,
        description,
        requestedResolution,
        evidenceFileUrls: [
          'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=600'
        ]
      });

      onSuccess();
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to file dispute.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl max-w-lg w-full shadow-2xl border border-slate-200 overflow-hidden">
        <div className="bg-rose-900 text-white p-5 flex items-center justify-between">
          <div className="flex items-center space-x-2.5">
            <div className="w-8 h-8 rounded-lg bg-rose-800 text-rose-200 flex items-center justify-center border border-rose-700">
              <ShieldAlert className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-base">File Dispute Case</h3>
              <p className="text-xs text-rose-300">Contract #{contractNumber}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-rose-300 hover:text-white hover:bg-rose-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 rounded-lg text-xs">
              {error}
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              Dispute Category
            </label>
            <select
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-rose-500"
            >
              <option value="QUALITY_MISMATCH">Quality / Moisture Level Mismatch</option>
              <option value="WEIGHT_SHORTAGE">Weighbridge Shortage</option>
              <option value="DISPATCH_DELAY">Dispatch or Delivery Delay</option>
              <option value="TRANSIT_DAMAGE">Goods Damaged In Transit</option>
              <option value="OTHER">Other Contractual Breach</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              Requested Resolution
            </label>
            <select
              value={requestedResolution}
              onChange={(e) => setRequestedResolution(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-rose-500"
            >
              <option value="FULL_REFUND">Full Refund to Buyer</option>
              <option value="PARTIAL_PRICE_REDUCTION">Partial Escrow Split / Price Reduction</option>
              <option value="REPLACE_LOT">Lot Replacement</option>
              <option value="FULL_PAYMENT_TO_FARMER">Full Escrow Release to Farmer</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              Detailed Description & Evidence Rationale
            </label>
            <textarea
              rows={4}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Provide specific details regarding the breach, lab test results, or delivery inspection..."
              required
              className="w-full p-3 rounded-xl border border-slate-300 focus:ring-2 focus:ring-rose-500 text-sm"
            />
          </div>

          <div className="p-3 bg-amber-50 rounded-xl border border-amber-200 text-xs text-amber-800 flex items-start space-x-2">
            <AlertTriangle className="w-4 h-4 text-amber-600 flex-shrink-0 mt-0.5" />
            <p>
              Once submitted, an Administrator will audit the case, examine photographic evidence, and adjudicate the escrow funds.
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
              className="w-2/3 py-2.5 px-4 rounded-xl bg-rose-600 hover:bg-rose-700 text-white font-semibold text-sm shadow-md shadow-rose-700/20 transition-all flex items-center justify-center space-x-2 disabled:opacity-50"
            >
              {loading ? <span>Submitting...</span> : <span>Submit Dispute Case</span>}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
