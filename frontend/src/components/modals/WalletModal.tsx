import React, { useState, useEffect } from 'react';
import {
  X,
  Wallet,
  Coins,
  ArrowDownLeft,
  ArrowUpRight,
  ShieldCheck,
  Building,
  Smartphone,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  Lock,
  Landmark,
  History,
  CreditCard,
  QrCode,
  Sprout,
  Warehouse,
  Sparkles,
  Receipt,
  FileCheck,
  Percent,
} from 'lucide-react';
import { walletApi, warehouseApi } from '../../api';
import { WalletResponse, WalletTransaction, WarehouseBookingResponse } from '../../types';
import { useAuth } from '../../context/AuthContext';

interface WalletModalProps {
  isOpen: boolean;
  onClose: () => void;
  defaultTab?: 'OVERVIEW' | 'DEPOSIT' | 'WITHDRAW' | 'ENWR_ADVANCE' | 'PASSBOOK';
}

export const WalletModal: React.FC<WalletModalProps> = ({
  isOpen,
  onClose,
  defaultTab = 'OVERVIEW',
}) => {
  const { user } = useAuth();
  const userRole = user?.role || 'BUYER';
  const isBuyer = userRole === 'BUYER';
  const isFarmer = userRole === 'FARMER';
  const isOperator = userRole === 'WAREHOUSE_OPERATOR';

  const [activeTab, setActiveTab] = useState<'OVERVIEW' | 'DEPOSIT' | 'WITHDRAW' | 'ENWR_ADVANCE' | 'PASSBOOK'>(defaultTab);
  const [wallet, setWallet] = useState<WalletResponse | null>(null);
  const [transactions, setTransactions] = useState<WalletTransaction[]>([]);
  const [bookings, setBookings] = useState<WarehouseBookingResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Deposit Form (Buyer only)
  const [depositAmount, setDepositAmount] = useState<number>(50000);
  const [depositMethod, setDepositMethod] = useState<'UPI' | 'NET_BANKING' | 'BANK_TRANSFER'>('UPI');

  // Withdraw / Redeem Leftover Tokens Form
  const [withdrawAmount, setWithdrawAmount] = useState<number>(25000);
  const [withdrawMethod, setWithdrawMethod] = useState<'BANK_ACCOUNT' | 'UPI'>('BANK_ACCOUNT');
  const [bankAccount, setBankAccount] = useState('50100492819234');
  const [ifsc, setIfsc] = useState('SBIN0001234');
  const [holderName, setHolderName] = useState(user?.fullName || 'Authorized Account Holder');
  const [upiId, setUpiId] = useState('farmer@upi');

  // e-NWR Advance Form (Farmer only)
  const [selectedBookingId, setSelectedBookingId] = useState<string>('');
  const [advanceAmount, setAdvanceAmount] = useState<number>(75000);

  useEffect(() => {
    setActiveTab(defaultTab);
  }, [defaultTab, isOpen]);

  const loadWalletData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [walletRes, txRes] = await Promise.all([
        walletApi.getWallet(),
        walletApi.getRecentTransactions(),
      ]);
      setWallet(walletRes.data);
      setTransactions(txRes.data || []);

      if (isFarmer) {
        try {
          const bookingsRes = await warehouseApi.getMyBookings();
          const approved = (bookingsRes.data || []).filter(
            (b: any) => b.status === 'APPROVED' || b.status === 'COMPLETED'
          );
          setBookings(approved);
          if (approved.length > 0) {
            setSelectedBookingId(approved[0].id.toString());
          }
        } catch (e) {
          // Handled gracefully
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load wallet information.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      loadWalletData();
    }
  }, [isOpen, userRole]);

  const handleDeposit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (depositAmount <= 0) return;
    setActionLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const res = await walletApi.deposit({
        amount: depositAmount,
        paymentMethod: depositMethod,
        notes: `Simulated ${depositMethod} token deposit to SAM Vault`,
      });

      setSuccessMessage(
        `Deposit Successful! ₹${depositAmount.toLocaleString('en-IN')} converted into Trade Security Tokens (${res.data?.tokenCode || 'TOK-SEC'}). Ready for bidding!`
      );
      await loadWalletData();
      setTimeout(() => setActiveTab('OVERVIEW'), 1500);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to deposit money.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleWithdraw = async (e: React.FormEvent) => {
    e.preventDefault();
    if (withdrawAmount <= 0) return;
    setActionLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const res = await walletApi.withdraw({
        amount: withdrawAmount,
        withdrawMethod,
        bankAccountNumber: withdrawMethod === 'BANK_ACCOUNT' ? bankAccount : undefined,
        ifscCode: withdrawMethod === 'BANK_ACCOUNT' ? ifsc : undefined,
        accountHolderName: withdrawMethod === 'BANK_ACCOUNT' ? holderName : undefined,
        upiId: withdrawMethod === 'UPI' ? upiId : undefined,
      });

      const messagePrefix = isBuyer
        ? `Leftover Tokens Exchanged to Cash! ₹${withdrawAmount.toLocaleString('en-IN')}`
        : `Payout Transferred to Bank! ₹${withdrawAmount.toLocaleString('en-IN')}`;

      setSuccessMessage(
        `${messagePrefix} sent to ${
          withdrawMethod === 'UPI' ? upiId : `Bank A/C ending in ${bankAccount.slice(-4)}`
        }. Ref: ${res.data?.referenceId}`
      );
      await loadWalletData();
      setTimeout(() => setActiveTab('OVERVIEW'), 1500);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to process withdrawal.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleENWRPledge = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedBookingId || advanceAmount <= 0) return;
    setActionLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const res = await walletApi.requestENWRAdvance(selectedBookingId, advanceAmount);
      setSuccessMessage(
        `🎉 70% e-NWR Liquidity Advance Approved! ₹${advanceAmount.toLocaleString('en-IN')} credited to your wallet against e-NWR Token: ${res.data?.tokenCode}. You can withdraw to bank right away!`
      );
      await loadWalletData();
      setTimeout(() => setActiveTab('OVERVIEW'), 1800);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to process e-NWR pledge advance.');
    } finally {
      setActionLoading(false);
    }
  };

  if (!isOpen) return null;

  const available = wallet?.availableBalance ?? 0;
  const locked = wallet?.lockedBalance ?? 0;
  const total = available + locked;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl max-w-2xl w-full shadow-2xl border border-slate-200 overflow-hidden animate-in fade-in zoom-in duration-200">
        {/* Role-Specific Header */}
        <div className={`p-6 text-white flex items-center justify-between border-b ${
          isFarmer
            ? 'bg-gradient-to-r from-emerald-950 via-slate-900 to-emerald-900 border-emerald-800'
            : isOperator
            ? 'bg-gradient-to-r from-blue-950 via-slate-900 to-indigo-950 border-blue-800'
            : 'bg-slate-900 border-slate-800'
        }`}>
          <div className="flex items-center space-x-3">
            <div className={`w-10 h-10 rounded-2xl flex items-center justify-center border ${
              isFarmer
                ? 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30'
                : isOperator
                ? 'bg-blue-500/20 text-blue-400 border-blue-500/30'
                : 'bg-amber-500/20 text-amber-400 border-amber-500/30'
            }`}>
              {isFarmer ? (
                <Sprout className="w-6 h-6" />
              ) : isOperator ? (
                <Warehouse className="w-6 h-6" />
              ) : (
                <Coins className="w-6 h-6" />
              )}
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <h2 className="text-lg font-black tracking-tight">
                  {isFarmer
                    ? 'Farmer Direct Crop Earnings Vault'
                    : isOperator
                    ? 'Warehouse Storage Rental Vault'
                    : 'Buyer Security Token & Escrow Vault'}
                </h2>
                <span className="px-2 py-0.5 rounded-md bg-white/10 text-[10px] font-mono uppercase font-bold tracking-wider">
                  {userRole}
                </span>
              </div>
              <p className="text-xs text-slate-400 font-mono">
                {wallet ? wallet.walletNumber : 'SAM Digital Vault'} • 100% Sovereign Trust Guarantee
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Role Domain Explanation Banner */}
        {isFarmer && (
          <div className="bg-emerald-50/90 border-b border-emerald-100 px-6 py-2.5 flex items-center justify-between text-xs text-emerald-900">
            <div className="flex items-center space-x-2">
              <ShieldCheck className="w-4 h-4 text-emerald-700 flex-shrink-0" />
              <span>
                <strong>0% Deposit Required for Farmers:</strong> You sell crops and receive 100% escrow settlements directly to your bank account with zero listing fees.
              </span>
            </div>
          </div>
        )}

        {isOperator && (
          <div className="bg-blue-50/90 border-b border-blue-100 px-6 py-2.5 flex items-center justify-between text-xs text-blue-900">
            <div className="flex items-center space-x-2">
              <ShieldCheck className="w-4 h-4 text-blue-700 flex-shrink-0" />
              <span>
                <strong>Warehouse Operator Revenue:</strong> Storage rentals are credited to your vault upon farmer produce check-in. Instant commercial bank transfer available.
              </span>
            </div>
          </div>
        )}

        {isBuyer && (
          <div className="bg-amber-50/90 border-b border-amber-100 px-6 py-2.5 flex items-center justify-between text-xs text-amber-900">
            <div className="flex items-center space-x-2">
              <Coins className="w-4 h-4 text-amber-700 flex-shrink-0" />
              <span>
                <strong>How Buyer Tokens Work:</strong> Deposit money to acquire 1:1 Bidding Tokens. Leftover tokens can be exchanged back to real money anytime with 1-click!
              </span>
            </div>
          </div>
        )}

        {/* Navigation Tabs - Tailored by Role */}
        <div className="flex border-b border-slate-200 bg-slate-50 px-6 pt-3 gap-2 overflow-x-auto text-xs font-bold">
          <button
            type="button"
            onClick={() => setActiveTab('OVERVIEW')}
            className={`pb-3 px-3 border-b-2 flex items-center space-x-1.5 transition-colors whitespace-nowrap ${
              activeTab === 'OVERVIEW'
                ? 'border-emerald-600 text-emerald-700 font-black'
                : 'border-transparent text-slate-500 hover:text-slate-900'
            }`}
          >
            <Wallet className="w-4 h-4" />
            <span>{isFarmer ? 'Sales Overview' : isOperator ? 'Revenue Overview' : 'Token Vault'}</span>
          </button>

          {/* Deposit tab shown ONLY for Buyers */}
          {isBuyer && (
            <button
              type="button"
              onClick={() => setActiveTab('DEPOSIT')}
              className={`pb-3 px-3 border-b-2 flex items-center space-x-1.5 transition-colors whitespace-nowrap ${
                activeTab === 'DEPOSIT'
                  ? 'border-emerald-600 text-emerald-700 font-black'
                  : 'border-transparent text-slate-500 hover:text-slate-900'
              }`}
            >
              <ArrowDownLeft className="w-4 h-4 text-emerald-600" />
              <span>Deposit (Get Tokens)</span>
            </button>
          )}

          {/* Withdraw / Token Exchange tab */}
          <button
            type="button"
            onClick={() => setActiveTab('WITHDRAW')}
            className={`pb-3 px-3 border-b-2 flex items-center space-x-1.5 transition-colors whitespace-nowrap ${
              activeTab === 'WITHDRAW'
                ? 'border-emerald-600 text-emerald-700 font-black'
                : 'border-transparent text-slate-500 hover:text-slate-900'
            }`}
          >
            <ArrowUpRight className="w-4 h-4 text-rose-600" />
            <span>{isBuyer ? '🔄 Exchange Tokens → Bank' : '🏧 Withdraw to Bank'}</span>
          </button>

          {/* Unique Feature Tab: e-NWR Warehouse Pledge Liquidity Advance (Farmer Only) */}
          {isFarmer && (
            <button
              type="button"
              onClick={() => setActiveTab('ENWR_ADVANCE')}
              className={`pb-3 px-3 border-b-2 flex items-center space-x-1.5 transition-colors whitespace-nowrap ${
                activeTab === 'ENWR_ADVANCE'
                  ? 'border-emerald-600 text-emerald-700 font-black'
                  : 'border-transparent text-slate-500 hover:text-slate-900'
              }`}
            >
              <Sparkles className="w-4 h-4 text-amber-500" />
              <span>🌾 e-NWR 70% Pledge Advance</span>
            </button>
          )}

          <button
            type="button"
            onClick={() => setActiveTab('PASSBOOK')}
            className={`pb-3 px-3 border-b-2 flex items-center space-x-1.5 transition-colors whitespace-nowrap ${
              activeTab === 'PASSBOOK'
                ? 'border-emerald-600 text-emerald-700 font-black'
                : 'border-transparent text-slate-500 hover:text-slate-900'
            }`}
          >
            <History className="w-4 h-4" />
            <span>Passbook Ledger</span>
          </button>
        </div>

        {/* Content Body */}
        <div className="p-6 space-y-5">
          {successMessage && (
            <div className="p-4 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-2xl text-xs flex items-center space-x-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0" />
              <span>{successMessage}</span>
            </div>
          )}

          {error && (
            <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs flex items-center space-x-2">
              <AlertCircle className="w-4 h-4 text-rose-600 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {loading ? (
            <div className="py-12 flex justify-center">
              <div className="animate-spin rounded-full h-8 w-8 border-4 border-emerald-600 border-t-transparent"></div>
            </div>
          ) : (
            <>
              {/* TAB 1: OVERVIEW */}
              {activeTab === 'OVERVIEW' && (
                <div className="space-y-5">
                  {/* Balance Cards */}
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                    <div className="bg-gradient-to-br from-emerald-950 to-slate-900 text-white p-5 rounded-3xl relative overflow-hidden shadow-lg">
                      <div className="absolute top-2 right-2 p-2 bg-emerald-500/10 rounded-xl">
                        {isFarmer ? (
                          <Sprout className="w-6 h-6 text-emerald-400" />
                        ) : (
                          <Coins className="w-6 h-6 text-emerald-400" />
                        )}
                      </div>
                      <span className="text-[11px] font-semibold text-emerald-300 uppercase tracking-wider block">
                        {isFarmer ? 'Available Payout Cash' : isOperator ? 'Available Rental Income' : 'Available Bidding Tokens'}
                      </span>
                      <h3 className="text-2xl font-black mt-1">₹{available.toLocaleString('en-IN')}</h3>
                      <p className="text-[10px] text-slate-400 mt-2">
                        {isFarmer
                          ? 'Settled earnings ready for instant bank transfer'
                          : isOperator
                          ? 'Net storage revenue ready to cash out'
                          : 'Tokens ready for applying bids & trade contracts'}
                      </p>
                    </div>

                    <div className="bg-amber-50 border border-amber-200 p-5 rounded-3xl">
                      <span className="text-[11px] font-semibold text-amber-700 uppercase tracking-wider block">
                        {isBuyer ? 'Tokens Locked in Bids / Escrow' : 'In-Transit Escrow Guarantee'}
                      </span>
                      <h3 className="text-2xl font-black text-amber-900 mt-1">₹{locked.toLocaleString('en-IN')}</h3>
                      <p className="text-[10px] text-amber-700 mt-2">
                        {isBuyer ? 'Earnest money deposit held in vault' : 'Buyer funds held safely until delivery check'}
                      </p>
                    </div>

                    <div className="bg-slate-50 border border-slate-200 p-5 rounded-3xl">
                      <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider block">
                        {isFarmer ? 'Total Lifetime Sales' : isOperator ? 'Total Lifetime Revenue' : 'Total Trading Vault'}
                      </span>
                      <h3 className="text-2xl font-black text-slate-900 mt-1">
                        ₹{(isFarmer || isOperator ? wallet?.totalDeposited ?? available : total).toLocaleString('en-IN')}
                      </h3>
                      <p className="text-[10px] text-slate-500 mt-2">Cumulative volume transacted</p>
                    </div>
                  </div>

                  {/* Token Certificate Badge / Farmer Direct Payout Certificate */}
                  <div className="p-4 bg-emerald-50/70 border border-emerald-200/80 rounded-2xl flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                    <div className="flex items-center space-x-3">
                      <div className="p-2 bg-emerald-100 rounded-xl text-emerald-700">
                        <ShieldCheck className="w-6 h-6" />
                      </div>
                      <div>
                        <h4 className="text-xs font-bold text-emerald-950">
                          {isBuyer ? 'Trade Security Token Certificate' : 'Accredited Nodal Bank Settlement ID'}
                        </h4>
                        <p className="text-[11px] font-mono font-bold text-emerald-700 mt-0.5">
                          {wallet?.activeTokenBatch || 'TOK-SEC-2026-ACTIVE'}
                        </p>
                        <p className="text-[10px] text-emerald-600">
                          {isBuyer
                            ? '1:1 Rupee-Backed Token for bidding. Leftover tokens can be redeemed back to bank anytime.'
                            : 'RBI-regulated direct account transfer with zero intermediary deductions.'}
                        </p>
                      </div>
                    </div>

                    {isBuyer ? (
                      <button
                        onClick={() => setActiveTab('DEPOSIT')}
                        className="px-3 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-xs transition-colors self-start sm:self-center"
                      >
                        + Top Up Tokens
                      </button>
                    ) : (
                      <button
                        onClick={() => setActiveTab('WITHDRAW')}
                        className="px-3 py-2 bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-bold rounded-xl shadow-xs transition-colors self-start sm:self-center"
                      >
                        🏧 Transfer to Bank
                      </button>
                    )}
                  </div>

                  {/* Quick Action Buttons */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
                    {isBuyer && (
                      <button
                        type="button"
                        onClick={() => setActiveTab('DEPOSIT')}
                        className="p-4 rounded-2xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs flex items-center justify-center space-x-2 shadow-xs transition-colors"
                      >
                        <ArrowDownLeft className="w-4 h-4" />
                        <span>Deposit Money & Get Tokens</span>
                      </button>
                    )}

                    <button
                      type="button"
                      onClick={() => setActiveTab('WITHDRAW')}
                      className={`p-4 rounded-2xl border font-bold text-xs flex items-center justify-center space-x-2 transition-colors ${
                        isBuyer
                          ? 'border-slate-300 hover:bg-slate-50 text-slate-800'
                          : 'bg-emerald-600 hover:bg-emerald-700 text-white border-transparent'
                      }`}
                    >
                      <ArrowUpRight className={`w-4 h-4 ${isBuyer ? 'text-rose-600' : 'text-white'}`} />
                      <span>{isBuyer ? '🔄 Exchange Leftover Tokens → Bank' : '🏧 Withdraw Payout to Bank Account'}</span>
                    </button>

                    {isFarmer && (
                      <button
                        type="button"
                        onClick={() => setActiveTab('ENWR_ADVANCE')}
                        className="p-4 rounded-2xl border border-amber-300 bg-amber-50 hover:bg-amber-100 text-amber-900 font-bold text-xs flex items-center justify-center space-x-2 transition-colors"
                      >
                        <Sparkles className="w-4 h-4 text-amber-600" />
                        <span>🌾 e-NWR 70% Warehouse Pledge Advance</span>
                      </button>
                    )}
                  </div>

                  {/* Recent 3 transactions */}
                  <div className="pt-2">
                    <div className="flex justify-between items-center mb-2">
                      <h4 className="text-xs font-bold text-slate-800 uppercase tracking-wider">Recent Activity</h4>
                      <button
                        onClick={() => setActiveTab('PASSBOOK')}
                        className="text-[11px] font-bold text-emerald-700 hover:underline"
                      >
                        View Full Passbook →
                      </button>
                    </div>
                    {transactions.length === 0 ? (
                      <p className="text-xs text-slate-400 py-3 text-center">No recent wallet transactions.</p>
                    ) : (
                      <div className="space-y-2">
                        {transactions.slice(0, 3).map((tx) => {
                          const isCredit =
                            tx.type === 'DEPOSIT' ||
                            tx.type === 'ESCROW_PAYOUT' ||
                            tx.type === 'BID_UNLOCK' ||
                            tx.type === 'ENWR_PLEDGE_ADVANCE' ||
                            tx.type === 'REFUND' ||
                            tx.type === 'DEMO_CREDIT';

                          return (
                            <div
                              key={tx.id}
                              className="p-3 bg-slate-50 rounded-xl border border-slate-100 flex items-center justify-between text-xs"
                            >
                              <div className="flex items-center space-x-2.5">
                                <div
                                  className={`w-7 h-7 rounded-lg flex items-center justify-center font-bold ${
                                    isCredit ? 'bg-emerald-100 text-emerald-700' : 'bg-rose-100 text-rose-700'
                                  }`}
                                >
                                  {isCredit ? (
                                    <ArrowDownLeft className="w-4 h-4" />
                                  ) : (
                                    <ArrowUpRight className="w-4 h-4" />
                                  )}
                                </div>
                                <div>
                                  <span className="font-bold text-slate-900 block">{tx.description}</span>
                                  <span className="text-[10px] text-slate-400">
                                    {new Date(tx.createdAt).toLocaleDateString('en-IN', {
                                      month: 'short',
                                      day: 'numeric',
                                      hour: '2-digit',
                                      minute: '2-digit',
                                    })}{' '}
                                    • {tx.referenceId}
                                  </span>
                                </div>
                              </div>
                              <div className="text-right font-bold">
                                <span className={isCredit ? 'text-emerald-700' : 'text-slate-900'}>
                                  {isCredit ? '+' : '-'}₹{tx.amount.toLocaleString('en-IN')}
                                </span>
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* TAB 2: DEPOSIT (BUYER ONLY) */}
              {activeTab === 'DEPOSIT' && isBuyer && (
                <form onSubmit={handleDeposit} className="space-y-5">
                  <div className="p-4 bg-amber-50/80 rounded-2xl border border-amber-200">
                    <p className="text-xs font-bold text-amber-900">How Token Deposit & Bidding Works for Buyers:</p>
                    <p className="text-[11px] text-amber-800 mt-1">
                      1. Deposit money into the RBI-nodal simulated escrow vault.
                      <br />
                      2. You immediately receive <strong>1:1 Trade Security Tokens</strong> in your account.
                      <br />
                      3. Use tokens to place bids on farmer produce lots.
                      <br />
                      4. <strong>Exchange Back:</strong> Any leftover or unused tokens can be exchanged back to real rupees anytime under the <em>"Exchange Tokens → Bank"</em> tab!
                    </p>
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                      Enter Deposit Amount (₹)
                    </label>
                    <input
                      type="number"
                      min="100"
                      step="500"
                      value={depositAmount}
                      onChange={(e) => setDepositAmount(Number(e.target.value))}
                      required
                      className="w-full text-xl font-extrabold p-3 border border-slate-300 rounded-2xl focus:ring-2 focus:ring-emerald-500 focus:outline-hidden"
                    />

                    {/* Quick amount chips */}
                    <div className="flex flex-wrap gap-2 mt-2">
                      {[10000, 25000, 50000, 100000, 500000].map((amt) => (
                        <button
                          key={amt}
                          type="button"
                          onClick={() => setDepositAmount(amt)}
                          className={`px-3 py-1 rounded-xl text-xs font-bold transition-colors ${
                            depositAmount === amt
                              ? 'bg-emerald-600 text-white'
                              : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                          }`}
                        >
                          + ₹{amt.toLocaleString('en-IN')}
                        </button>
                      ))}
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                      Select Deposit Method
                    </label>
                    <div className="grid grid-cols-3 gap-3">
                      {[
                        { id: 'UPI', label: 'UPI (GPay / PhonePe)', icon: Smartphone },
                        { id: 'NET_BANKING', label: 'Net Banking', icon: Building },
                        { id: 'BANK_TRANSFER', label: 'NEFT / RTGS', icon: Landmark },
                      ].map((m) => {
                        const Icon = m.icon;
                        const isSelected = depositMethod === m.id;
                        return (
                          <button
                            key={m.id}
                            type="button"
                            onClick={() => setDepositMethod(m.id as any)}
                            className={`p-3 rounded-2xl border text-left flex flex-col justify-between transition-all ${
                              isSelected
                                ? 'border-emerald-600 bg-emerald-50/60 ring-2 ring-emerald-500/20'
                                : 'border-slate-200 hover:border-slate-300 bg-white'
                            }`}
                          >
                            <Icon className={`w-5 h-5 mb-2 ${isSelected ? 'text-emerald-700' : 'text-slate-500'}`} />
                            <span className="text-xs font-bold text-slate-900">{m.label}</span>
                          </button>
                        );
                      })}
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={actionLoading || depositAmount <= 0}
                    className="w-full py-3.5 bg-emerald-600 hover:bg-emerald-700 disabled:bg-slate-300 text-white font-black text-sm rounded-2xl shadow-lg shadow-emerald-900/10 transition-colors flex items-center justify-center space-x-2 cursor-pointer"
                  >
                    {actionLoading ? (
                      <RefreshCw className="w-5 h-5 animate-spin" />
                    ) : (
                      <>
                        <Coins className="w-5 h-5" />
                        <span>Authorize Deposit & Issue ₹{depositAmount.toLocaleString('en-IN')} Tokens</span>
                      </>
                    )}
                  </button>
                </form>
              )}

              {/* TAB 3: WITHDRAW / LEFTOVER TOKEN REDEMPTION */}
              {activeTab === 'WITHDRAW' && (
                <form onSubmit={handleWithdraw} className="space-y-5">
                  <div className={`p-4 rounded-2xl border text-xs ${
                    isBuyer
                      ? 'bg-amber-50 border-amber-200 text-amber-900'
                      : 'bg-blue-50 border-blue-200 text-blue-900'
                  }`}>
                    <p className="font-bold">
                      {isBuyer
                        ? '🔄 Leftover Token Redemption & Instant Bank Refund:'
                        : '🏧 Direct Settlement to Registered Bank Account:'}
                    </p>
                    <p className="mt-1">
                      {isBuyer
                        ? 'Convert any uncommitted or leftover bidding tokens back into real rupees and withdraw directly to your bank account or UPI ID with zero exit fees.'
                        : 'Payouts from fulfilled crop sale contracts are credited to your bank account with complete e-NWR compliance and transparent audit trails.'}
                    </p>
                  </div>

                  <div>
                    <div className="flex justify-between items-center mb-1">
                      <label className="text-xs font-bold text-slate-700 uppercase tracking-wider">
                        {isBuyer ? 'Amount of Leftover Tokens to Redeem (₹)' : 'Amount to Transfer to Bank (₹)'}
                      </label>
                      <span className="text-xs font-bold text-emerald-700">
                        Max Available: ₹{available.toLocaleString('en-IN')}
                      </span>
                    </div>
                    <input
                      type="number"
                      min="100"
                      max={available}
                      value={withdrawAmount}
                      onChange={(e) => setWithdrawAmount(Number(e.target.value))}
                      required
                      className="w-full text-xl font-extrabold p-3 border border-slate-300 rounded-2xl focus:ring-2 focus:ring-rose-500 focus:outline-hidden"
                    />
                    <div className="flex gap-2 mt-2">
                      <button
                        type="button"
                        onClick={() => setWithdrawAmount(Math.floor(available / 2))}
                        className="px-3 py-1 rounded-xl text-xs font-bold bg-slate-100 text-slate-700 hover:bg-slate-200"
                      >
                        50% (₹{Math.floor(available / 2).toLocaleString('en-IN')})
                      </button>
                      <button
                        type="button"
                        onClick={() => setWithdrawAmount(available)}
                        className="px-3 py-1 rounded-xl text-xs font-bold bg-emerald-100 text-emerald-800 hover:bg-emerald-200"
                      >
                        100% Full Leftover Tokens (₹{available.toLocaleString('en-IN')})
                      </button>
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                      Receive Payout Via
                    </label>
                    <div className="grid grid-cols-2 gap-3 mb-3">
                      <button
                        type="button"
                        onClick={() => setWithdrawMethod('BANK_ACCOUNT')}
                        className={`p-3 rounded-2xl border text-left flex items-center space-x-2 transition-all ${
                          withdrawMethod === 'BANK_ACCOUNT'
                            ? 'border-emerald-600 bg-emerald-50/60 ring-2 ring-emerald-500/20'
                            : 'border-slate-200 hover:border-slate-300 bg-white'
                        }`}
                      >
                        <Landmark className="w-5 h-5 text-emerald-700" />
                        <span className="text-xs font-bold">Bank Account</span>
                      </button>
                      <button
                        type="button"
                        onClick={() => setWithdrawMethod('UPI')}
                        className={`p-3 rounded-2xl border text-left flex items-center space-x-2 transition-all ${
                          withdrawMethod === 'UPI'
                            ? 'border-emerald-600 bg-emerald-50/60 ring-2 ring-emerald-500/20'
                            : 'border-slate-200 hover:border-slate-300 bg-white'
                        }`}
                      >
                        <Smartphone className="w-5 h-5 text-emerald-700" />
                        <span className="text-xs font-bold">Instant UPI (VPA)</span>
                      </button>
                    </div>

                    {withdrawMethod === 'BANK_ACCOUNT' ? (
                      <div className="space-y-3 bg-slate-50 p-4 rounded-2xl border border-slate-200">
                        <div>
                          <label className="text-[10px] font-bold text-slate-500 uppercase">Bank Account Number</label>
                          <input
                            type="text"
                            value={bankAccount}
                            onChange={(e) => setBankAccount(e.target.value)}
                            required
                            className="w-full text-xs font-mono font-bold p-2 border border-slate-300 rounded-xl bg-white"
                          />
                        </div>
                        <div className="grid grid-cols-2 gap-2">
                          <div>
                            <label className="text-[10px] font-bold text-slate-500 uppercase">IFSC Code</label>
                            <input
                              type="text"
                              value={ifsc}
                              onChange={(e) => setIfsc(e.target.value.toUpperCase())}
                              required
                              className="w-full text-xs font-mono font-bold p-2 border border-slate-300 rounded-xl bg-white"
                            />
                          </div>
                          <div>
                            <label className="text-[10px] font-bold text-slate-500 uppercase">Account Holder Name</label>
                            <input
                              type="text"
                              value={holderName}
                              onChange={(e) => setHolderName(e.target.value)}
                              required
                              className="w-full text-xs font-bold p-2 border border-slate-300 rounded-xl bg-white"
                            />
                          </div>
                        </div>
                      </div>
                    ) : (
                      <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200">
                        <label className="text-[10px] font-bold text-slate-500 uppercase">UPI Virtual ID (VPA)</label>
                        <input
                          type="text"
                          value={upiId}
                          onChange={(e) => setUpiId(e.target.value)}
                          placeholder="yourname@okhdfcbank"
                          required
                          className="w-full text-xs font-mono font-bold p-2 border border-slate-300 rounded-xl bg-white"
                        />
                      </div>
                    )}
                  </div>

                  <button
                    type="submit"
                    disabled={actionLoading || withdrawAmount <= 0 || withdrawAmount > available}
                    className="w-full py-3.5 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-300 text-white font-black text-sm rounded-2xl shadow-lg transition-colors flex items-center justify-center space-x-2 cursor-pointer"
                  >
                    {actionLoading ? (
                      <RefreshCw className="w-5 h-5 animate-spin" />
                    ) : (
                      <>
                        <ArrowUpRight className="w-5 h-5 text-emerald-400" />
                        <span>
                          {isBuyer
                            ? `Redeem ₹${withdrawAmount.toLocaleString('en-IN')} Tokens Back to Bank`
                            : `Transfer ₹${withdrawAmount.toLocaleString('en-IN')} to Bank Account`}
                        </span>
                      </>
                    )}
                  </button>
                </form>
              )}

              {/* TAB 4: UNIQUE FEATURE: e-NWR WAREHOUSE PLEDGE ADVANCE (FARMER ONLY) */}
              {activeTab === 'ENWR_ADVANCE' && isFarmer && (
                <form onSubmit={handleENWRPledge} className="space-y-5">
                  <div className="p-4 bg-gradient-to-br from-amber-50 to-emerald-50 rounded-2xl border border-amber-200 text-xs">
                    <div className="flex items-center space-x-2">
                      <Sparkles className="w-5 h-5 text-amber-600 flex-shrink-0" />
                      <p className="font-bold text-amber-950">
                        Zero Distress Selling: Instant 70% e-NWR Liquidity Advance
                      </p>
                    </div>
                    <p className="text-slate-600 mt-1.5">
                      Don't sell your harvest at market bottom! Store your grains in a WDRA-accredited SAM warehouse, get an electronic Negotiable Warehouse Receipt (e-NWR), and unlock instant liquidity tokens to fund your next cropping season.
                    </p>
                  </div>

                  {bookings.length === 0 ? (
                    <div className="p-6 bg-slate-50 border border-slate-200 rounded-2xl text-center space-y-2">
                      <Warehouse className="w-8 h-8 text-slate-400 mx-auto" />
                      <p className="text-xs font-bold text-slate-700">No Approved Warehouse Storage Found</p>
                      <p className="text-[11px] text-slate-500">
                        Book space in a SAM warehouse first. Once the operator verifies your crop deposit, your e-NWR pledge advance will appear here.
                      </p>
                    </div>
                  ) : (
                    <>
                      <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                          Select Stored Crop Booking (e-NWR Deposit)
                        </label>
                        <div className="space-y-2">
                          {bookings.map((b) => (
                            <div
                              key={b.id}
                              onClick={() => setSelectedBookingId(b.id.toString())}
                              className={`p-3.5 rounded-2xl border cursor-pointer transition-all ${
                                selectedBookingId === b.id.toString()
                                  ? 'border-emerald-600 bg-emerald-50/70 ring-2 ring-emerald-500/20'
                                  : 'border-slate-200 bg-white hover:border-slate-300'
                              }`}
                            >
                              <div className="flex justify-between items-center text-xs">
                                <div>
                                  <span className="font-bold text-slate-900 block">
                                    {b.crop} • {b.quantityQuintals} Quintals
                                  </span>
                                  <span className="text-[10px] text-slate-500">
                                    Warehouse: {b.warehouseName} ({b.warehouseDistrict || 'Accredited Hub'}) • e-NWR Verified
                                  </span>
                                </div>
                                <div className="text-right">
                                  <span className="px-2 py-0.5 rounded-md bg-emerald-100 text-emerald-800 font-bold text-[10px]">
                                    70% Advance Eligible
                                  </span>
                                  <span className="block text-xs font-bold text-slate-800 mt-1">
                                    Max ~₹{(b.quantityQuintals * 2200 * 0.7).toLocaleString('en-IN')}
                                  </span>
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>

                      <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                          Requested Liquidity Advance Amount (₹)
                        </label>
                        <input
                          type="number"
                          min="1000"
                          step="1000"
                          value={advanceAmount}
                          onChange={(e) => setAdvanceAmount(Number(e.target.value))}
                          required
                          className="w-full text-xl font-extrabold p-3 border border-slate-300 rounded-2xl focus:ring-2 focus:ring-emerald-500"
                        />
                      </div>

                      <button
                        type="submit"
                        disabled={actionLoading || !selectedBookingId || advanceAmount <= 0}
                        className="w-full py-3.5 bg-emerald-700 hover:bg-emerald-800 disabled:bg-slate-300 text-white font-black text-sm rounded-2xl shadow-lg transition-colors flex items-center justify-center space-x-2 cursor-pointer"
                      >
                        {actionLoading ? (
                          <RefreshCw className="w-5 h-5 animate-spin" />
                        ) : (
                          <>
                            <Sparkles className="w-5 h-5 text-amber-300" />
                            <span>Issue e-NWR Token & Credit ₹{advanceAmount.toLocaleString('en-IN')} Advance</span>
                          </>
                        )}
                      </button>
                    </>
                  )}
                </form>
              )}

              {/* TAB 5: PASSBOOK */}
              {activeTab === 'PASSBOOK' && (
                <div className="space-y-4">
                  <div className="flex justify-between items-center">
                    <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700">
                      Audit Statement ({transactions.length} Records)
                    </h3>
                    <button
                      onClick={loadWalletData}
                      className="p-1.5 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-100 text-xs flex items-center space-x-1"
                    >
                      <RefreshCw className="w-3.5 h-3.5" />
                      <span>Refresh</span>
                    </button>
                  </div>

                  {transactions.length === 0 ? (
                    <div className="text-center py-10 text-slate-400 text-xs">
                      No passbook ledger transactions found.
                    </div>
                  ) : (
                    <div className="space-y-2 max-h-96 overflow-y-auto pr-1">
                      {transactions.map((tx) => {
                        const isCredit =
                          tx.type === 'DEPOSIT' ||
                          tx.type === 'ESCROW_PAYOUT' ||
                          tx.type === 'BID_UNLOCK' ||
                          tx.type === 'ENWR_PLEDGE_ADVANCE' ||
                          tx.type === 'REFUND' ||
                          tx.type === 'DEMO_CREDIT';

                        return (
                          <div
                            key={tx.id}
                            className="p-3.5 bg-slate-50 border border-slate-200/80 rounded-2xl flex flex-col sm:flex-row sm:items-center justify-between gap-2 text-xs"
                          >
                            <div className="flex items-start space-x-3">
                              <div
                                className={`w-8 h-8 rounded-xl flex items-center justify-center font-bold flex-shrink-0 mt-0.5 ${
                                  isCredit ? 'bg-emerald-100 text-emerald-700' : 'bg-rose-100 text-rose-700'
                                }`}
                              >
                                {isCredit ? (
                                  <ArrowDownLeft className="w-4 h-4" />
                                ) : (
                                  <ArrowUpRight className="w-4 h-4" />
                                )}
                              </div>
                              <div>
                                <span className="font-bold text-slate-900 block">{tx.description}</span>
                                <div className="flex flex-wrap gap-2 text-[10px] text-slate-500 mt-0.5">
                                  <span>Ref: <strong>{tx.referenceId}</strong></span>
                                  {tx.tokenCode && (
                                    <span className="text-emerald-700 font-mono font-bold">
                                      Token: {tx.tokenCode}
                                    </span>
                                  )}
                                  <span>
                                    {new Date(tx.createdAt).toLocaleDateString('en-IN', {
                                      month: 'short',
                                      day: 'numeric',
                                      hour: '2-digit',
                                      minute: '2-digit',
                                    })}
                                  </span>
                                </div>
                              </div>
                            </div>

                            <div className="text-right flex-shrink-0">
                              <span
                                className={`text-sm font-black block ${
                                  isCredit ? 'text-emerald-700' : 'text-slate-900'
                                }`}
                              >
                                {isCredit ? '+' : '-'}₹{tx.amount.toLocaleString('en-IN')}
                              </span>
                              <span className="text-[10px] text-slate-400">
                                Bal: ₹{tx.balanceAfter.toLocaleString('en-IN')}
                              </span>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};
