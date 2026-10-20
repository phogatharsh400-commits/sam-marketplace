import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useNotifications } from '../../context/NotificationContext';
import { useLanguage } from '../../context/LanguageContext';
import {
  Sprout,
  Bell,
  User,
  LogOut,
  Menu,
  X,
  TrendingUp,
  Warehouse,
  ShoppingBag,
  LayoutDashboard,
  ShieldCheck,
  Check,
  Coins,
  Wallet,
} from 'lucide-react';
import { MeritScoreBadge } from './MeritScoreBadge';
import { WalletModal } from '../modals/WalletModal';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const { unreadCount, notifications, markAsRead, markAllAsRead } = useNotifications();
  const { language, setLanguage, t } = useLanguage();
  const navigate = useNavigate();
  const location = useLocation();

  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isNotifOpen, setIsNotifOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [isWalletOpen, setIsWalletOpen] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const getDashboardLink = () => {
    if (!user) return '/login';
    switch (user.role) {
      case 'FARMER':
        return '/farmer/dashboard';
      case 'BUYER':
        return '/buyer/dashboard';
      case 'WAREHOUSE_OPERATOR':
        return '/operator/dashboard';
      case 'ADMIN':
        return '/admin/dashboard';
      default:
        return '/marketplace';
    }
  };

  return (
    <nav className="bg-white border-b border-slate-200 sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          {/* Logo & Main Nav */}
          <div className="flex items-center gap-8">
            <Link to="/" className="flex items-center gap-2 group">
              <div className="w-10 h-10 rounded-xl bg-brand-600 flex items-center justify-center text-white shadow-sm group-hover:bg-brand-700 transition">
                <Sprout className="w-6 h-6" />
              </div>
              <div>
                <span className="font-bold text-lg text-slate-900 leading-none block">SAM</span>
                <span className="text-xs text-brand-700 font-medium">Smart Agri Market</span>
              </div>
            </Link>

            <div className="hidden md:flex items-center gap-1">
              <Link
                to="/marketplace"
                className={`px-3 py-2 rounded-lg text-sm font-medium transition flex items-center gap-1.5 ${
                  location.pathname.startsWith('/marketplace')
                    ? 'bg-brand-50 text-brand-700 font-semibold'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <ShoppingBag className="w-4 h-4" />
                {t('marketplace')}
              </Link>
              <Link
                to="/benchmarks"
                className={`px-3 py-2 rounded-lg text-sm font-medium transition flex items-center gap-1.5 ${
                  location.pathname.startsWith('/benchmarks')
                    ? 'bg-brand-50 text-brand-700 font-semibold'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <TrendingUp className="w-4 h-4" />
                {t('benchmarks')}
              </Link>
              <Link
                to="/warehouses"
                className={`px-3 py-2 rounded-lg text-sm font-medium transition flex items-center gap-1.5 ${
                  location.pathname.startsWith('/warehouses')
                    ? 'bg-brand-50 text-brand-700 font-semibold'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <Warehouse className="w-4 h-4" />
                {t('warehouses')}
              </Link>
            </div>
          </div>

          {/* Right section: Language, Notifications, User */}
          <div className="flex items-center gap-3">
            {/* Language Switcher */}
            <div className="flex bg-slate-100 p-1 rounded-lg text-xs font-semibold">
              <button
                onClick={() => setLanguage('en')}
                className={`px-2 py-1 rounded transition ${
                  language === 'en' ? 'bg-white text-brand-700 shadow-sm' : 'text-slate-600'
                }`}
              >
                EN
              </button>
              <button
                onClick={() => setLanguage('hi')}
                className={`px-2 py-1 rounded transition ${
                  language === 'hi' ? 'bg-white text-brand-700 shadow-sm' : 'text-slate-600'
                }`}
              >
                हिन्दी
              </button>
            </div>

            {isAuthenticated && user ? (
              <>
                {/* Merit & Trust Score Badge */}
                <div className="hidden sm:block">
                  <MeritScoreBadge
                    score={user.meritPoints ?? 100}
                    tier={user.trustTier ?? 'PLATINUM'}
                    userName={user.fullName}
                    size="sm"
                  />
                </div>

                {/* Security Vault & Token Balance Button */}
                <button
                  type="button"
                  onClick={() => setIsWalletOpen(true)}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 text-emerald-800 text-xs font-bold transition shadow-xs cursor-pointer"
                  title="Open SAM Security Vault & Deposit Tokens"
                >
                  <Coins className="w-4 h-4 text-emerald-600" />
                  <span className="hidden sm:inline">Vault & Tokens</span>
                </button>

                {/* Notifications Dropdown */}
                <div className="relative">
                  <button
                    onClick={() => {
                      setIsNotifOpen(!isNotifOpen);
                      setIsProfileOpen(false);
                    }}
                    className="p-2 text-slate-600 hover:text-slate-900 hover:bg-slate-100 rounded-lg relative transition"
                    aria-label="Notifications"
                  >
                    <Bell className="w-5 h-5" />
                    {unreadCount > 0 && (
                      <span className="absolute top-1 right-1 w-4 h-4 bg-rose-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center animate-pulse">
                        {unreadCount > 9 ? '9+' : unreadCount}
                      </span>
                    )}
                  </button>

                  {isNotifOpen && (
                    <div className="absolute right-0 mt-2 w-80 sm:w-96 bg-white border border-slate-200 rounded-xl shadow-xl z-50 overflow-hidden">
                      <div className="p-3 border-b border-slate-100 flex items-center justify-between bg-slate-50">
                        <div className="font-semibold text-sm text-slate-800 flex items-center gap-1.5">
                          <Bell className="w-4 h-4 text-brand-600" />
                          Notifications ({unreadCount} new)
                        </div>
                        {unreadCount > 0 && (
                          <button
                            onClick={() => markAllAsRead()}
                            className="text-xs text-brand-600 hover:text-brand-800 font-medium flex items-center gap-1"
                          >
                            <Check className="w-3.5 h-3.5" /> Mark all read
                          </button>
                        )}
                      </div>
                      <div className="max-h-80 overflow-y-auto divide-y divide-slate-100">
                        {notifications.length === 0 ? (
                          <div className="p-6 text-center text-slate-400 text-sm">
                            No notifications yet
                          </div>
                        ) : (
                          notifications.map((notif) => (
                            <div
                              key={notif.id}
                              onClick={() => !notif.isRead && markAsRead(notif.id)}
                              className={`p-3 text-xs transition cursor-pointer hover:bg-slate-50 ${
                                !notif.isRead ? 'bg-emerald-50/40 font-medium' : 'text-slate-600'
                              }`}
                            >
                              <div className="flex items-start justify-between gap-2">
                                <span className="font-semibold text-slate-900">{notif.title}</span>
                                {!notif.isRead && (
                                  <span className="w-2 h-2 rounded-full bg-brand-600 flex-shrink-0 mt-1"></span>
                                )}
                              </div>
                              <p className="mt-1 text-slate-600 line-clamp-2">{notif.message}</p>
                              <span className="text-[10px] text-slate-400 mt-1 block">
                                {new Date(notif.createdAt).toLocaleDateString()} at{' '}
                                {new Date(notif.createdAt).toLocaleTimeString([], {
                                  hour: '2-digit',
                                  minute: '2-digit',
                                })}
                              </span>
                            </div>
                          ))
                        )}
                      </div>
                    </div>
                  )}
                </div>

                {/* User Menu Button */}
                <div className="relative">
                  <button
                    onClick={() => {
                      setIsProfileOpen(!isProfileOpen);
                      setIsNotifOpen(false);
                    }}
                    className="flex items-center gap-2 pl-2 pr-3 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 transition"
                  >
                    <div className="w-7 h-7 rounded-full bg-brand-100 text-brand-800 flex items-center justify-center font-bold text-xs">
                      {user.fullName ? user.fullName[0].toUpperCase() : 'U'}
                    </div>
                    <div className="text-left hidden sm:block">
                      <span className="text-xs font-semibold text-slate-900 block leading-tight truncate max-w-[120px]">
                        {user.fullName}
                      </span>
                      <span className="text-[10px] text-brand-700 font-medium block leading-tight">
                        {user.role}
                      </span>
                    </div>
                  </button>

                  {isProfileOpen && (
                    <div className="absolute right-0 mt-2 w-56 bg-white border border-slate-200 rounded-xl shadow-xl z-50 py-1 overflow-hidden">
                      <div className="px-4 py-2 border-b border-slate-100 bg-slate-50">
                        <p className="text-xs font-semibold text-slate-900">{user.fullName}</p>
                        <p className="text-[11px] text-slate-500">{user.mobileNumber}</p>
                        <div className="mt-1 flex items-center justify-between gap-1">
                          <div className="inline-flex items-center gap-1 text-[10px] font-semibold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800">
                            <ShieldCheck className="w-3 h-3" /> {user.status}
                          </div>
                          <MeritScoreBadge
                            score={user.meritPoints ?? 100}
                            tier={user.trustTier ?? 'PLATINUM'}
                            userName={user.fullName}
                            size="sm"
                          />
                        </div>
                      </div>

                      <Link
                        to={getDashboardLink()}
                        onClick={() => setIsProfileOpen(false)}
                        className="flex items-center gap-2 px-4 py-2 text-xs text-slate-700 hover:bg-brand-50 hover:text-brand-700 transition"
                      >
                        <LayoutDashboard className="w-4 h-4" />
                        {t('dashboard')}
                      </Link>

                      <button
                        type="button"
                        onClick={() => {
                          setIsProfileOpen(false);
                          setIsWalletOpen(true);
                        }}
                        className="w-full flex items-center gap-2 px-4 py-2 text-xs text-emerald-700 hover:bg-emerald-50 transition text-left font-semibold"
                      >
                        <Coins className="w-4 h-4 text-emerald-600" />
                        <span>Security Vault & Tokens</span>
                      </button>

                      <button
                        onClick={handleLogout}
                        className="w-full flex items-center gap-2 px-4 py-2 text-xs text-rose-600 hover:bg-rose-50 transition text-left"
                      >
                        <LogOut className="w-4 h-4" />
                        {t('logout')}
                      </button>
                    </div>
                  )}
                </div>
              </>
            ) : (
              <div className="flex items-center gap-2">
                <Link
                  to="/login"
                  className="px-3.5 py-2 text-sm font-medium text-slate-700 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition"
                >
                  {t('login')}
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 text-sm font-semibold text-white bg-brand-600 hover:bg-brand-700 rounded-lg shadow-sm transition"
                >
                  {t('register')}
                </Link>
              </div>
            )}

            {/* Mobile menu button */}
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="p-2 text-slate-600 hover:text-slate-900 hover:bg-slate-100 rounded-lg md:hidden"
            >
              {isMobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile nav dropdown */}
      {isMobileMenuOpen && (
        <div className="md:hidden border-t border-slate-200 bg-white px-4 py-3 space-y-2">
          <Link
            to="/marketplace"
            onClick={() => setIsMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-md text-sm font-medium text-slate-700 hover:bg-slate-100"
          >
            {t('marketplace')}
          </Link>
          <Link
            to="/benchmarks"
            onClick={() => setIsMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-md text-sm font-medium text-slate-700 hover:bg-slate-100"
          >
            {t('benchmarks')}
          </Link>
          <Link
            to="/warehouses"
            onClick={() => setIsMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-md text-sm font-medium text-slate-700 hover:bg-slate-100"
          >
            {t('warehouses')}
          </Link>
          {isAuthenticated && (
            <>
              <button
                onClick={() => {
                  setIsMobileMenuOpen(false);
                  setIsWalletOpen(true);
                }}
                className="w-full text-left px-3 py-2 rounded-md text-sm font-bold text-emerald-700 bg-emerald-50 flex items-center space-x-2"
              >
                <Coins className="w-4 h-4" />
                <span>Security Vault & Tokens</span>
              </button>
              <Link
                to={getDashboardLink()}
                onClick={() => setIsMobileMenuOpen(false)}
                className="block px-3 py-2 rounded-md text-sm font-medium text-brand-700 bg-brand-50"
              >
                {t('dashboard')}
              </Link>
            </>
          )}
        </div>
      )}

      {/* Security Vault & Token Passbook Modal */}
      <WalletModal isOpen={isWalletOpen} onClose={() => setIsWalletOpen(false)} />
    </nav>
  );
};
