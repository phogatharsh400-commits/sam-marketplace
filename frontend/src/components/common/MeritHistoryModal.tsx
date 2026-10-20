import React, { useEffect, useState } from 'react';
import { X, Award, ShieldAlert, ShieldCheck, CheckCircle2, TrendingDown, TrendingUp, AlertTriangle, Scale, Lock } from 'lucide-react';
import { meritApi } from '../../api';

interface MeritHistoryModalProps {
  userId?: string;
  userName?: string;
  currentScore?: number;
  currentTier?: string;
  onClose: () => void;
}

export const MeritHistoryModal: React.FC<MeritHistoryModalProps> = ({
  userId,
  userName,
  currentScore = 100,
  currentTier = 'PLATINUM',
  onClose,
}) => {
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState<any>(null);

  useEffect(() => {
    const fetchHistory = async () => {
      setLoading(true);
      try {
        let res;
        if (userId) {
          res = await meritApi.getUserMeritProfile(userId);
        } else {
          res = await meritApi.getMyMeritProfile();
        }
        setData(res?.data);
      } catch (err) {
        console.error('Failed to load merit history', err);
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, [userId]);

  const score = data?.meritPoints ?? currentScore;
  const tier = data?.trustTier ?? currentTier;
  const history = data?.history || [];

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl max-w-2xl w-full shadow-2xl border border-slate-100 overflow-hidden animate-in fade-in zoom-in-95 duration-200">
        
        {/* Header */}
        <div className="bg-gradient-to-r from-slate-900 to-slate-800 p-6 text-white relative">
          <button
            onClick={onClose}
            className="absolute top-5 right-5 text-slate-400 hover:text-white p-1 rounded-full hover:bg-slate-800 transition"
          >
            <X className="w-5 h-5" />
          </button>

          <div className="flex items-center space-x-2 text-emerald-400 text-xs font-bold uppercase tracking-wider mb-1">
            <Award className="w-4 h-4" />
            <span>Reputation & Condition Compliance Ledger</span>
          </div>

          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mt-2">
            <div>
              <h2 className="text-xl font-black">{data?.userName || userName || 'Participant Profile'}</h2>
              <p className="text-slate-400 text-xs mt-0.5">Merit Score & Trust Standing</p>
            </div>

            <div className="flex items-center space-x-3 bg-slate-800/80 px-4 py-2 rounded-2xl border border-slate-700">
              <div className="text-right">
                <span className="text-[10px] text-slate-400 uppercase font-bold block">Merit Score</span>
                <span className={`text-2xl font-black ${score >= 90 ? 'text-emerald-400' : score >= 75 ? 'text-amber-400' : 'text-rose-400'}`}>
                  {score}/100
                </span>
              </div>
              <div className="h-8 w-[1px] bg-slate-700" />
              <div className="text-left">
                <span className="text-[10px] text-slate-400 uppercase font-bold block">Tier</span>
                <span className="text-xs font-bold text-white uppercase">{tier}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Condition & Deduction Rules */}
        <div className="p-6 space-y-6">
          <div className="bg-slate-50 border border-slate-200 p-4 rounded-2xl">
            <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider mb-2 flex items-center space-x-1.5">
              <Scale className="w-4 h-4 text-slate-600" />
              <span>Standard Condition & Penalty Rules</span>
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5 text-xs text-slate-600">
              <div className="flex items-start space-x-2 p-2 rounded-xl bg-white border border-slate-100">
                <TrendingDown className="w-4 h-4 text-rose-500 shrink-0 mt-0.5" />
                <div>
                  <span className="font-bold text-slate-800">-15 Points:</span> Quality Assay failure or produce grade mismatch
                </div>
              </div>
              <div className="flex items-start space-x-2 p-2 rounded-xl bg-white border border-slate-100">
                <TrendingDown className="w-4 h-4 text-rose-500 shrink-0 mt-0.5" />
                <div>
                  <span className="font-bold text-slate-800">-10 Points:</span> Warehouse booking cancellation or dispatch delay
                </div>
              </div>
              <div className="flex items-start space-x-2 p-2 rounded-xl bg-white border border-slate-100">
                <TrendingDown className="w-4 h-4 text-rose-500 shrink-0 mt-0.5" />
                <div>
                  <span className="font-bold text-slate-800">-15 Points:</span> Dispute lost upon administrative resolution
                </div>
              </div>
              <div className="flex items-start space-x-2 p-2 rounded-xl bg-white border border-slate-100">
                <TrendingUp className="w-4 h-4 text-emerald-500 shrink-0 mt-0.5" />
                <div>
                  <span className="font-bold text-slate-800">+5 Points:</span> Verified delivery completion & satisfied assay
                </div>
              </div>
            </div>
          </div>

          {/* Transaction History Log */}
          <div>
            <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider mb-3">
              Merit Activity History ({history.length} events)
            </h3>

            {loading ? (
              <div className="p-8 text-center text-xs text-slate-500">Loading audit history...</div>
            ) : history.length === 0 ? (
              <div className="p-6 text-center bg-slate-50 rounded-2xl border border-dashed border-slate-200">
                <CheckCircle2 className="w-6 h-6 text-emerald-500 mx-auto mb-1.5" />
                <p className="text-xs font-bold text-slate-700">Flawless Compliance Record</p>
                <p className="text-[11px] text-slate-500 mt-0.5">
                  Initial 100/100 points assigned. No condition violations or penalties recorded.
                </p>
              </div>
            ) : (
              <div className="max-h-60 overflow-y-auto space-y-2 pr-1">
                {history.map((tx: any) => (
                  <div
                    key={tx.id}
                    className={`p-3 rounded-2xl border flex items-start justify-between text-xs ${
                      tx.pointsDelta > 0
                        ? 'bg-emerald-50/50 border-emerald-100'
                        : 'bg-rose-50/50 border-rose-100'
                    }`}
                  >
                    <div className="space-y-0.5">
                      <div className="flex items-center space-x-2">
                        <span
                          className={`font-black text-xs ${
                            tx.pointsDelta > 0 ? 'text-emerald-700' : 'text-rose-700'
                          }`}
                        >
                          {tx.pointsDelta > 0 ? `+${tx.pointsDelta}` : tx.pointsDelta} Points
                        </span>
                        <span className="text-slate-400 text-[10px]">•</span>
                        <span className="font-bold text-slate-700">{tx.referenceEntity || 'Marketplace Event'}</span>
                      </div>
                      <p className="text-slate-600 text-[11px]">{tx.reason}</p>
                      <span className="text-[10px] text-slate-400 block">
                        {new Date(tx.createdAt).toLocaleString('en-IN')}
                      </span>
                    </div>

                    <div className="text-right shrink-0">
                      <span className="text-[10px] text-slate-400 uppercase font-semibold block">Balance</span>
                      <span className="font-bold text-slate-800 text-xs">{tx.resultingPoints} pts</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 bg-slate-50 border-t border-slate-100 flex justify-end">
          <button
            type="button"
            onClick={onClose}
            className="px-5 py-2 bg-slate-900 text-white rounded-xl text-xs font-bold hover:bg-slate-800 transition"
          >
            Close Ledger
          </button>
        </div>

      </div>
    </div>
  );
};
