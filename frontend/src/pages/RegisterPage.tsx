import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Sprout, Building, Warehouse, AlertCircle, CheckCircle2, UserCheck, ShieldCheck } from 'lucide-react';
import { authApi } from '../api';
import { Role } from '../types';
import { useLanguage } from '../context/LanguageContext';

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useLanguage();

  const [role, setRole] = useState<Role>('FARMER');
  const [name, setName] = useState('');
  const [mobileNumber, setMobileNumber] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // Farmer fields
  const [state, setState] = useState('Haryana');
  const [district, setDistrict] = useState('Karnal');
  const [village, setVillage] = useState('Taraori');
  const [bankAccountNumber, setBankAccountNumber] = useState('9876543210123');
  const [ifscCode, setIfscCode] = useState('SBIN0001234');
  const [preferredLanguage, setPreferredLanguage] = useState('en');

  // Buyer fields
  const [businessName, setBusinessName] = useState('');
  const [gstin, setGstin] = useState('06AAAAA0000A1Z5');
  const [businessAddress, setBusinessAddress] = useState('');
  const [buyerType, setBuyerType] = useState('TRADER');

  // Warehouse fields
  const [warehouseOperatorName, setWarehouseOperatorName] = useState('');
  const [warehouseDistrict, setWarehouseDistrict] = useState('Karnal');
  const [licenseNumber, setLicenseNumber] = useState('WDRA-HR-9988');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const districts = ['Karnal', 'Hisar', 'Ambala', 'Rohtak', 'Sirsa', 'Panipat'];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const payload: any = {
        name,
        mobileNumber,
        email: email || undefined,
        password,
        role,
      };

      if (role === 'FARMER') {
        payload.state = state;
        payload.district = district;
        payload.village = village;
        payload.bankAccountNumber = bankAccountNumber;
        payload.ifscCode = ifscCode;
        payload.preferredLanguage = preferredLanguage;
      } else if (role === 'BUYER') {
        payload.businessName = businessName || `${name} Agri Trading`;
        payload.gstin = gstin;
        payload.businessAddress = businessAddress || `${district} Mandi Yard, Haryana`;
        payload.buyerType = buyerType;
      } else if (role === 'WAREHOUSE_OPERATOR') {
        payload.warehouseOperatorName = warehouseOperatorName || name;
        payload.district = warehouseDistrict;
        payload.licenseNumber = licenseNumber;
      }

      const response = await authApi.register(payload);
      // Navigate to OTP verification
      navigate(`/verify-otp?mobile=${mobileNumber}&otp=${response.data.mockOtp || '123456'}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Please check your inputs.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[85vh] flex items-center justify-center px-4 sm:px-6 lg:px-8 py-10">
      <div className="max-w-2xl w-full bg-white p-8 sm:p-10 rounded-3xl border border-slate-200 shadow-xl space-y-6">
        <div className="text-center space-y-2">
          <div className="w-12 h-12 rounded-2xl bg-emerald-600 text-white flex items-center justify-center mx-auto shadow-lg shadow-emerald-600/30">
            <Sprout className="w-7 h-7" />
          </div>
          <h2 className="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight">
            Create SAM Account
          </h2>
          <p className="text-xs sm:text-sm text-slate-500">
            Join the verified digital agricultural trade network in Haryana
          </p>
        </div>

        {error && (
          <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 rounded-xl text-xs flex items-center space-x-2">
            <AlertCircle className="w-4 h-4 flex-shrink-0 text-rose-600" />
            <span>{error}</span>
          </div>
        )}

        {/* Role Selector Tabs */}
        <div>
          <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-2">
            Select User Role
          </label>
          <div className="grid grid-cols-3 gap-2.5">
            <button
              type="button"
              onClick={() => setRole('FARMER')}
              className={`flex flex-col items-center justify-center p-3 rounded-2xl border text-xs font-bold transition-all ${
                role === 'FARMER'
                  ? 'border-emerald-600 bg-emerald-50 text-emerald-900 ring-2 ring-emerald-500/20 shadow-xs'
                  : 'border-slate-200 text-slate-600 hover:bg-slate-50'
              }`}
            >
              <Sprout className="w-5 h-5 mb-1 text-emerald-600" />
              <span>{t('farmer')}</span>
            </button>

            <button
              type="button"
              onClick={() => setRole('BUYER')}
              className={`flex flex-col items-center justify-center p-3 rounded-2xl border text-xs font-bold transition-all ${
                role === 'BUYER'
                  ? 'border-emerald-600 bg-emerald-50 text-emerald-900 ring-2 ring-emerald-500/20 shadow-xs'
                  : 'border-slate-200 text-slate-600 hover:bg-slate-50'
              }`}
            >
              <Building className="w-5 h-5 mb-1 text-emerald-600" />
              <span>{t('buyer')} / Trader</span>
            </button>

            <button
              type="button"
              onClick={() => setRole('WAREHOUSE_OPERATOR')}
              className={`flex flex-col items-center justify-center p-3 rounded-2xl border text-xs font-bold transition-all ${
                role === 'WAREHOUSE_OPERATOR'
                  ? 'border-emerald-600 bg-emerald-50 text-emerald-900 ring-2 ring-emerald-500/20 shadow-xs'
                  : 'border-slate-200 text-slate-600 hover:bg-slate-50'
              }`}
            >
              <Warehouse className="w-5 h-5 mb-1 text-emerald-600" />
              <span>Warehouse Mgr</span>
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* General info */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Full Name
              </label>
              <input
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. Ramesh Kumar"
                className="w-full p-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                10-Digit Mobile Number
              </label>
              <input
                type="tel"
                maxLength={10}
                required
                value={mobileNumber}
                onChange={(e) => setMobileNumber(e.target.value)}
                placeholder="9876543210"
                className="w-full p-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm font-medium"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Email Address (Optional)
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="ramesh@example.com"
                className="w-full p-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Password
              </label>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Min 6 characters"
                className="w-full p-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm font-medium"
              />
            </div>
          </div>

          {/* Role specific profile fields */}
          {role === 'FARMER' && (
            <div className="p-4 bg-emerald-50/50 rounded-2xl border border-emerald-100 space-y-3">
              <h4 className="text-xs font-bold uppercase tracking-wider text-emerald-900">
                Farmer Agricultural & Bank Profile
              </h4>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">State</label>
                  <input
                    type="text"
                    value={state}
                    disabled
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs font-medium text-slate-600"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">District</label>
                  <select
                    value={district}
                    onChange={(e) => setDistrict(e.target.value)}
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs font-medium"
                  >
                    {districts.map((d) => (
                      <option key={d} value={d}>{d}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">Village</label>
                  <input
                    type="text"
                    value={village}
                    onChange={(e) => setVillage(e.target.value)}
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">Bank Account No (Direct Credit)</label>
                  <input
                    type="text"
                    value={bankAccountNumber}
                    onChange={(e) => setBankAccountNumber(e.target.value)}
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">Bank IFSC Code</label>
                  <input
                    type="text"
                    value={ifscCode}
                    onChange={(e) => setIfscCode(e.target.value)}
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs uppercase"
                  />
                </div>
              </div>
            </div>
          )}

          {role === 'BUYER' && (
            <div className="p-4 bg-emerald-50/50 rounded-2xl border border-emerald-100 space-y-3">
              <h4 className="text-xs font-bold uppercase tracking-wider text-emerald-900">
                Buyer & Commercial Entity Details
              </h4>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">Business / Firm Name</label>
                  <input
                    type="text"
                    value={businessName}
                    onChange={(e) => setBusinessName(e.target.value)}
                    placeholder="e.g. Haryana Agro Millers Ltd"
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">GSTIN Number</label>
                  <input
                    type="text"
                    value={gstin}
                    onChange={(e) => setGstin(e.target.value)}
                    placeholder="06AAAAA0000A1Z5"
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs uppercase"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">Buyer Type</label>
                  <select
                    value={buyerType}
                    onChange={(e) => setBuyerType(e.target.value)}
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs"
                  >
                    <option value="TRADER">Wholesale Trader / Commission Agent</option>
                    <option value="PROCESSOR">Flour / Rice Mill Processor</option>
                    <option value="EXPORTER">Commodity Exporter</option>
                    <option value="CORPORATE_BUYER">Corporate FMCG / Bulk Purchaser</option>
                  </select>
                </div>
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">Business Address</label>
                  <input
                    type="text"
                    value={businessAddress}
                    onChange={(e) => setBusinessAddress(e.target.value)}
                    placeholder="Karnal Mandi Yard, Haryana"
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs"
                  />
                </div>
              </div>
            </div>
          )}

          {role === 'WAREHOUSE_OPERATOR' && (
            <div className="p-4 bg-emerald-50/50 rounded-2xl border border-emerald-100 space-y-3">
              <h4 className="text-xs font-bold uppercase tracking-wider text-emerald-900">
                Warehouse Operator Details
              </h4>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">Operating District</label>
                  <select
                    value={warehouseDistrict}
                    onChange={(e) => setWarehouseDistrict(e.target.value)}
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs"
                  >
                    {districts.map((d) => (
                      <option key={d} value={d}>{d}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-[11px] font-semibold text-slate-700 mb-1">WDRA / State License No</label>
                  <input
                    type="text"
                    value={licenseNumber}
                    onChange={(e) => setLicenseNumber(e.target.value)}
                    placeholder="WDRA-HR-9988"
                    className="w-full p-2 rounded-lg bg-white border border-slate-300 text-xs uppercase"
                  />
                </div>
              </div>
            </div>
          )}

          <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 text-[11px] text-slate-500 space-y-1">
            <div className="flex items-center space-x-1 font-semibold text-slate-700">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
              <span>Verification Protocol</span>
            </div>
            <p>
              Newly registered accounts receive <span className="font-semibold text-amber-700">PENDING_VERIFICATION</span> status. The Admin can verify your profile to enable live trading.
            </p>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 px-4 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm shadow-md shadow-emerald-700/20 transition-all flex items-center justify-center space-x-2 disabled:opacity-50"
          >
            {loading ? (
              <span>Submitting Registration...</span>
            ) : (
              <>
                <UserCheck className="w-4 h-4" />
                <span>Register & Proceed to OTP</span>
              </>
            )}
          </button>
        </form>

        <div className="text-center pt-2 border-t border-slate-100 text-xs text-slate-600">
          <span>Already registered? </span>
          <Link to="/login" className="font-bold text-emerald-700 hover:text-emerald-800">
            {t('login')}
          </Link>
        </div>
      </div>
    </div>
  );
};
