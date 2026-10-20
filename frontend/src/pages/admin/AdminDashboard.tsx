import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  AlertTriangle,
  FileCheck2,
  TrendingUp,
  History,
  ShieldCheck,
  Scale,
  Landmark,
  ArrowRight
} from 'lucide-react';
import { adminApi, disputeApi } from '../../api';
import { DisputeResponse, UserResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';

export const AdminDashboard: React.FC = () => {
  const [stats, setStats] = useState<any>(null);
  const [disputes, setDisputes] = useState<DisputeResponse[]>([]);
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchAdminData = async () => {
    setLoading(true);
    try {
      const [statsRes, dispRes, usersRes] = await Promise.allSettled([
        adminApi.getMarketplaceStats(),
        disputeApi.getAllDisputes(),
        adminApi.getAllUsers(),
      ]);

      if (statsRes.status === 'fulfilled') setStats(statsRes.value.data);
      if (dispRes.status === 'fulfilled') setDisputes(dispRes.value.data);
      if (usersRes.status === 'fulfilled') setUsers(usersRes.value.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAdminData();
  }, []);

  const pendingVerificationCount = users.filter((u) => u.accountStatus === 'PENDING_VERIFICATION').length;
  const openDisputesCount = disputes.filter((d) => d.status === 'OPEN' || d.status === 'UNDER_REVIEW').length;

  return (
    <div className="space-y-8">
      {/* Banner */}
      <div className="bg-gradient-to-r from-purple-950 via-slate-900 to-emerald-950 text-white p-6 sm:p-8 rounded-3xl border border-purple-900 shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="space-y-1.5">
          <div className="inline-flex items-center space-x-1 px-3 py-1 rounded-full bg-purple-500/20 text-purple-300 text-xs font-semibold">
            <ShieldCheck className="w-3.5 h-3.5 mr-1" />
            <span>Platform Governance & Oversight</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black">
            Marketplace Admin Dashboard
          </h1>
          <p className="text-xs sm:text-sm text-slate-300">
            System health, dispute arbitration, user KYC verifications, and audit logging.
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <Link
            to="/admin/users"
            className="px-4 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs shadow-md transition-all flex items-center space-x-1.5"
          >
            <Users className="w-4 h-4" />
            <span>Verify Pending Users ({pendingVerificationCount})</span>
          </Link>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Total Users</span>
            <div className="w-8 h-8 rounded-lg bg-blue-100 text-blue-700 flex items-center justify-center">
              <Users className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-slate-900">{users.length}</p>
          <p className="text-[11px] text-amber-700 font-semibold">{pendingVerificationCount} pending approval</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Active Disputes</span>
            <div className="w-8 h-8 rounded-lg bg-rose-100 text-rose-700 flex items-center justify-center">
              <AlertTriangle className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-rose-700">{openDisputesCount}</p>
          <p className="text-[11px] text-slate-500">Awaiting arbitrator action</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Total Escrow Processed</span>
            <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center">
              <Landmark className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-emerald-700">
            ₹{stats?.totalEscrowAmount ? stats.totalEscrowAmount.toLocaleString('en-IN') : '14,85,000'}
          </p>
          <p className="text-[11px] text-emerald-600">Simulated volume</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase">Total Contracts</span>
            <div className="w-8 h-8 rounded-lg bg-purple-100 text-purple-700 flex items-center justify-center">
              <FileCheck2 className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black text-slate-900">{stats?.totalContracts || 12}</p>
          <p className="text-[11px] text-slate-500">Across Haryana mandis</p>
        </div>
      </div>

      {/* Disputes & User Management shortcuts */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        <div className="lg:col-span-7 space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="font-bold text-slate-900 text-base">Escrow Dispute Queue</h3>
              <Link to="/admin/disputes" className="text-xs font-bold text-purple-700 hover:text-purple-800">
                Adjudicate All ({disputes.length})
              </Link>
            </div>

            {disputes.length === 0 ? (
              <div className="text-center py-8 text-slate-500 text-xs">
                Zero active disputes reported! All trade deliveries proceeding smoothly.
              </div>
            ) : (
              <div className="space-y-3">
                {disputes.slice(0, 4).map((d) => (
                  <div key={d.id} className="p-4 rounded-xl bg-slate-50 border border-slate-200 space-y-2 text-xs">
                    <div className="flex justify-between items-center">
                      <span className="font-bold text-slate-900">Contract #{d.contractNumber}</span>
                      <StatusBadge status={d.status} />
                    </div>
                    <p className="text-slate-700 font-medium">Reason: {d.reason}</p>
                    <p className="text-slate-500 line-clamp-1">{d.description}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="lg:col-span-5 space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="font-bold text-slate-900 text-base">Quick Admin Navigation</h3>
            </div>

            <div className="grid grid-cols-1 gap-3">
              <Link
                to="/admin/users"
                className="p-4 rounded-xl border border-slate-200 hover:border-purple-500 hover:bg-purple-50/30 transition-all flex items-center justify-between"
              >
                <div className="flex items-center space-x-3">
                  <Users className="w-5 h-5 text-purple-600" />
                  <div>
                    <h4 className="font-bold text-slate-900 text-xs">User KYC & Verification</h4>
                    <p className="text-[11px] text-slate-500">Approve farmers, buyers and warehouse operators</p>
                  </div>
                </div>
                <ArrowRight className="w-4 h-4 text-slate-400" />
              </Link>

              <Link
                to="/admin/disputes"
                className="p-4 rounded-xl border border-slate-200 hover:border-purple-500 hover:bg-purple-50/30 transition-all flex items-center justify-between"
              >
                <div className="flex items-center space-x-3">
                  <AlertTriangle className="w-5 h-5 text-rose-600" />
                  <div>
                    <h4 className="font-bold text-slate-900 text-xs">Dispute Resolution</h4>
                    <p className="text-[11px] text-slate-500">Release escrow, refund buyers or split funds</p>
                  </div>
                </div>
                <ArrowRight className="w-4 h-4 text-slate-400" />
              </Link>

              <Link
                to="/admin/benchmarks"
                className="p-4 rounded-xl border border-slate-200 hover:border-purple-500 hover:bg-purple-50/30 transition-all flex items-center justify-between"
              >
                <div className="flex items-center space-x-3">
                  <Scale className="w-5 h-5 text-emerald-600" />
                  <div>
                    <h4 className="font-bold text-slate-900 text-xs">Mandi Benchmarks & MSP</h4>
                    <p className="text-[11px] text-slate-500">Update modal APMC prices and MSP thresholds</p>
                  </div>
                </div>
                <ArrowRight className="w-4 h-4 text-slate-400" />
              </Link>

              <Link
                to="/admin/audit-logs"
                className="p-4 rounded-xl border border-slate-200 hover:border-purple-500 hover:bg-purple-50/30 transition-all flex items-center justify-between"
              >
                <div className="flex items-center space-x-3">
                  <History className="w-5 h-5 text-slate-600" />
                  <div>
                    <h4 className="font-bold text-slate-900 text-xs">Immutable Audit Logs</h4>
                    <p className="text-[11px] text-slate-500">Track all security and transactional events</p>
                  </div>
                </div>
                <ArrowRight className="w-4 h-4 text-slate-400" />
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
