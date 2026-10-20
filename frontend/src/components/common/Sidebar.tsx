import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Sprout,
  PlusCircle,
  FileCheck2,
  Warehouse,
  Gavel,
  ShieldCheck,
  TrendingUp,
  Users,
  AlertTriangle,
  History,
  Scale
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useLanguage } from '../../context/LanguageContext';

export const Sidebar: React.FC = () => {
  const { user } = useAuth();
  const { t } = useLanguage();

  if (!user) return null;

  const displayName = user.name || user.fullName || 'User';

  const getNavLinks = () => {
    switch (user.role) {
      case 'FARMER':
        return [
          { to: '/farmer/dashboard', label: t('dashboard'), icon: LayoutDashboard },
          { to: '/farmer/create-lot', label: t('createLot'), icon: PlusCircle },
          { to: '/farmer/lots', label: t('myLots'), icon: Sprout },
          { to: '/farmer/contracts', label: t('contracts'), icon: FileCheck2 },
          { to: '/farmer/bookings', label: t('warehouseBookings'), icon: Warehouse },
          { to: '/benchmarks', label: t('mandiBenchmarks'), icon: TrendingUp },
        ];
      case 'BUYER':
        return [
          { to: '/buyer/dashboard', label: t('dashboard'), icon: LayoutDashboard },
          { to: '/marketplace', label: t('browseLots'), icon: Sprout },
          { to: '/buyer/bids', label: t('myBids'), icon: Gavel },
          { to: '/buyer/contracts', label: t('contracts'), icon: FileCheck2 },
          { to: '/benchmarks', label: t('mandiBenchmarks'), icon: TrendingUp },
        ];
      case 'WAREHOUSE_OPERATOR':
        return [
          { to: '/operator/dashboard', label: t('dashboard'), icon: LayoutDashboard },
          { to: '/operator/register-warehouse', label: t('manageWarehouses'), icon: PlusCircle },
          { to: '/operator/bookings', label: t('warehouseBookings'), icon: Warehouse },
        ];
      case 'ADMIN':
        return [
          { to: '/admin/dashboard', label: t('dashboard'), icon: LayoutDashboard },
          { to: '/admin/users', label: t('userManagement'), icon: Users },
          { to: '/admin/disputes', label: t('disputes'), icon: AlertTriangle },
          { to: '/admin/benchmarks', label: 'Benchmark Admin', icon: Scale },
          { to: '/admin/audit-logs', label: 'Audit Trail', icon: History },
        ];
      default:
        return [];
    }
  };

  const links = getNavLinks();

  return (
    <aside className="w-64 bg-white border-r border-slate-200 min-h-[calc(100vh-4rem)] p-4 flex flex-col justify-between hidden md:flex">
      <div className="space-y-6">
        <div className="px-3 py-2 bg-slate-50 rounded-xl border border-slate-100 flex items-center space-x-3">
          <div className="w-10 h-10 rounded-full bg-emerald-100 text-emerald-800 font-bold flex items-center justify-center text-sm border border-emerald-200">
            {displayName.charAt(0).toUpperCase()}
          </div>
          <div className="overflow-hidden">
            <h4 className="text-sm font-semibold text-slate-800 truncate">{displayName}</h4>
            <p className="text-xs text-slate-500 capitalize">{user.role.toLowerCase().replace('_', ' ')}</p>
          </div>
        </div>

        <nav className="space-y-1">
          {links.map((link) => {
            const Icon = link.icon;
            return (
              <NavLink
                key={link.to}
                to={link.to}
                className={({ isActive }) =>
                  `flex items-center space-x-3 px-3.5 py-2.5 rounded-lg text-sm font-medium transition-all ${
                    isActive
                      ? 'bg-emerald-50 text-emerald-700 font-semibold shadow-xs border border-emerald-100'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                  }`
                }
              >
                <Icon className="w-4 h-4 flex-shrink-0" />
                <span className="truncate">{link.label}</span>
              </NavLink>
            );
          })}
        </nav>
      </div>

      <div className="p-3 bg-emerald-50/60 rounded-xl border border-emerald-100 text-xs text-emerald-800 space-y-1">
        <div className="flex items-center space-x-1 font-semibold text-emerald-900">
          <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
          <span>Status: {user.accountStatus || user.status}</span>
        </div>
        <p className="text-emerald-700">
          {(user.accountStatus || user.status) === 'ACTIVE'
            ? 'Fully verified account'
            : 'Pending Admin approval'}
        </p>
      </div>
    </aside>
  );
};
