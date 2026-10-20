import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Warehouse, PlusCircle, ArrowLeft, AlertCircle } from 'lucide-react';
import { warehouseApi } from '../../api';

export const RegisterWarehousePage: React.FC = () => {
  const navigate = useNavigate();

  const [warehouseName, setWarehouseName] = useState('');
  const [ownerName, setOwnerName] = useState('');
  const [contactNumber, setContactNumber] = useState('');
  const [state, setState] = useState('Haryana');
  const [district, setDistrict] = useState('Karnal');
  const [address, setAddress] = useState('');
  const [totalCapacityQuintals, setTotalCapacityQuintals] = useState<number>(5000);
  const [supportedCrops, setSupportedCrops] = useState<string[]>(['Wheat', 'Paddy']);
  const [storageChargePerQuintalPerMonth, setStorageChargePerQuintalPerMonth] = useState<number>(25);
  const [accreditationStatus, setAccreditationStatus] = useState('WDRA_ACCREDITED');
  const [description, setDescription] = useState('State-of-the-art climate controlled warehouse facility with automated fumigation and weighbridge.');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const districts = ['Karnal', 'Hisar', 'Ambala', 'Rohtak', 'Sirsa', 'Panipat'];
  const allCrops = ['Wheat', 'Paddy', 'Mustard'];

  const handleCropToggle = (crop: string) => {
    if (supportedCrops.includes(crop)) {
      if (supportedCrops.length > 1) {
        setSupportedCrops(supportedCrops.filter((c) => c !== crop));
      }
    } else {
      setSupportedCrops([...supportedCrops, crop]);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await warehouseApi.createWarehouse({
        warehouseName,
        ownerName,
        contactNumber,
        state,
        district,
        address,
        totalCapacityQuintals,
        supportedCrops,
        storageChargePerQuintalPerMonth,
        accreditationStatus,
        description,
      });

      navigate('/operator/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to register warehouse.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="inline-flex items-center text-xs font-semibold text-emerald-700 hover:text-emerald-800 transition-colors mb-1"
          >
            <ArrowLeft className="w-4 h-4 mr-1" />
            <span>Back</span>
          </button>
          <h1 className="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight">
            Register Storage Facility
          </h1>
          <p className="text-xs sm:text-sm text-slate-500">
            Publish available warehouse space for farmer crop reservations
          </p>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs flex items-center space-x-2">
          <AlertCircle className="w-4 h-4 text-rose-600 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Warehouse Facility Name</label>
            <input
              type="text"
              required
              value={warehouseName}
              onChange={(e) => setWarehouseName(e.target.value)}
              placeholder="e.g. Kisan Agri Logistics & Storage"
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Owner / Manager Name</label>
            <input
              type="text"
              required
              value={ownerName}
              onChange={(e) => setOwnerName(e.target.value)}
              placeholder="e.g. Baldev Singh"
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Contact Phone</label>
            <input
              type="tel"
              required
              value={contactNumber}
              onChange={(e) => setContactNumber(e.target.value)}
              placeholder="9876543210"
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">District</label>
            <select
              value={district}
              onChange={(e) => setDistrict(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
            >
              {districts.map((d) => (
                <option key={d} value={d}>{d}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Accreditation</label>
            <select
              value={accreditationStatus}
              onChange={(e) => setAccreditationStatus(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
            >
              <option value="WDRA_ACCREDITED">WDRA Accredited</option>
              <option value="STATE_REGISTERED">State Govt Registered</option>
              <option value="PRIVATE_CERTIFIED">Private Certified</option>
            </select>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Total Storage Capacity (Quintals)
            </label>
            <input
              type="number"
              min="100"
              required
              value={totalCapacityQuintals}
              onChange={(e) => setTotalCapacityQuintals(parseFloat(e.target.value) || 0)}
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-bold text-slate-900"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Monthly Storage Fee (₹ / Quintal / Month)
            </label>
            <input
              type="number"
              min="1"
              required
              value={storageChargePerQuintalPerMonth}
              onChange={(e) => setStorageChargePerQuintalPerMonth(parseFloat(e.target.value) || 0)}
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-bold text-emerald-700"
            />
          </div>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-700 mb-1">Supported Crop Commodities</label>
          <div className="flex flex-wrap gap-3">
            {allCrops.map((c) => (
              <button
                key={c}
                type="button"
                onClick={() => handleCropToggle(c)}
                className={`px-4 py-2 rounded-xl text-xs font-bold transition-all border ${
                  supportedCrops.includes(c)
                    ? 'bg-emerald-600 text-white border-emerald-600'
                    : 'bg-white text-slate-600 border-slate-300'
                }`}
              >
                {supportedCrops.includes(c) ? '✓ ' : '+ '} {c}
              </button>
            ))}
          </div>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-700 mb-1">Facility Address</label>
          <input
            type="text"
            required
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="Plot 45, GT Road Mandi Complex, Karnal, Haryana"
            className="w-full p-2.5 rounded-xl border border-slate-300 text-sm"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-700 mb-1">Description & Amenities</label>
          <textarea
            rows={3}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full p-3 rounded-xl border border-slate-300 text-sm"
          />
        </div>

        <div className="pt-4 border-t border-slate-100 flex items-center justify-end space-x-3">
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="py-2.5 px-5 rounded-xl border border-slate-300 text-slate-700 font-medium text-xs hover:bg-slate-50 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className="py-2.5 px-6 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md shadow-emerald-700/20 transition-all flex items-center space-x-2 disabled:opacity-50"
          >
            {loading ? <span>Registering...</span> : <span>Confirm & Publish Warehouse</span>}
          </button>
        </div>
      </form>
    </div>
  );
};
