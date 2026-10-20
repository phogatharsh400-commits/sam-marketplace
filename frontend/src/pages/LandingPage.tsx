import React from 'react';
import { Link } from 'react-router-dom';
import {
  Sprout,
  ShieldCheck,
  TrendingUp,
  Scale,
  Truck,
  Warehouse,
  CheckCircle2,
  ArrowRight,
  Landmark,
  Layers,
  Users,
  Award
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';

export const LandingPage: React.FC = () => {
  const { isAuthenticated, user } = useAuth();
  const { t } = useLanguage();

  const getDashboardPath = () => {
    if (!user) return '/marketplace';
    switch (user.role) {
      case 'FARMER': return '/farmer/dashboard';
      case 'BUYER': return '/buyer/dashboard';
      case 'WAREHOUSE_OPERATOR': return '/operator/dashboard';
      case 'ADMIN': return '/admin/dashboard';
      default: return '/marketplace';
    }
  };

  return (
    <div className="space-y-16 pb-16">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-b from-emerald-950 via-emerald-900 to-slate-900 text-white pt-16 pb-24 px-4 sm:px-6 lg:px-8">
        {/* Glow decoration */}
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[300px] bg-emerald-500/20 blur-[120px] rounded-full pointer-events-none"></div>

        <div className="max-w-7xl mx-auto text-center relative z-10 space-y-6">
          <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-300 text-xs font-semibold backdrop-blur-md">
            <Award className="w-4 h-4 text-emerald-400" />
            <span>Smart Agricultural Marketplace • Haryana Agri-Tech SRS Prototype</span>
          </div>

          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-black tracking-tight leading-tight max-w-4xl mx-auto">
            Direct Trade, Fair Prices & <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 via-teal-300 to-amber-300">Guaranteed Escrow</span>
          </h1>

          <p className="text-base sm:text-lg text-emerald-100/80 max-w-2xl mx-auto leading-relaxed">
            Eliminating middlemen by empowering verified farmers with live digital bidding, Haryana APMC Mandi price intelligence, and simulated escrow payment protection.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-4">
            {isAuthenticated ? (
              <Link
                to={getDashboardPath()}
                className="w-full sm:w-auto px-8 py-3.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-base shadow-lg shadow-emerald-500/25 transition-all flex items-center justify-center space-x-2"
              >
                <span>Go to Dashboard</span>
                <ArrowRight className="w-5 h-5" />
              </Link>
            ) : (
              <>
                <Link
                  to="/register"
                  className="w-full sm:w-auto px-8 py-3.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-base shadow-lg shadow-emerald-500/25 transition-all flex items-center justify-center space-x-2"
                >
                  <span>Register as Farmer or Buyer</span>
                  <ArrowRight className="w-5 h-5" />
                </Link>
                <Link
                  to="/marketplace"
                  className="w-full sm:w-auto px-8 py-3.5 rounded-xl bg-white/10 hover:bg-white/20 text-white font-semibold text-base border border-white/20 backdrop-blur-md transition-all flex items-center justify-center space-x-2"
                >
                  <span>Explore Open Produce Lots</span>
                </Link>
              </>
            )}
          </div>

          {/* Quick Stats Banner */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 max-w-4xl mx-auto pt-10 text-left">
            <div className="bg-white/5 border border-white/10 p-4 rounded-xl backdrop-blur-xs">
              <p className="text-2xl font-black text-emerald-400">₹0</p>
              <p className="text-xs text-slate-300">Middlemen Commission</p>
            </div>
            <div className="bg-white/5 border border-white/10 p-4 rounded-xl backdrop-blur-xs">
              <p className="text-2xl font-black text-emerald-400">100%</p>
              <p className="text-xs text-slate-300">Escrow Payment Security</p>
            </div>
            <div className="bg-white/5 border border-white/10 p-4 rounded-xl backdrop-blur-xs">
              <p className="text-2xl font-black text-emerald-400">6 Districts</p>
              <p className="text-xs text-slate-300">Haryana APMC Mandi Data</p>
            </div>
            <div className="bg-white/5 border border-white/10 p-4 rounded-xl backdrop-blur-xs">
              <p className="text-2xl font-black text-emerald-400">Wheat • Paddy</p>
              <p className="text-xs text-slate-300">Mustard & Major Grains</p>
            </div>
          </div>
        </div>
      </section>

      {/* Core Workflow Steps */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto space-y-3 mb-12">
          <h2 className="text-xs font-bold text-emerald-600 uppercase tracking-widest">
            End-to-End Trade Lifecycle
          </h2>
          <p className="text-3xl font-extrabold text-slate-900 tracking-tight">
            How SAM Empowers Agriculture
          </p>
          <p className="text-slate-600 text-sm">
            From field harvest to bank settlement — a seamless 5-stage verified trading workflow.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-5 gap-6">
          {/* Step 1 */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col justify-between hover:shadow-md transition-shadow">
            <div>
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-800 font-black flex items-center justify-center text-sm mb-4">
                1
              </div>
              <h3 className="font-bold text-slate-900 text-base mb-2">Produce Listing</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Farmer lists harvested lots with moisture %, grade, min price, and farm-gate location.
              </p>
            </div>
            <div className="pt-4 mt-4 border-t border-slate-100 flex items-center text-xs text-emerald-700 font-semibold">
              <Sprout className="w-4 h-4 mr-1.5" />
              <span>Verified Harvest</span>
            </div>
          </div>

          {/* Step 2 */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col justify-between hover:shadow-md transition-shadow">
            <div>
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-800 font-black flex items-center justify-center text-sm mb-4">
                2
              </div>
              <h3 className="font-bold text-slate-900 text-base mb-2">Competitive Bidding</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Verified buyers submit price offers with transparent Mandi benchmark comparisons.
              </p>
            </div>
            <div className="pt-4 mt-4 border-t border-slate-100 flex items-center text-xs text-emerald-700 font-semibold">
              <TrendingUp className="w-4 h-4 mr-1.5" />
              <span>Mandi Price Intel</span>
            </div>
          </div>

          {/* Step 3 */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col justify-between hover:shadow-md transition-shadow">
            <div>
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-800 font-black flex items-center justify-center text-sm mb-4">
                3
              </div>
              <h3 className="font-bold text-slate-900 text-base mb-2">Contract & Escrow</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Farmer accepts the best bid. Buyer funds simulated escrow to guarantee payment.
              </p>
            </div>
            <div className="pt-4 mt-4 border-t border-slate-100 flex items-center text-xs text-emerald-700 font-semibold">
              <ShieldCheck className="w-4 h-4 mr-1.5" />
              <span>Escrow Protected</span>
            </div>
          </div>

          {/* Step 4 */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col justify-between hover:shadow-md transition-shadow">
            <div>
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-800 font-black flex items-center justify-center text-sm mb-4">
                4
              </div>
              <h3 className="font-bold text-slate-900 text-base mb-2">Dispatch & Delivery</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Farmer dispatches grain. Buyer confirms weighbridge receipts and quality inspection.
              </p>
            </div>
            <div className="pt-4 mt-4 border-t border-slate-100 flex items-center text-xs text-emerald-700 font-semibold">
              <Truck className="w-4 h-4 mr-1.5" />
              <span>Audit Proof</span>
            </div>
          </div>

          {/* Step 5 */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col justify-between hover:shadow-md transition-shadow">
            <div>
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-800 font-black flex items-center justify-center text-sm mb-4">
                5
              </div>
              <h3 className="font-bold text-slate-900 text-base mb-2">Instant Release</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Escrow funds automatically unlock to the farmer’s account or Admin resolves disputes.
              </p>
            </div>
            <div className="pt-4 mt-4 border-t border-slate-100 flex items-center text-xs text-emerald-700 font-semibold">
              <Landmark className="w-4 h-4 mr-1.5" />
              <span>Guaranteed Pay</span>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Highlights Grid */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-gradient-to-br from-slate-900 via-slate-800 to-emerald-950 text-white rounded-3xl p-8 sm:p-12 border border-slate-800 shadow-xl">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="space-y-4">
              <div className="w-12 h-12 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center border border-emerald-500/30">
                <Scale className="w-6 h-6" />
              </div>
              <h3 className="text-2xl font-bold">Mandi Price Benchmarks</h3>
              <p className="text-sm text-slate-300 leading-relaxed">
                Live 30-day modal price charts across Karnal, Hisar, Ambala, Rohtak, Sirsa, and Panipat so farmers never sell below government Minimum Support Prices (MSP).
              </p>
              <Link
                to="/benchmarks"
                className="inline-flex items-center text-sm font-semibold text-emerald-400 hover:text-emerald-300 pt-2"
              >
                <span>View Price Trends</span>
                <ArrowRight className="w-4 h-4 ml-1" />
              </Link>
            </div>

            <div className="space-y-4">
              <div className="w-12 h-12 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center border border-emerald-500/30">
                <Warehouse className="w-6 h-6" />
              </div>
              <h3 className="text-2xl font-bold">Integrated Warehousing</h3>
              <p className="text-sm text-slate-300 leading-relaxed">
                Store harvested crops in WDRA/Govt-accredited cold and dry warehouses to avoid distress sales during peak harvest gluts.
              </p>
              <Link
                to="/warehouses"
                className="inline-flex items-center text-sm font-semibold text-emerald-400 hover:text-emerald-300 pt-2"
              >
                <span>Browse Warehouses</span>
                <ArrowRight className="w-4 h-4 ml-1" />
              </Link>
            </div>

            <div className="space-y-4">
              <div className="w-12 h-12 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center border border-emerald-500/30">
                <ShieldCheck className="w-6 h-6" />
              </div>
              <h3 className="text-2xl font-bold">Admin Dispute Adjudication</h3>
              <p className="text-sm text-slate-300 leading-relaxed">
                Fair arbitrations with photographic inspection evidence, weighbridge slip verification, and multi-option escrow disbursement (Full Release, Refund, Split).
              </p>
              <Link
                to="/login"
                className="inline-flex items-center text-sm font-semibold text-emerald-400 hover:text-emerald-300 pt-2"
              >
                <span>Login to SAM Platform</span>
                <ArrowRight className="w-4 h-4 ml-1" />
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Role Cards */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-2xl mx-auto space-y-2 mb-10">
          <h2 className="text-xs font-bold text-emerald-600 uppercase tracking-widest">
            Tailored User Portals
          </h2>
          <p className="text-2xl sm:text-3xl font-extrabold text-slate-900">
            Choose Your Profile to Get Started
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs hover:border-emerald-500 transition-colors space-y-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-800 flex items-center justify-center">
              <Sprout className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900">Farmer Portal</h4>
            <p className="text-xs text-slate-600">
              List crops, monitor bids in real time, accept best prices, dispatch lots, and book warehouse space.
            </p>
          </div>

          <div className="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs hover:border-emerald-500 transition-colors space-y-3">
            <div className="w-10 h-10 rounded-xl bg-blue-100 text-blue-800 flex items-center justify-center">
              <Users className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900">Buyer Portal</h4>
            <p className="text-xs text-slate-600">
              Browse graded produce, place competitive bids, fund escrow, inspect deliveries, and request resolutions.
            </p>
          </div>

          <div className="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs hover:border-emerald-500 transition-colors space-y-3">
            <div className="w-10 h-10 rounded-xl bg-amber-100 text-amber-800 flex items-center justify-center">
              <Warehouse className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900">Warehouse Operator</h4>
            <p className="text-xs text-slate-600">
              Manage storage capacity, set storage charges, approve bookings, and release stored produce.
            </p>
          </div>

          <div className="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs hover:border-emerald-500 transition-colors space-y-3">
            <div className="w-10 h-10 rounded-xl bg-purple-100 text-purple-800 flex items-center justify-center">
              <Layers className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900">Market Admin</h4>
            <p className="text-xs text-slate-600">
              Verify users, manage Mandi price benchmarks, resolve disputes, and audit marketplace transactions.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
};
