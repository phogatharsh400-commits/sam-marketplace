import React, { useState, useEffect } from 'react';
import { FileCheck2, Truck, ShieldCheck, AlertCircle, CheckCircle2, Landmark, AlertTriangle, FileCheck } from 'lucide-react';
import { contractApi } from '../../api';
import { SaleContractResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';
import { DisputeModal } from '../../components/modals/DisputeModal';
import { EscrowSafetyTokenCard } from '../../components/common/EscrowSafetyTokenCard';
import { QualityAssayCertificateModal } from '../../components/common/QualityAssayCertificateModal';

export const FarmerContractsPage: React.FC = () => {
  const [contracts, setContracts] = useState<SaleContractResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Dispatch confirmation
  const [dispatchingId, setDispatchingId] = useState<string | number | null>(null);
  const [dispatchSuccess, setDispatchSuccess] = useState<string | null>(null);

  // Dispute modal
  const [disputeContract, setDisputeContract] = useState<SaleContractResponse | null>(null);

  // Quality modal
  const [qualityContract, setQualityContract] = useState<SaleContractResponse | null>(null);

  const fetchContracts = async () => {
    setLoading(true);
    try {
      const res = await contractApi.getMyContracts();
      setContracts(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load sale contracts.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchContracts();
  }, []);

  const handleConfirmDispatch = async (contractId: string | number) => {
    setDispatchingId(contractId);
    setError(null);
    try {
      await contractApi.confirmDispatch(contractId);
      setDispatchSuccess(`Dispatch confirmed for contract! Buyer has been notified.`);
      setTimeout(() => setDispatchSuccess(null), 5000);
      fetchContracts();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to confirm dispatch.');
    } finally {
      setDispatchingId(null);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Farmer Sale Contracts & Safety Vault</h1>
          <p className="text-xs text-slate-500">
            Track escrow funding status, verify bank guarantee tokens, view quality assaying, and execute dispatches
          </p>
        </div>
      </div>

      {dispatchSuccess && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-2xl text-xs flex items-center space-x-2">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0" />
          <span>{dispatchSuccess}</span>
        </div>
      )}

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs flex items-center space-x-2">
          <AlertCircle className="w-4 h-4 text-rose-600 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {loading ? (
        <div className="py-12 flex justify-center">
          <div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-600 border-t-transparent"></div>
        </div>
      ) : contracts.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center max-w-md mx-auto space-y-3">
          <FileCheck2 className="w-12 h-12 text-slate-400 mx-auto" />
          <h3 className="text-base font-bold text-slate-800">No Sale Contracts</h3>
          <p className="text-xs text-slate-500">
            Accept an active buyer bid on your produce lots to generate a sale contract.
          </p>
        </div>
      ) : (
        <div className="space-y-6">
          {contracts.map((c) => {
            const hasEscrow =
              c.status === 'ESCROW_FUNDED' ||
              c.status === 'READY_FOR_DISPATCH' ||
              c.status === 'DISPATCHED' ||
              c.status === 'DELIVERED' ||
              c.status === 'PAYMENT_RELEASED';

            return (
              <div
                key={c.id}
                className="bg-white p-6 rounded-3xl border border-slate-200 shadow-xs space-y-5 hover:border-slate-300 transition-all"
              >
                {/* Farmer Payment Safety Guarantee Token Banner */}
                {hasEscrow && (
                  <EscrowSafetyTokenCard
                    contractNumber={c.contractNumber}
                    amount={c.totalAmount}
                    farmerName={c.farmerName}
                    buyerName={c.buyerName}
                    crop={c.crop}
                    tokenCode={c.escrowTokenCode}
                    tokenHash={c.escrowTokenHash}
                    bankGuaranteeRef={c.bankGuaranteeRef}
                    status={c.status}
                  />
                )}

                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-3 border-b border-slate-100">
                  <div className="flex items-center space-x-3">
                    <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-800 flex items-center justify-center font-mono font-bold text-xs">
                      #{c.id}
                    </div>
                    <div>
                      <h3 className="font-mono font-bold text-slate-900 text-sm">{c.contractNumber}</h3>
                      <p className="text-[11px] text-slate-500">
                        Contract generated on {new Date(c.createdAt).toLocaleDateString('en-IN')}
                      </p>
                    </div>
                  </div>
                  <StatusBadge status={c.status} />
                </div>

                {/* Grid details */}
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 bg-slate-50 p-3 rounded-2xl border border-slate-100 text-xs">
                  <div>
                    <span className="text-[10px] text-slate-400 block uppercase font-semibold">Buyer</span>
                    <span className="font-bold text-slate-800">{c.buyerName}</span>
                  </div>
                  <div>
                    <span className="text-[10px] text-slate-400 block uppercase font-semibold">Agreed Quantity</span>
                    <span className="font-bold text-slate-800">{c.quantity || c.quantityQuintals} Quintals</span>
                  </div>
                  <div>
                    <span className="text-[10px] text-slate-400 block uppercase font-semibold">Unit Price</span>
                    <span className="font-extrabold text-emerald-700">₹{c.pricePerUnit || c.pricePerQuintal}/qtl</span>
                  </div>
                  <div>
                    <span className="text-[10px] text-slate-400 block uppercase font-semibold">Total Escrow Value</span>
                    <span className="font-black text-slate-900 text-sm">₹{c.totalAmount.toLocaleString('en-IN')}</span>
                  </div>
                </div>

                <div className="text-xs text-slate-600 space-y-1">
                  <p><strong>Delivery Terms:</strong> {c.deliveryTerms}</p>
                  <div className="flex flex-wrap gap-4 text-[11px] text-slate-500 pt-1">
                    <span>Dispatch Deadline: <strong>{c.dispatchDeadline ? new Date(c.dispatchDeadline).toLocaleDateString('en-IN') : 'N/A'}</strong></span>
                    <span>Delivery Deadline: <strong>{c.deliveryDeadline ? new Date(c.deliveryDeadline).toLocaleDateString('en-IN') : 'N/A'}</strong></span>
                  </div>
                </div>

                {/* Action Bar */}
                <div className="pt-3 border-t border-slate-100 flex flex-wrap items-center justify-between gap-3">
                  <div className="text-xs">
                    {c.status === 'AWAITING_ESCROW' && (
                      <span className="text-amber-700 font-semibold flex items-center">
                        <ShieldCheck className="w-4 h-4 mr-1 inline text-amber-600" />
                        Waiting for buyer to fund simulated escrow vault...
                      </span>
                    )}
                    {(c.status === 'ESCROW_FUNDED' || c.status === 'READY_FOR_DISPATCH') && (
                      <span className="text-emerald-700 font-semibold flex items-center">
                        <ShieldCheck className="w-4 h-4 mr-1 inline text-emerald-600" />
                        Escrow 100% Locked! Farmer is guaranteed payment upon delivery.
                      </span>
                    )}
                    {c.status === 'DISPATCHED' && (
                      <span className="text-blue-700 font-semibold flex items-center">
                        <Truck className="w-4 h-4 mr-1 inline text-blue-600" />
                        In transit. Waiting for buyer weighbridge & quality verification.
                      </span>
                    )}
                    {c.status === 'PAYMENT_RELEASED' && (
                      <span className="text-emerald-700 font-bold flex items-center">
                        <Landmark className="w-4 h-4 mr-1 inline text-emerald-600" />
                        Payment Released! ₹{c.totalAmount.toLocaleString('en-IN')} settled to your bank account.
                      </span>
                    )}
                    {c.status === 'DISPUTED' && (
                      <span className="text-rose-700 font-bold flex items-center">
                        <AlertTriangle className="w-4 h-4 mr-1 inline text-rose-600" />
                        Contract in dispute. Under administrative audit.
                      </span>
                    )}
                  </div>

                  <div className="flex items-center space-x-2">
                    {/* Quality Assay Certificate Button */}
                    <button
                      type="button"
                      onClick={() => setQualityContract(c)}
                      className="px-3 py-2 rounded-xl border border-emerald-300 bg-emerald-50 text-emerald-800 hover:bg-emerald-100 text-xs font-semibold flex items-center space-x-1"
                    >
                      <FileCheck className="w-3.5 h-3.5 text-emerald-600" />
                      <span>Quality Assaying</span>
                    </button>

                    {(c.status === 'ESCROW_FUNDED' || c.status === 'READY_FOR_DISPATCH') && (
                      <button
                        type="button"
                        onClick={() => handleConfirmDispatch(c.id)}
                        disabled={dispatchingId === c.id}
                        className="px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-xs transition-colors flex items-center space-x-1.5"
                      >
                        <Truck className="w-4 h-4" />
                        <span>{dispatchingId === c.id ? 'Confirming...' : 'Confirm Produce Dispatch'}</span>
                      </button>
                    )}

                    {c.status !== 'CREATED' && c.status !== 'CANCELLED' && c.status !== 'REFUNDED' && (
                      <button
                        type="button"
                        onClick={() => setDisputeContract(c)}
                        className="px-3 py-2 rounded-xl border border-slate-300 text-slate-600 hover:text-rose-700 hover:bg-rose-50 text-xs font-semibold transition-colors flex items-center space-x-1"
                      >
                        <AlertTriangle className="w-3.5 h-3.5" />
                        <span>Raise Dispute</span>
                      </button>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Quality Assay Modal */}
      {qualityContract && (
        <QualityAssayCertificateModal
          lotNumber={qualityContract.lotNumber}
          crop={qualityContract.crop}
          variety={qualityContract.variety}
          grade="GRADE_A"
          moisturePercentage={12.0}
          qualityCertificateNumber={qualityContract.qualityCertificateNumber}
          sampleStatus={qualityContract.sampleStatus}
          quantityQuintals={qualityContract.quantityQuintals}
          isOpen={!!qualityContract}
          onClose={() => setQualityContract(null)}
        />
      )}

      {/* Dispute Modal */}
      {disputeContract && (
        <DisputeModal
          contractId={disputeContract.id}
          contractNumber={disputeContract.contractNumber}
          isOpen={!!disputeContract}
          onClose={() => setDisputeContract(null)}
          onSuccess={() => {
            fetchContracts();
          }}
        />
      )}
    </div>
  );
};

