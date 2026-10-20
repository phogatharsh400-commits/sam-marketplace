import React, { useState } from 'react';
import { X, ShieldCheck, CreditCard, Landmark, Smartphone, AlertCircle, CheckCircle2 } from 'lucide-react';
import { paymentApi } from '../../api';

interface SimulatedPaymentModalProps {
  contractId: string | number;
  contractNumber: string;
  amount: number;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const SimulatedPaymentModal: React.FC<SimulatedPaymentModalProps> = ({
  contractId,
  contractNumber,
  amount,
  isOpen,
  onClose,
  onSuccess,
}) => {
  const [paymentMethod, setPaymentMethod] = useState<'UPI' | 'NET_BANKING' | 'BANK_TRANSFER'>('UPI');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [simulatedResult, setSimulatedResult] = useState<'SUCCESS' | 'FAILED'>('SUCCESS');

  if (!isOpen) return null;

  const handleFundEscrow = async () => {
    setLoading(true);
    setError(null);

    try {
      if (simulatedResult === 'FAILED') {
        throw new Error('Simulated Bank Gateway Declined: Insufficient mock balance or network timeout.');
      }

      await paymentApi.fundEscrow(contractId, {
        paymentMethod,
        referenceNumber: `SIM-${paymentMethod}-${Date.now()}`,
      });

      onSuccess();
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Payment simulation failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl max-w-lg w-full shadow-2xl border border-slate-200 overflow-hidden animate-in fade-in zoom-in duration-200">
        {/* Header */}
        <div className="bg-slate-900 text-white p-5 flex items-center justify-between">
          <div className="flex items-center space-x-2.5">
            <div className="w-8 h-8 rounded-lg bg-emerald-500/20 text-emerald-400 flex items-center justify-center border border-emerald-500/30">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-base">Escrow Payment Gateway</h3>
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

        {/* Prototype simulation alert */}
        <div className="bg-amber-50 border-b border-amber-100 px-5 py-2.5 flex items-center justify-between text-xs text-amber-800">
          <div className="flex items-center space-x-1.5 font-medium">
            <AlertCircle className="w-4 h-4 text-amber-600 flex-shrink-0" />
            <span>Academic Sandbox Mode (Zero Real Money Debited)</span>
          </div>
        </div>

        <div className="p-6 space-y-6">
          {/* Amount Display */}
          <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 text-center">
            <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold">Total Escrow Amount</p>
            <p className="text-3xl font-extrabold text-slate-900 mt-1">₹{amount.toLocaleString('en-IN')}</p>
            <p className="text-[11px] text-emerald-700 mt-1 flex items-center justify-center space-x-1">
              <ShieldCheck className="w-3.5 h-3.5 inline text-emerald-600" />
              <span>Funds will be held in simulated escrow until delivery verification</span>
            </p>
          </div>

          {/* Payment Method Selector */}
          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-2">
              Select Payment Method
            </label>
            <div className="grid grid-cols-3 gap-2.5">
              <button
                type="button"
                onClick={() => setPaymentMethod('UPI')}
                className={`flex flex-col items-center justify-center p-3 rounded-xl border text-xs font-semibold transition-all ${
                  paymentMethod === 'UPI'
                    ? 'border-emerald-600 bg-emerald-50/70 text-emerald-900 ring-2 ring-emerald-500/20'
                    : 'border-slate-200 text-slate-600 hover:bg-slate-50'
                }`}
              >
                <Smartphone className="w-5 h-5 mb-1.5 text-emerald-600" />
                <span>UPI / QR</span>
              </button>

              <button
                type="button"
                onClick={() => setPaymentMethod('NET_BANKING')}
                className={`flex flex-col items-center justify-center p-3 rounded-xl border text-xs font-semibold transition-all ${
                  paymentMethod === 'NET_BANKING'
                    ? 'border-emerald-600 bg-emerald-50/70 text-emerald-900 ring-2 ring-emerald-500/20'
                    : 'border-slate-200 text-slate-600 hover:bg-slate-50'
                }`}
              >
                <Landmark className="w-5 h-5 mb-1.5 text-emerald-600" />
                <span>Net Banking</span>
              </button>

              <button
                type="button"
                onClick={() => setPaymentMethod('BANK_TRANSFER')}
                className={`flex flex-col items-center justify-center p-3 rounded-xl border text-xs font-semibold transition-all ${
                  paymentMethod === 'BANK_TRANSFER'
                    ? 'border-emerald-600 bg-emerald-50/70 text-emerald-900 ring-2 ring-emerald-500/20'
                    : 'border-slate-200 text-slate-600 hover:bg-slate-50'
                }`}
              >
                <CreditCard className="w-5 h-5 mb-1.5 text-emerald-600" />
                <span>NEFT / RTGS</span>
              </button>
            </div>
          </div>

          {/* Simulation Outcome Toggle (for Academic Testing) */}
          <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
            <div className="flex items-center justify-between text-xs">
              <span className="font-semibold text-slate-700">Simulate Response:</span>
              <div className="flex space-x-2">
                <button
                  type="button"
                  onClick={() => setSimulatedResult('SUCCESS')}
                  className={`px-2.5 py-1 rounded-md text-xs font-medium ${
                    simulatedResult === 'SUCCESS'
                      ? 'bg-emerald-600 text-white shadow-xs'
                      : 'bg-white text-slate-600 border border-slate-200'
                  }`}
                >
                  Success
                </button>
                <button
                  type="button"
                  onClick={() => setSimulatedResult('FAILED')}
                  className={`px-2.5 py-1 rounded-md text-xs font-medium ${
                    simulatedResult === 'FAILED'
                      ? 'bg-rose-600 text-white shadow-xs'
                      : 'bg-white text-slate-600 border border-slate-200'
                  }`}
                >
                  Fail Scenario
                </button>
              </div>
            </div>
          </div>

          {error && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 rounded-lg text-xs flex items-center space-x-2">
              <AlertCircle className="w-4 h-4 flex-shrink-0 text-rose-600" />
              <span>{error}</span>
            </div>
          )}

          {/* Action Buttons */}
          <div className="flex items-center space-x-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="w-1/3 py-2.5 px-4 rounded-xl border border-slate-300 text-slate-700 font-medium text-sm hover:bg-slate-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="button"
              onClick={handleFundEscrow}
              disabled={loading}
              className="w-2/3 py-2.5 px-4 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm shadow-md shadow-emerald-700/20 transition-all flex items-center justify-center space-x-2 disabled:opacity-50"
            >
              {loading ? (
                <span>Simulating Transaction...</span>
              ) : (
                <>
                  <CheckCircle2 className="w-4 h-4" />
                  <span>Authorize ₹{amount.toLocaleString('en-IN')}</span>
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
