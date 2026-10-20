import React from 'react';
import { Award, CheckCircle2, AlertCircle, FileCheck, Droplets, Sparkles, Scale, Layers } from 'lucide-react';

interface QualityAssayCertificateModalProps {
  lotNumber: string;
  crop: string;
  variety: string;
  grade: string;
  moisturePercentage: number;
  foreignMatterPercent?: number;
  brokenGrainsPercent?: number;
  qualityCertificateNumber?: string;
  sampleStatus?: string;
  poolingEnabled?: boolean;
  quantityQuintals: number;
  isOpen: boolean;
  onClose: () => void;
}

export const QualityAssayCertificateModal: React.FC<QualityAssayCertificateModalProps> = ({
  lotNumber,
  crop,
  variety,
  grade,
  moisturePercentage,
  foreignMatterPercent = 0.8,
  brokenGrainsPercent = 1.2,
  qualityCertificateNumber,
  sampleStatus = 'QUALITY_VERIFIED_AND_APPROVED',
  poolingEnabled = false,
  quantityQuintals,
  isOpen,
  onClose,
}) => {
  if (!isOpen) return null;

  const certNumber = qualityCertificateNumber || `QC-HAR-2026-${lotNumber.replace(/\D+/g, '') || '1088'}`;

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white text-slate-900 w-full max-w-xl rounded-3xl p-6 sm:p-8 shadow-2xl border-2 border-emerald-500 relative space-y-6 max-h-[90vh] overflow-y-auto">
        {/* Certificate Header */}
        <div className="text-center space-y-1 pb-4 border-b-2 border-slate-100">
          <div className="w-12 h-12 rounded-2xl bg-emerald-100 flex items-center justify-center text-emerald-700 mx-auto mb-2">
            <FileCheck className="w-7 h-7" />
          </div>
          <span className="text-[10px] font-black uppercase tracking-widest text-emerald-700 bg-emerald-50 px-3 py-1 rounded-full">
            AGMARK & HAR-MANDI COMPLIANT QUALITY ASSAY
          </span>
          <h2 className="text-xl font-black text-slate-900">
            Official Pre-Dispatch Produce Quality Certificate
          </h2>
          <p className="text-xs text-slate-500">
            Certificate Ref: <strong className="font-mono text-emerald-800">{certNumber}</strong>
          </p>
        </div>

        {/* Assaying Status Banner */}
        <div className="p-3.5 bg-emerald-50 border border-emerald-200 rounded-2xl flex items-center space-x-3 text-xs text-emerald-900">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0" />
          <div>
            <span className="font-bold block">Physical Sample Assayed & Approved</span>
            <span className="text-[11px] text-emerald-700">
              Produce lot meets fair average quality (FAQ) standards for wholesale delivery and escrow settlement.
            </span>
          </div>
        </div>

        {/* Quality Parameters Table */}
        <div className="space-y-3">
          <h4 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
            Standard Quality Assaying Parameters
          </h4>
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 text-xs">
            <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
              <div className="flex items-center space-x-1.5 text-slate-500 mb-1">
                <Droplets className="w-3.5 h-3.5 text-blue-500" />
                <span className="text-[10px] font-semibold">Moisture Level</span>
              </div>
              <span className="text-base font-black text-slate-900">{moisturePercentage}%</span>
              <p className="text-[9px] text-emerald-700 font-semibold mt-0.5">✓ Below max 14% limit</p>
            </div>

            <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
              <div className="flex items-center space-x-1.5 text-slate-500 mb-1">
                <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                <span className="text-[10px] font-semibold">Foreign Matter</span>
              </div>
              <span className="text-base font-black text-slate-900">{foreignMatterPercent}%</span>
              <p className="text-[9px] text-emerald-700 font-semibold mt-0.5">✓ Standard grade (max 2%)</p>
            </div>

            <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
              <div className="flex items-center space-x-1.5 text-slate-500 mb-1">
                <Scale className="w-3.5 h-3.5 text-purple-500" />
                <span className="text-[10px] font-semibold">Broken / Shriveled</span>
              </div>
              <span className="text-base font-black text-slate-900">{brokenGrainsPercent}%</span>
              <p className="text-[9px] text-emerald-700 font-semibold mt-0.5">✓ Standard (max 3%)</p>
            </div>
          </div>
        </div>

        {/* Smallholder Pooling Feasibility Info */}
        {poolingEnabled && (
          <div className="p-3.5 bg-blue-50 border border-blue-200 rounded-2xl flex items-start space-x-3 text-xs text-blue-900">
            <Layers className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
            <div>
              <span className="font-bold block">Smallholder FPO Aggregation Feasibility Active</span>
              <span className="text-[11px] text-blue-700">
                This lot is part of a Farmer Producer Organization (FPO) batch pool ({quantityQuintals} Quintals). Multiple smallholder farmers from the same village share logistics freight and attain wholesale buyer pricing.
              </span>
            </div>
          </div>
        )}

        {/* Lot Spec Details */}
        <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 grid grid-cols-2 gap-3 text-xs">
          <div>
            <span className="text-[10px] text-slate-400 font-semibold block">Lot Number</span>
            <span className="font-mono font-bold text-slate-800">{lotNumber}</span>
          </div>
          <div>
            <span className="text-[10px] text-slate-400 font-semibold block">Crop & Variety</span>
            <span className="font-bold text-slate-800">{crop} ({variety})</span>
          </div>
          <div>
            <span className="text-[10px] text-slate-400 font-semibold block">Declared Grade</span>
            <span className="font-bold text-emerald-700">{grade}</span>
          </div>
          <div>
            <span className="text-[10px] text-slate-400 font-semibold block">Assayer Lab Inspector</span>
            <span className="font-bold text-slate-800">Haryana State Quality Assaying Cell</span>
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center justify-end space-x-3 pt-2">
          <button
            onClick={onClose}
            className="px-6 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 text-white font-bold text-xs shadow-md"
          >
            Close Certificate
          </button>
        </div>
      </div>
    </div>
  );
};
