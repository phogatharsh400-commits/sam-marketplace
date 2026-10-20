import React, { useState, useEffect } from 'react';
import { AlertTriangle, ShieldCheck, CheckCircle2, Landmark, RefreshCcw, FileText, Camera } from 'lucide-react';
import { disputeApi } from '../../api';
import { DisputeResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';

export const AdminDisputesPage: React.FC = () => {
  const [disputes, setDisputes] = useState<DisputeResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Adjudication modal
  const [adjudicatingDispute, setAdjudicatingDispute] = useState<DisputeResponse | null>(null);
  const [resolutionStatus, setResolutionStatus] = useState('RESOLVED_FOR_BUYER');
  const [resolutionNotes, setResolutionNotes] = useState('');
  const [refundPercentage, setRefundPercentage] = useState(100);
  const [submitting, setSubmitting] = useState(false);

  const fetchDisputes = async () => {
    setLoading(true);
    try {
      const res = await disputeApi.getAllDisputes();
      setDisputes(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load disputes.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDisputes();
  }, []);

  const handleResolve = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!adjudicatingDispute) return;

    setSubmitting(true);
    try {
      await disputeApi.resolveDispute(adjudicatingDispute.id, {
        status: resolutionStatus,
        resolutionNotes,
        refundPercentage: resolutionStatus === 'PARTIALLY_RESOLVED' ? refundPercentage : undefined,
      });

      setAdjudicatingDispute(null);
      fetchDisputes();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to resolve dispute.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Dispute Resolution Arbitration</h1>
          <p className="text-xs text-slate-500">
            Adjudicate quality discrepancies, inspect weighbridge slips, and manage escrow disbursements
          </p>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs">
          {error}
        </div>
      )}

      {loading ? (
        <div className="py-12 flex justify-center">
          <div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-600 border-t-transparent"></div>
        </div>
      ) : disputes.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center max-w-md mx-auto space-y-3">
          <ShieldCheck className="w-12 h-12 text-emerald-600 mx-auto" />
          <h3 className="text-base font-bold text-slate-800">No Active Disputes</h3>
          <p className="text-xs text-slate-500">
            All trade deliveries and transactions across Haryana are executing smoothly.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {disputes.map((d) => (
            <div
              key={d.id}
              className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4 hover:border-slate-300 transition-all"
            >
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-3 border-b border-slate-100">
                <div className="flex items-center space-x-3">
                  <div className="w-8 h-8 rounded-lg bg-rose-100 text-rose-800 flex items-center justify-center font-bold text-xs">
                    #{d.id}
                  </div>
                  <div>
                    <h3 className="font-bold text-slate-900 text-sm">Contract #{d.contractNumber}</h3>
                    <p className="text-[11px] text-slate-500">
                      Filed by <strong>{d.raisedByName}</strong> on {new Date(d.createdAt).toLocaleDateString('en-IN')}
                    </p>
                  </div>
                </div>
                <StatusBadge status={d.status} />
              </div>

              <div className="space-y-2 text-xs">
                <p><strong>Reason:</strong> <span className="text-slate-800">{d.reason}</span></p>
                <p className="text-slate-600 leading-relaxed bg-slate-50 p-3 rounded-xl border border-slate-100">
                  {d.description}
                </p>
                <p><strong>Requested Resolution:</strong> <span className="font-semibold text-rose-700">{d.requestedResolution}</span></p>

                {d.resolutionNotes && (
                  <div className="bg-purple-50 p-3 rounded-xl border border-purple-100 text-purple-950 mt-2">
                    <p className="font-bold">Arbitrator Ruling & Notes:</p>
                    <p className="mt-0.5">{d.resolutionNotes}</p>
                  </div>
                )}
              </div>

              {/* Action Bar */}
              {(d.status === 'OPEN' || d.status === 'UNDER_REVIEW') && (
                <div className="pt-3 border-t border-slate-100 flex justify-end">
                  <button
                    type="button"
                    onClick={() => {
                      setAdjudicatingDispute(d);
                      setResolutionNotes(`Reviewed quality evidence and weighbridge receipts. Ruling issued.`);
                    }}
                    className="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-700 text-white font-bold text-xs shadow-xs transition-colors flex items-center space-x-1.5"
                  >
                    <ShieldCheck className="w-4 h-4" />
                    <span>Adjudicate & Disburse Escrow</span>
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Adjudication Modal */}
      {adjudicatingDispute && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full shadow-2xl border border-slate-200 overflow-hidden">
            <div className="bg-purple-950 text-white p-5 flex items-center justify-between">
              <div className="flex items-center space-x-2.5">
                <ShieldCheck className="w-5 h-5 text-purple-400" />
                <div>
                  <h3 className="font-bold text-base">Adjudicate Dispute #{adjudicatingDispute.id}</h3>
                  <p className="text-xs text-purple-300">Contract #{adjudicatingDispute.contractNumber}</p>
                </div>
              </div>
            </div>

            <form onSubmit={handleResolve} className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
                  Arbitration Decision
                </label>
                <select
                  value={resolutionStatus}
                  onChange={(e) => setResolutionStatus(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium focus:ring-2 focus:ring-purple-500"
                >
                  <option value="RESOLVED_FOR_BUYER">Refund Escrow to Buyer (Full)</option>
                  <option value="RESOLVED_FOR_FARMER">Release Escrow to Farmer (Full)</option>
                  <option value="PARTIALLY_RESOLVED">Partial Escrow Split (Price Reduction)</option>
                  <option value="CLOSED">Dismiss / Close Case</option>
                </select>
              </div>

              {resolutionStatus === 'PARTIALLY_RESOLVED' && (
                <div>
                  <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
                    Buyer Refund Percentage (%)
                  </label>
                  <input
                    type="number"
                    min="1"
                    max="99"
                    value={refundPercentage}
                    onChange={(e) => setRefundPercentage(parseInt(e.target.value, 10) || 50)}
                    className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-bold"
                  />
                  <p className="text-[11px] text-slate-500 mt-1">
                    {refundPercentage}% refunded to Buyer, {100 - refundPercentage}% released to Farmer.
                  </p>
                </div>
              )}

              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
                  Ruling Justification & Notes
                </label>
                <textarea
                  rows={3}
                  required
                  value={resolutionNotes}
                  onChange={(e) => setResolutionNotes(e.target.value)}
                  placeholder="Record justification and weighbridge moisture findings..."
                  className="w-full p-2.5 rounded-xl border border-slate-300 text-sm"
                />
              </div>

              <div className="flex items-center space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setAdjudicatingDispute(null)}
                  className="w-1/3 py-2.5 px-4 rounded-xl border border-slate-300 text-slate-700 font-medium text-sm hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="w-2/3 py-2.5 px-4 rounded-xl bg-purple-600 hover:bg-purple-700 text-white font-bold text-sm shadow-md transition-all flex items-center justify-center space-x-2 disabled:opacity-50"
                >
                  {submitting ? <span>Executing Ruling...</span> : <span>Confirm Adjudication</span>}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
