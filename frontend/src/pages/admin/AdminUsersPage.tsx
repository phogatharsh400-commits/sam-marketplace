import React, { useState, useEffect } from 'react';
import { Users, CheckCircle2, XCircle, ShieldAlert, UserCheck, Search, Filter } from 'lucide-react';
import { adminApi } from '../../api';
import { UserResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';

export const AdminUsersPage: React.FC = () => {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedRole, setSelectedRole] = useState<string>('');
  const [searchQuery, setSearchQuery] = useState('');
  const [processingId, setProcessingId] = useState<string | number | null>(null);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await adminApi.getAllUsers();
      setUsers(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load users.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleUpdateStatus = async (userId: string | number, status: string) => {
    setProcessingId(userId);
    try {
      await adminApi.updateUserStatus(userId, status);
      fetchUsers();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update user status.');
    } finally {
      setProcessingId(null);
    }
  };

  const filteredUsers = users.filter((u) => {
    const matchesRole = !selectedRole || u.role === selectedRole;
    const name = u.name || u.fullName || '';
    const matchesQuery =
      !searchQuery ||
      name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.mobileNumber.includes(searchQuery) ||
      (u.email && u.email.toLowerCase().includes(searchQuery.toLowerCase()));
    return matchesRole && matchesQuery;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-black text-slate-900">User KYC & Verification</h1>
          <p className="text-xs text-slate-500">
            Verify farmer landholding records, buyer GSTIN profiles, and warehouse licenses
          </p>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs">
          {error}
        </div>
      )}

      {/* Filter toolbar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs grid grid-cols-1 sm:grid-cols-12 gap-3">
        <div className="sm:col-span-8 relative">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by user name, mobile number, or email..."
            className="w-full pl-9 pr-4 py-2 rounded-xl border border-slate-300 text-xs font-medium focus:ring-2 focus:ring-purple-500"
          />
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
        </div>

        <div className="sm:col-span-4">
          <select
            value={selectedRole}
            onChange={(e) => setSelectedRole(e.target.value)}
            className="w-full p-2 rounded-xl border border-slate-300 text-xs font-medium text-slate-700"
          >
            <option value="">All Roles (Farmer, Buyer, Operator, Admin)</option>
            <option value="FARMER">Farmers Only</option>
            <option value="BUYER">Buyers Only</option>
            <option value="WAREHOUSE_OPERATOR">Warehouse Operators</option>
            <option value="ADMIN">Admins</option>
          </select>
        </div>
      </div>

      {/* Users table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 uppercase font-semibold">
              <tr>
                <th className="py-3 px-4">User</th>
                <th className="py-3 px-4">Mobile & Email</th>
                <th className="py-3 px-4">Role</th>
                <th className="py-3 px-4">KYC Status</th>
                <th className="py-3 px-4">Joined Date</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredUsers.map((u) => {
                const status = u.accountStatus || u.status;
                const displayName = u.name || u.fullName;
                return (
                  <tr key={u.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-3.5 px-4 font-bold text-slate-900">
                      {displayName}
                    </td>
                    <td className="py-3.5 px-4 text-slate-600">
                      <p className="font-mono">{u.mobileNumber}</p>
                      {u.email && <p className="text-[11px] text-slate-400">{u.email}</p>}
                    </td>
                    <td className="py-3.5 px-4 font-medium text-slate-700 capitalize">
                      {u.role.toLowerCase().replace('_', ' ')}
                    </td>
                    <td className="py-3.5 px-4">
                      <StatusBadge status={status} />
                    </td>
                    <td className="py-3.5 px-4 text-slate-500">
                      {new Date(u.createdAt).toLocaleDateString('en-IN')}
                    </td>
                    <td className="py-3.5 px-4 text-right space-x-1.5">
                      {status === 'PENDING_VERIFICATION' && (
                        <button
                          type="button"
                          disabled={processingId === u.id}
                          onClick={() => handleUpdateStatus(u.id, 'ACTIVE')}
                          className="px-2.5 py-1 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-[11px] transition-colors inline-flex items-center space-x-1"
                        >
                          <UserCheck className="w-3 h-3" />
                          <span>Verify</span>
                        </button>
                      )}

                      {status === 'ACTIVE' && (
                        <button
                          type="button"
                          disabled={processingId === u.id}
                          onClick={() => handleUpdateStatus(u.id, 'SUSPENDED')}
                          className="px-2.5 py-1 rounded-lg bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 font-bold text-[11px] transition-colors"
                        >
                          Suspend
                        </button>
                      )}

                      {status === 'SUSPENDED' && (
                        <button
                          type="button"
                          disabled={processingId === u.id}
                          onClick={() => handleUpdateStatus(u.id, 'ACTIVE')}
                          className="px-2.5 py-1 rounded-lg bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 font-bold text-[11px] transition-colors"
                        >
                          Reactivate
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
