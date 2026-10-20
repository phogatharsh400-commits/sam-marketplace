import React, { useState, useEffect } from 'react';
import {
  X,
  Bell,
  Plus,
  Trash2,
  CheckCircle,
  AlertTriangle,
  ToggleLeft,
  ToggleRight,
  TrendingUp,
  MapPin,
  IndianRupee,
  Smartphone,
  ShieldAlert
} from 'lucide-react';
import { priceAlertApi } from '../../api';
import { PriceAlertDto, PriceAlertCreateDto, CropType } from '../../types';

interface PriceAlertModalProps {
  isOpen: boolean;
  onClose: () => void;
  defaultCrop?: string;
  defaultDistrict?: string;
  defaultTargetPrice?: number;
}

const CROP_OPTIONS: CropType[] = [
  'WHEAT',
  'PADDY',
  'MUSTARD',
];

const DISTRICTS = [
  'Karnal',
  'Hisar',
  'Sirsa',
  'Kurukshetra',
  'Ambala',
  'Fatehabad',
  'Jind',
  'Kaithal',
  'Panipat',
  'Sonipat',
  'Rohtak',
  'Bhiwani',
  'Rewari',
  'Yamunanagar',
];

export const PriceAlertModal: React.FC<PriceAlertModalProps> = ({
  isOpen,
  onClose,
  defaultCrop = 'WHEAT',
  defaultDistrict = 'Karnal',
  defaultTargetPrice = 2450,
}) => {
  const [tab, setTab] = useState<'create' | 'list'>('create');
  const [alerts, setAlerts] = useState<PriceAlertDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [formData, setFormData] = useState<PriceAlertCreateDto>({
    crop: defaultCrop,
    district: defaultDistrict,
    targetPrice: defaultTargetPrice,
    condition: 'EQUAL_OR_ABOVE',
    notificationChannel: 'ALL',
    notes: '',
  });

  useEffect(() => {
    if (isOpen) {
      setFormData({
        crop: defaultCrop || 'WHEAT',
        district: defaultDistrict || 'Karnal',
        targetPrice: defaultTargetPrice || 2450,
        condition: 'EQUAL_OR_ABOVE',
        notificationChannel: 'ALL',
        notes: '',
      });
      loadAlerts();
    }
  }, [isOpen, defaultCrop, defaultDistrict, defaultTargetPrice]);

  const loadAlerts = async () => {
    setLoading(true);
    try {
      const res = await priceAlertApi.getMyAlerts();
      if (res.data) {
        setAlerts(res.data);
      }
    } catch (err: any) {
      console.error('Failed to load price alerts:', err);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    setSuccessMessage(null);

    try {
      await priceAlertApi.createAlert(formData);
      setSuccessMessage(`Target price alert set for ₹${formData.targetPrice}/qtl on ${formData.crop}!`);
      await loadAlerts();
      setTimeout(() => {
        setTab('list');
        setSuccessMessage(null);
      }, 1200);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create price alert.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggle = async (id: string) => {
    try {
      await priceAlertApi.toggleAlert(id);
      loadAlerts();
    } catch (err: any) {
      console.error('Failed to toggle alert', err);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await priceAlertApi.deleteAlert(id);
      setAlerts(prev => prev.filter(a => a.id !== id));
    } catch (err: any) {
      console.error('Failed to delete alert', err);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-950/70 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl max-w-xl w-full shadow-2xl border border-slate-200 overflow-hidden animate-in fade-in zoom-in duration-200">
        {/* Top Header */}
        <div className="bg-slate-900 text-white p-5 flex items-center justify-between border-b border-slate-800">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-amber-500/20 text-amber-400 flex items-center justify-center border border-amber-500/30">
              <Bell className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-lg">Target-Price Alerts</h3>
              <p className="text-xs text-slate-400">Get notified when mandi or buyer bids hit your target price</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Selection */}
        <div className="flex border-b border-slate-200 bg-slate-50 px-6 pt-3">
          <button
            onClick={() => setTab('create')}
            className={`pb-3 px-4 text-xs font-bold border-b-2 transition-colors flex items-center space-x-1.5 ${
              tab === 'create'
                ? 'border-amber-600 text-amber-800'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <Plus className="w-4 h-4" />
            <span>Create New Alert</span>
          </button>
          <button
            onClick={() => setTab('list')}
            className={`pb-3 px-4 text-xs font-bold border-b-2 transition-colors flex items-center space-x-1.5 ${
              tab === 'list'
                ? 'border-amber-600 text-amber-800'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <Bell className="w-4 h-4" />
            <span>My Active Alerts ({alerts.length})</span>
          </button>
        </div>

        <div className="p-6 max-h-[75vh] overflow-y-auto">
          {error && (
            <div className="mb-4 p-3 bg-rose-50 border border-rose-200 text-rose-800 rounded-xl text-xs flex items-center space-x-2">
              <AlertTriangle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {successMessage && (
            <div className="mb-4 p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-xl text-xs flex items-center space-x-2">
              <CheckCircle className="w-4 h-4 shrink-0" />
              <span>{successMessage}</span>
            </div>
          )}

          {tab === 'create' ? (
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Crop Commodity</label>
                  <select
                    value={formData.crop}
                    onChange={(e) => setFormData({ ...formData, crop: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-hidden focus:ring-2 focus:ring-amber-500 bg-white"
                  >
                    {CROP_OPTIONS.map((c) => (
                      <option key={c} value={c}>
                        {c}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">District / Mandi Zone</label>
                  <select
                    value={formData.district}
                    onChange={(e) => setFormData({ ...formData, district: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-hidden focus:ring-2 focus:ring-amber-500 bg-white"
                  >
                    {DISTRICTS.map((d) => (
                      <option key={d} value={d}>
                        {d}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Target Price (₹ / Quintal)</label>
                  <div className="relative">
                    <span className="absolute left-3 top-2.5 text-slate-400 text-xs font-bold">₹</span>
                    <input
                      type="number"
                      required
                      min={100}
                      step={10}
                      value={formData.targetPrice}
                      onChange={(e) => setFormData({ ...formData, targetPrice: parseFloat(e.target.value) || 0 })}
                      className="w-full pl-7 pr-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-hidden focus:ring-2 focus:ring-amber-500 font-bold text-slate-800"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Trigger Condition</label>
                  <select
                    value={formData.condition}
                    onChange={(e) => setFormData({ ...formData, condition: e.target.value as any })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-hidden focus:ring-2 focus:ring-amber-500 bg-white font-medium"
                  >
                    <option value="EQUAL_OR_ABOVE">Price ≥ Target (Higher/Equal)</option>
                    <option value="BELOW">Price &lt; Target (Dip Buying)</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Notification Channel</label>
                <div className="grid grid-cols-3 gap-2">
                  {[
                    { id: 'ALL', label: 'In-App + SMS' },
                    { id: 'IN_APP', label: 'In-App Bell' },
                    { id: 'SMS', label: 'Mobile SMS' },
                  ].map((ch) => (
                    <button
                      key={ch.id}
                      type="button"
                      onClick={() => setFormData({ ...formData, notificationChannel: ch.id as any })}
                      className={`py-2 px-3 rounded-xl border text-xs font-bold text-center transition-colors ${
                        formData.notificationChannel === ch.id
                          ? 'bg-amber-50 border-amber-500 text-amber-800'
                          : 'bg-white border-slate-200 text-slate-600 hover:bg-slate-50'
                      }`}
                    >
                      {ch.label}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Personal Note / Goal (Optional)</label>
                <input
                  type="text"
                  placeholder="e.g., Sell 50 quintals when wheat crosses ₹2,500"
                  value={formData.notes || ''}
                  onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-hidden focus:ring-2 focus:ring-amber-500"
                />
              </div>

              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 text-[11px] text-slate-500 leading-relaxed">
                💡 <span className="font-semibold text-slate-700">How it works:</span> Our automated scanner monitors APMC Mandi daily arrivals and active buyer bids. You will immediately receive a notification when the market touches your threshold!
              </div>

              <button
                type="submit"
                disabled={submitting}
                className="w-full py-3 bg-amber-600 hover:bg-amber-700 text-white rounded-xl text-xs font-bold flex items-center justify-center space-x-2 transition-colors shadow-md shadow-amber-200 disabled:opacity-50"
              >
                <Bell className="w-4 h-4" />
                <span>{submitting ? 'Setting Alert...' : 'Create Target Alert'}</span>
              </button>
            </form>
          ) : (
            <div className="space-y-3">
              {loading ? (
                <div className="py-8 text-center text-xs text-slate-500">Loading active alerts...</div>
              ) : alerts.length === 0 ? (
                <div className="py-12 text-center space-y-2">
                  <Bell className="w-8 h-8 text-slate-300 mx-auto" />
                  <p className="text-xs font-bold text-slate-600">No active price alerts set</p>
                  <p className="text-[11px] text-slate-400">Set an alert to receive notifications on market peaks.</p>
                  <button
                    onClick={() => setTab('create')}
                    className="mt-2 px-3 py-1.5 bg-amber-600 text-white text-xs font-bold rounded-lg"
                  >
                    + Create Your First Alert
                  </button>
                </div>
              ) : (
                alerts.map((alert) => (
                  <div
                    key={alert.id}
                    className={`p-4 rounded-2xl border transition-all ${
                      alert.isTriggered
                        ? 'bg-emerald-50/70 border-emerald-300'
                        : alert.isActive
                        ? 'bg-white border-slate-200 shadow-xs'
                        : 'bg-slate-50 border-slate-200 opacity-60'
                    }`}
                  >
                    <div className="flex items-start justify-between">
                      <div className="space-y-1">
                        <div className="flex items-center space-x-2">
                          <span className="font-bold text-slate-900 text-sm">{alert.crop}</span>
                          <span className="text-xs text-slate-500">• {alert.district}</span>
                          {alert.isTriggered ? (
                            <span className="px-2 py-0.5 rounded-full text-[10px] font-extrabold bg-emerald-100 text-emerald-800 border border-emerald-300">
                              TRIGGERED
                            </span>
                          ) : alert.isActive ? (
                            <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-800 border border-amber-300">
                              ACTIVE
                            </span>
                          ) : (
                            <span className="px-2 py-0.5 rounded-full text-[10px] font-medium bg-slate-200 text-slate-600">
                              PAUSED
                            </span>
                          )}
                        </div>

                        <div className="text-xs font-extrabold text-slate-800">
                          Target: {alert.condition === 'EQUAL_OR_ABOVE' ? '≥' : '<'} ₹{alert.targetPrice.toLocaleString('en-IN')} / qtl
                        </div>

                        {alert.notes && <p className="text-[11px] text-slate-500 italic">"{alert.notes}"</p>}
                      </div>

                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleToggle(alert.id)}
                          title={alert.isActive ? 'Pause Alert' : 'Resume Alert'}
                          className="text-slate-400 hover:text-slate-600 p-1"
                        >
                          {alert.isActive ? (
                            <ToggleRight className="w-6 h-6 text-amber-600" />
                          ) : (
                            <ToggleLeft className="w-6 h-6 text-slate-400" />
                          )}
                        </button>
                        <button
                          onClick={() => handleDelete(alert.id)}
                          title="Delete Alert"
                          className="text-slate-400 hover:text-rose-600 p-1"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
