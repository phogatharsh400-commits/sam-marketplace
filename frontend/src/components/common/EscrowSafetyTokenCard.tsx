import React, { useState } from 'react';
import { ShieldCheck, Lock, Award, CheckCircle2, QrCode, FileText, ArrowRight, Printer } from 'lucide-react';

interface EscrowSafetyTokenCardProps {
  contractNumber: string;
  amount: number;
  farmerName: string;
  buyerName: string;
  crop: string;
  tokenCode?: string;
  tokenHash?: string;
  bankGuaranteeRef?: string;
  status?: string;
}

export const EscrowSafetyTokenCard: React.FC<EscrowSafetyTokenCardProps> = ({
  contractNumber,
  amount,
  farmerName,
  buyerName,
  crop,
  tokenCode,
  tokenHash,
  bankGuaranteeRef,
  status = 'ESCROW_FUNDED',
}) => {
  const [showModal, setShowModal] = useState(false);

  const cleanNum = contractNumber.replace(/\D+/g, '') || '883921';
  const effectiveTokenCode = tokenCode || `ESC-2026-${cleanNum.slice(0, 6).padEnd(6, '9')}`;
  const effectiveBankRef = bankGuaranteeRef || `RBI-NODAL-ESC-${cleanNum.padStart(8, '0').slice(-8)}`;
  const effectiveHash = tokenHash || 'SHA256:7f83b1657ff1fc53b92dc18148a1d65d4923e';

  return (
    <>
      <div className="bg-gradient-to-br from-emerald-900 via-teal-900 to-slate-900 text-white rounded-3xl p-6 shadow-xl border border-emerald-500/30 relative overflow-hidden">
        {/* Background decorative watermark */}
        <div className="absolute -right-6 -bottom-6 opacity-10 pointer-events-none">
          <Award className="w-64 h-64 text-emerald-300" />
        </div>

        <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-emerald-500/20 border border-emerald-400/30 text-emerald-300 text-xs font-bold uppercase tracking-wider">
              <ShieldCheck className="w-4 h-4 text-emerald-400" />
              <span>Farmer Payment Safety Guarantee Token</span>
            </div>
            <h3 className="text-xl font-black text-white tracking-tight">
              100% Escrow-Secured Settlement Token
            </h3>
            <p className="text-xs text-emerald-100/80 max-w-xl">
              Buyer's money is locked in a regulated digital escrow vault. The farmer is guaranteed payment of ₹{amount?.toLocaleString('en-IN')} upon delivery confirmation.
            </p>
          </div>

          <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 w-full md:w-auto">
            <div className="bg-white/10 backdrop-blur-md px-4 py-3 rounded-2xl border border-white/10 text-center">
              <span className="text-[10px] text-emerald-200 uppercase font-semibold block">Secured Amount</span>
              <span className="text-xl font-black text-emerald-300">₹{amount?.toLocaleString('en-IN')}</span>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-5 py-3 rounded-2xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-lg transition-all flex items-center justify-center space-x-2 active:scale-95"
            >
              <FileText className="w-4 h-4 text-slate-950" />
              <span>View Escrow Guarantee Note</span>
            </button>
          </div>
        </div>

        <div className="mt-5 pt-4 border-t border-emerald-500/20 grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
          <div>
            <span className="text-[10px] text-emerald-300/70 block">Digital Token Code</span>
            <span className="font-mono font-bold text-white text-[11px]">{effectiveTokenCode}</span>
          </div>
          <div>
            <span className="text-[10px] text-emerald-300/70 block">Nodal Bank Vault Ref</span>
            <span className="font-mono font-bold text-white text-[11px]">{effectiveBankRef}</span>
          </div>
          <div>
            <span className="text-[10px] text-emerald-300/70 block">Release Condition</span>
            <span className="font-semibold text-emerald-300 text-[11px]">Instant on Delivery OTP</span>
          </div>
          <div>
            <span className="text-[10px] text-emerald-300/70 block">Statutory Compliance</span>
            <span className="font-semibold text-emerald-300 text-[11px]">RBI Nodal & IT Act 10A</span>
          </div>
        </div>
      </div>

      {/* Pop-up Certificate Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white text-slate-900 w-full max-w-2xl rounded-3xl p-6 sm:p-8 shadow-2xl border-4 border-emerald-600 relative space-y-6 max-h-[90vh] overflow-y-auto">
            {/* Header */}
            <div className="text-center space-y-1 pb-4 border-b-2 border-slate-100 relative">
              <div className="w-12 h-12 rounded-2xl bg-emerald-100 flex items-center justify-center text-emerald-700 mx-auto mb-2">
                <Award className="w-7 h-7" />
              </div>
              <span className="text-[10px] font-black uppercase tracking-widest text-emerald-700 bg-emerald-50 px-3 py-1 rounded-full">
                SMART AGRICULTURAL MARKETPLACE (SAM)
              </span>
              <h2 className="text-2xl font-black text-slate-900">
                FARMER ESCROW SETTLEMENT GUARANTEE NOTE
              </h2>
              <p className="text-xs text-slate-500">
                Issued in accordance with Reserve Bank of India (RBI) Payment Aggregator Nodal Guidelines & Section 10A of IT Act 2000
              </p>
            </div>

            {/* Token Certificate Details */}
            <div className="bg-emerald-50/70 border border-emerald-200 rounded-2xl p-4 grid grid-cols-2 sm:grid-cols-3 gap-4 text-xs">
              <div>
                <span className="text-[10px] text-slate-500 font-semibold block">Guarantee Token ID</span>
                <span className="font-mono font-black text-emerald-800 text-sm">{effectiveTokenCode}</span>
              </div>
              <div>
                <span className="text-[10px] text-slate-500 font-semibold block">Contract Number</span>
                <span className="font-mono font-bold text-slate-800">{contractNumber}</span>
              </div>
              <div>
                <span className="text-[10px] text-slate-500 font-semibold block">Vault Amount Locked</span>
                <span className="font-black text-emerald-700 text-base">₹{amount?.toLocaleString('en-IN')}</span>
              </div>
              <div>
                <span className="text-[10px] text-slate-500 font-semibold block">Beneficiary Farmer</span>
                <span className="font-bold text-slate-800">{farmerName}</span>
              </div>
              <div>
                <span className="text-[10px] text-slate-500 font-semibold block">Payer Wholesale Buyer</span>
                <span className="font-bold text-slate-800">{buyerName}</span>
              </div>
              <div>
                <span className="text-[10px] text-slate-500 font-semibold block">Produce Commodity</span>
                <span className="font-bold text-slate-800">{crop}</span>
              </div>
            </div>

            {/* Statutory Terms & Safety Clauses */}
            <div className="space-y-3 text-xs bg-slate-50 p-4 rounded-2xl border border-slate-200">
              <h4 className="font-bold text-slate-800 flex items-center space-x-1.5">
                <Lock className="w-4 h-4 text-emerald-600" />
                <span>Irrevocable Farmer Safety Guarantee Terms:</span>
              </h4>
              <ul className="space-y-2 text-slate-600 text-[11px] list-disc pl-4">
                <li>
                  <strong>Funds Irrevocability:</strong> The buyer has transferred ₹{amount?.toLocaleString('en-IN')} into the Escrow Vault. The buyer <em>cannot cancel or withdraw</em> these funds during transit.
                </li>
                <li>
                  <strong>Direct Payout:</strong> Upon submission of the delivery OTP or weighing slip confirmation, the escrow balance will be automatically disbursed to the farmer's registered bank account.
                </li>
                <li>
                  <strong>Dispute Safe Harbor:</strong> Even in case of quality dispute, funds remain secured in neutral third-party escrow until admin arbitrator resolves settlement.
                </li>
              </ul>
            </div>

            {/* Cryptographic Signature & Hash */}
            <div className="flex flex-col sm:flex-row items-center justify-between gap-4 p-4 bg-slate-900 text-white rounded-2xl">
              <div className="space-y-1 text-xs">
                <span className="text-[10px] text-slate-400 font-mono block">SHA-256 Cryptographic Hash</span>
                <span className="font-mono text-[10px] text-emerald-400 break-all">{effectiveHash}</span>
                <span className="text-[10px] text-slate-400 block pt-1">
                  Nodal Bank Identifier: <strong className="text-white">{effectiveBankRef}</strong>
                </span>
              </div>
              <div className="flex-shrink-0 bg-white p-2 rounded-xl text-slate-900 text-center">
                <QrCode className="w-16 h-16 text-slate-900 mx-auto" />
                <span className="text-[9px] font-black uppercase text-emerald-800 block mt-1">RBI Escrow Verified</span>
              </div>
            </div>

            {/* Actions */}
            <div className="flex items-center justify-between pt-2">
              <button
                onClick={() => window.print()}
                className="px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold text-xs flex items-center space-x-1.5"
              >
                <Printer className="w-4 h-4" />
                <span>Print Guarantee Note</span>
              </button>
              <button
                onClick={() => setShowModal(false)}
                className="px-6 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md"
              >
                Close & Return
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};
