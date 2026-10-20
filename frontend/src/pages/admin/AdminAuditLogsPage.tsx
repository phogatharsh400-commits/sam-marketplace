import React, { useState, useEffect } from 'react';
import { History, ShieldCheck, Search, Filter, RefreshCw, AlertCircle } from 'lucide-react';
import { adminApi } from '../../api';

export const AdminAuditLogsPage: React.FC = () => {
  const [logs, setLogs] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterAction, setFilterAction] = useState('ALL');

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await adminApi.getAuditLogs({ size: 50 });
      setLogs(Array.isArray(res.data) ? res.data : []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load audit logs.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, []);

  const getActionBadgeColor = (action: string) => {
    if (!action) return 'bg-slate-100 text-slate-800';
    const a = action.toUpperCase();
    if (a.includes('ESCROW') || a.includes('PAYMENT') || a.includes('RELEASE')) return 'bg-emerald-100 text-emerald-800 border-emerald-300';
    if (a.includes('USER') || a.includes('VERIFY') || a.includes('REGISTER')) return 'bg-blue-100 text-blue-800 border-blue-300';
    if (a.includes('DISPUTE')) return 'bg-rose-100 text-rose-800 border-rose-300';
    if (a.includes('BID') || a.includes('LOT')) return 'bg-amber-100 text-amber-800 border-amber-300';
    return 'bg-purple-100 text-purple-800 border-purple-300';
  };

  const filteredLogs = logs.filter((log) => {
    const action = String(log.action || '');
    const resType = String(log.resourceType || log.entityType || '');
    const resId = String(log.resourceId || log.entityId || '');
    const actor = String(log.userName || log.userMobile || '');
    const details = String(log.details || '');

    if (filterAction !== 'ALL' && !action.toUpperCase().includes(filterAction)) {
      return false;
    }

    if (!searchQuery) return true;
    const q = searchQuery.toLowerCase();
    return (
      action.toLowerCase().includes(q) ||
      resType.toLowerCase().includes(q) ||
      resId.toLowerCase().includes(q) ||
      actor.toLowerCase().includes(q) ||
      details.toLowerCase().includes(q)
    );
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-black text-slate-900">System Security & Audit Trail</h1>
          <p className="text-xs text-slate-500">
            Immutable transaction records, access attempts, and administrative actions
          </p>
        </div>
        <button
          onClick={fetchLogs}
          disabled={loading}
          className="px-4 py-2 rounded-xl bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-bold shadow-xs flex items-center space-x-1.5 self-start sm:self-auto"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          <span>Refresh Logs</span>
        </button>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs flex items-center space-x-2">
          <AlertCircle className="w-4 h-4 text-rose-600 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Filter toolbar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search action, resource, actor mobile/name, or detail description..."
            className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-300 text-xs font-medium focus:ring-2 focus:ring-purple-500"
          />
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
        </div>

        <select
          value={filterAction}
          onChange={(e) => setFilterAction(e.target.value)}
          className="px-3 py-2 rounded-xl border border-slate-300 text-xs font-medium bg-white"
        >
          <option value="ALL">All Categories</option>
          <option value="ESCROW">Escrow & Payments</option>
          <option value="LOT">Produce Lots</option>
          <option value="BID">Bids</option>
          <option value="USER">Users & Auth</option>
          <option value="DISPUTE">Disputes</option>
        </select>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-slate-400 text-xs font-medium">
            Loading cryptographic audit trail records...
          </div>
        ) : filteredLogs.length === 0 ? (
          <div className="p-12 text-center text-slate-400 text-xs">
            No audit log records match your search filter.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 uppercase font-semibold text-[11px]">
                <tr>
                  <th className="py-3 px-4">Timestamp</th>
                  <th className="py-3 px-4">Action</th>
                  <th className="py-3 px-4">Resource Target</th>
                  <th className="py-3 px-4">Actor</th>
                  <th className="py-3 px-4">IP Address</th>
                  <th className="py-3 px-4">Audit Details</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredLogs.map((log) => {
                  const resType = log.resourceType || log.entityType || 'SYSTEM';
                  const resId = log.resourceId || log.entityId || '-';
                  const actor = log.userName || log.userMobile || 'SYSTEM';
                  return (
                    <tr key={log.id} className="hover:bg-slate-50/80 transition-colors">
                      <td className="py-3 px-4 font-mono text-[11px] text-slate-500 whitespace-nowrap">
                        {log.createdAt ? new Date(log.createdAt).toLocaleString('en-IN') : 'Just now'}
                      </td>
                      <td className="py-3 px-4 font-bold">
                        <span className={`px-2.5 py-1 rounded-lg border font-mono text-[10px] font-bold ${getActionBadgeColor(log.action)}`}>
                          {log.action}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-slate-700 font-semibold">
                        {resType} <span className="text-[10px] font-mono text-slate-400">#{String(resId).slice(0, 8)}</span>
                      </td>
                      <td className="py-3 px-4 font-mono text-slate-700">
                        {actor}
                      </td>
                      <td className="py-3 px-4 font-mono text-[11px] text-slate-400">
                        {log.ipAddress || '127.0.0.1'}
                      </td>
                      <td className="py-3 px-4 text-slate-600 max-w-sm">
                        <p className="truncate text-xs font-normal" title={log.details}>
                          {log.details || 'Action completed successfully.'}
                        </p>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

