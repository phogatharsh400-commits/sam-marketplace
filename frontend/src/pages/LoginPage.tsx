import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Phone, Lock, LogIn, Sprout, AlertCircle, ShieldCheck, KeyRound } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { t } = useLanguage();

  const [mobileNumber, setMobileNumber] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await login(mobileNumber, password);
      // AuthContext will update user state; redirect based on credentials/role
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid mobile number or password.');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickFill = (mobile: string, pass: string) => {
    setMobileNumber(mobile);
    setPassword(pass);
    setError(null);
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4 sm:px-6 lg:px-8 py-12">
      <div className="max-w-md w-full space-y-8 bg-white p-8 sm:p-10 rounded-3xl border border-slate-200 shadow-xl">
        <div className="text-center space-y-2">
          <div className="w-12 h-12 rounded-2xl bg-emerald-600 text-white flex items-center justify-center mx-auto shadow-lg shadow-emerald-600/30">
            <Sprout className="w-7 h-7" />
          </div>
          <h2 className="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight">
            {t('login')} to SAM
          </h2>
          <p className="text-xs sm:text-sm text-slate-500">
            Smart Agricultural Marketplace • Haryana
          </p>
        </div>

        {error && (
          <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 rounded-xl text-xs flex items-center space-x-2">
            <AlertCircle className="w-4 h-4 flex-shrink-0 text-rose-600" />
            <span>{error}</span>
          </div>
        )}

        {/* Quick Fill Test Accounts */}
        <div className="bg-slate-50 p-3.5 rounded-2xl border border-slate-200 space-y-2">
          <div className="flex items-center space-x-1 text-xs font-semibold text-slate-700">
            <KeyRound className="w-3.5 h-3.5 text-emerald-600" />
            <span>Quick Login (Demo Accounts):</span>
          </div>
          <div className="grid grid-cols-2 gap-1.5 text-xs">
            <button
              type="button"
              onClick={() => handleQuickFill('9000000002', 'Farmer@123')}
              className="py-1 px-2 text-left bg-white hover:bg-emerald-50 hover:text-emerald-800 rounded-lg border border-slate-200 font-medium transition-colors"
            >
              🌾 Farmer: <span className="font-mono text-[11px]">9000000002</span>
            </button>
            <button
              type="button"
              onClick={() => handleQuickFill('9000000003', 'Buyer@123')}
              className="py-1 px-2 text-left bg-white hover:bg-emerald-50 hover:text-emerald-800 rounded-lg border border-slate-200 font-medium transition-colors"
            >
              🏢 Buyer: <span className="font-mono text-[11px]">9000000003</span>
            </button>
            <button
              type="button"
              onClick={() => handleQuickFill('9000000004', 'Warehouse@123')}
              className="py-1 px-2 text-left bg-white hover:bg-emerald-50 hover:text-emerald-800 rounded-lg border border-slate-200 font-medium transition-colors"
            >
              🏬 Operator: <span className="font-mono text-[11px]">9000000004</span>
            </button>
            <button
              type="button"
              onClick={() => handleQuickFill('9000000001', 'Admin@123')}
              className="py-1 px-2 text-left bg-white hover:bg-emerald-50 hover:text-emerald-800 rounded-lg border border-slate-200 font-medium transition-colors"
            >
              🛡️ Admin: <span className="font-mono text-[11px]">9000000001</span>
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
              {t('mobileNumber')}
            </label>
            <div className="relative">
              <input
                type="tel"
                maxLength={10}
                required
                value={mobileNumber}
                onChange={(e) => setMobileNumber(e.target.value)}
                placeholder="10-digit mobile number"
                className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm font-medium"
              />
              <Phone className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
            </div>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider">
                {t('password')}
              </label>
              <Link to="/forgot-password" className="text-xs font-semibold text-emerald-600 hover:text-emerald-700">
                {t('forgotPassword')}
              </Link>
            </div>
            <div className="relative">
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter account password"
                className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm font-medium"
              />
              <Lock className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 px-4 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm shadow-md shadow-emerald-700/20 transition-all flex items-center justify-center space-x-2 disabled:opacity-50 mt-2"
          >
            {loading ? (
              <span>Authenticating...</span>
            ) : (
              <>
                <LogIn className="w-4 h-4" />
                <span>{t('login')}</span>
              </>
            )}
          </button>
        </form>

        <div className="text-center pt-2 border-t border-slate-100 text-xs text-slate-600">
          <span>Don't have an account? </span>
          <Link to="/register" className="font-bold text-emerald-700 hover:text-emerald-800">
            {t('register')} Now
          </Link>
        </div>
      </div>
    </div>
  );
};
