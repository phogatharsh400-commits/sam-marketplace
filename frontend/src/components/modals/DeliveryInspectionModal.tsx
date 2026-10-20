import React, { useState } from 'react';
import { X, CheckCircle, AlertTriangle, Truck, Scale, Camera } from 'lucide-react';
import { deliveryApi } from '../../api';

interface DeliveryInspectionModalProps {
  contractId: string | number;
  contractNumber: string;
  expectedQuantity: number;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const DeliveryInspectionModal: React.FC<DeliveryInspectionModalProps> = ({
  contractId,
  contractNumber,
  expectedQuantity,
  isOpen,
  onClose,
  onSuccess,
}) => {
  const [actualQuantity, setActualQuantity] = useState(expectedQuantity);
  const [remarks, setRemarks] = useState('');
  const [hasQualityIssue, setHasQualityIssue] = useState(false);
  const [issueDescription, setIssueDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await deliveryApi.recordDelivery(contractId, {
        actualQuantityDelivered: actualQuantity,
        remarks: remarks || (hasQualityIssue ? issueDescription : 'Goods received in satisfactory condition.'),
        hasQualityIssue,
        issueDescription: hasQualityIssue ? issueDescription : undefined,
        weightSlipUrl: 'https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=600',
        deliveryPhotoUrls: [
          'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=600'
        ]
      });

      onSuccess();
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to record delivery.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl max-w-lg w-full shadow-2xl border border-slate-200 overflow-hidden animate-in fade-in zoom-in duration-200">
        <div className="bg-slate-900 text-white p-5 flex items-center justify-between">
          <div className="flex items-center space-x-2.5">
            <div className="w-8 h-8 rounded-lg bg-emerald-500/20 text-emerald-400 flex items-center justify-center border border-emerald-500/30">
              <Truck className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-base">Delivery Inspection & Confirmation</h3>
              <p className="text-xs text-slate-400">Contract #{contractNumber}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          {error && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 rounded-lg text-xs">
              {error}
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              Verified Delivered Quantity (Quintals)
            </label>
            <div className="relative">
              <input
                type="number"
                step="0.01"
                min="0.1"
                value={actualQuantity}
                onChange={(e) => setActualQuantity(parseFloat(e.target.value))}
                required
                className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm font-medium"
              />
              <Scale className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
            </div>
            <p className="text-[11px] text-slate-500 mt-1">
              Expected from contract: <span className="font-semibold">{expectedQuantity} Quintals</span>
            </p>
          </div>

          <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 space-y-2">
            <div className="flex items-center justify-between text-xs text-slate-600">
              <span className="flex items-center space-x-1 font-medium">
                <Camera className="w-3.5 h-3.5 text-emerald-600" />
                <span>Weight Slip & Produce Photos</span>
              </span>
              <span className="text-emerald-700 font-semibold bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                Mock Attached
              </span>
            </div>
            <p className="text-[11px] text-slate-500">
              Electronic weighbridge slip #WB-78923 and lot batch inspection photo attached.
            </p>
          </div>

          <div>
            <label className="flex items-center space-x-2 text-sm font-medium text-slate-800 cursor-pointer">
              <input
                type="checkbox"
                checked={hasQualityIssue}
                onChange={(e) => setHasQualityIssue(e.target.checked)}
                className="rounded text-rose-600 focus:ring-rose-500 w-4 h-4"
              />
              <span className="text-rose-700 font-semibold flex items-center space-x-1">
                <AlertTriangle className="w-4 h-4 text-rose-600 inline" />
                <span>Flag Quality Defect / Moisture Discrepancy</span>
              </span>
            </label>
            <p className="text-[11px] text-slate-500 ml-6 mt-0.5">
              Checking this will immediately place the contract in DISPUTED status and lock escrow release.
            </p>
          </div>

          {hasQualityIssue ? (
            <div>
              <label className="block text-xs font-semibold text-rose-800 uppercase tracking-wider mb-1.5">
                Defect / Discrepancy Details
              </label>
              <textarea
                rows={3}
                value={issueDescription}
                onChange={(e) => setIssueDescription(e.target.value)}
                placeholder="Describe moisture deviation, foreign matter, insect damage, or weight shortfall..."
                required={hasQualityIssue}
                className="w-full p-3 rounded-xl border border-rose-300 focus:ring-2 focus:ring-rose-500 text-sm bg-rose-50/30"
              />
            </div>
          ) : (
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
                Inspection Remarks (Optional)
              </label>
              <textarea
                rows={2}
                value={remarks}
                onChange={(e) => setRemarks(e.target.value)}
                placeholder="e.g. Grain quality meets FAQ standards. Clean and dry."
                className="w-full p-3 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm"
              />
            </div>
          )}

          <div className="flex items-center space-x-3 pt-3">
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
              className={`w-2/3 py-2.5 px-4 rounded-xl text-white font-semibold text-sm shadow-md transition-all flex items-center justify-center space-x-2 disabled:opacity-50 ${
                hasQualityIssue
                  ? 'bg-rose-600 hover:bg-rose-700 shadow-rose-700/20'
                  : 'bg-emerald-600 hover:bg-emerald-700 shadow-emerald-700/20'
              }`}
            >
              {loading ? (
                <span>Recording...</span>
              ) : hasQualityIssue ? (
                <>
                  <AlertTriangle className="w-4 h-4" />
                  <span>Raise Dispute & Lock Escrow</span>
                </>
              ) : (
                <>
                  <CheckCircle className="w-4 h-4" />
                  <span>Approve & Release Escrow</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
