import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { PlusCircle, Sprout, ShieldCheck, AlertCircle, ArrowLeft, Image as ImageIcon } from 'lucide-react';
import { lotApi } from '../../api';

export const CreateLotPage: React.FC = () => {
  const navigate = useNavigate();

  const [crop, setCrop] = useState('Wheat');
  const [variety, setVariety] = useState('Sharbati');
  const [quantity, setQuantity] = useState<number>(100);
  const [unit, setUnit] = useState('QUINTAL');
  const [grade, setGrade] = useState('A');
  const [moisturePercentage, setMoisturePercentage] = useState<number>(11.5);
  const [state, setState] = useState('Haryana');
  const [district, setDistrict] = useState('Karnal');
  const [village, setVillage] = useState('Taraori');
  const [pickupLocation, setPickupLocation] = useState('Farm Gate, Village Taraori, District Karnal');
  const [minPricePerQuintal, setMinPricePerQuintal] = useState<number>(2450);
  const [expectedDispatchDate, setExpectedDispatchDate] = useState(
    new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  );
  const [auctionClosingDate, setAuctionClosingDate] = useState(
    new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  );
  const [storageRequirement, setStorageRequirement] = useState('DRY_STORAGE');
  const [description, setDescription] = useState('Premium newly harvested crop, dry cleaned and packed in 50kg standard gunny bags.');
  const [imageUrl, setImageUrl] = useState('https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=600');

  // New Quality and Pooling features
  const [poolingEnabled, setPoolingEnabled] = useState(false);
  const [minOrderQuantity, setMinOrderQuantity] = useState<number>(50);
  const [foreignMatterPercent, setForeignMatterPercent] = useState<number>(0.8);
  const [brokenGrainsPercent, setBrokenGrainsPercent] = useState<number>(1.2);
  const [sampleApproved, setSampleApproved] = useState(true);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const districts = ['Karnal', 'Hisar', 'Ambala', 'Rohtak', 'Sirsa', 'Panipat'];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await lotApi.createLot({
        crop,
        variety,
        quantity,
        unit,
        grade,
        moisturePercentage,
        state,
        district,
        village,
        pickupLocation,
        minPricePerQuintal,
        expectedDispatchDate: `${expectedDispatchDate}T10:00:00`,
        auctionClosingDate: `${auctionClosingDate}T18:00:00`,
        storageRequirement,
        description,
        poolingEnabled,
        minOrderQuantity: poolingEnabled ? minOrderQuantity : quantity,
        foreignMatterPercent,
        brokenGrainsPercent,
        sampleApproved,
        imageUrls: imageUrl ? [imageUrl] : [],
      });

      navigate('/farmer/lots');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to list produce lot.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
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
            List New Produce Lot
          </h1>
          <p className="text-xs sm:text-sm text-slate-500">
            Publish harvest lot for direct buyer bidding across Haryana
          </p>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs flex items-center space-x-2">
          <AlertCircle className="w-4 h-4 flex-shrink-0 text-rose-600" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-6">
        {/* Crop specifications */}
        <div>
          <h3 className="text-sm font-bold uppercase tracking-wider text-emerald-800 mb-3">
            1. Crop & Quality Parameters
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Crop Type</label>
              <select
                value={crop}
                onChange={(e) => setCrop(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
              >
                <option value="Wheat">Wheat (गेहूँ)</option>
                <option value="Paddy">Paddy / Basmati (धान / बासमती)</option>
                <option value="Mustard">Mustard (सरसों)</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Crop Variety</label>
              <input
                type="text"
                required
                value={variety}
                onChange={(e) => setVariety(e.target.value)}
                placeholder="e.g. Sharbati, PB-1121, Pusa 44"
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Quality Grade</label>
              <select
                value={grade}
                onChange={(e) => setGrade(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
              >
                <option value="A+">Grade A+ (Premium Export)</option>
                <option value="A">Grade A (FAQ Standard)</option>
                <option value="B">Grade B (Commercial)</option>
                <option value="C">Grade C (Feed / Industrial)</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mt-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Quantity</label>
              <input
                type="number"
                min="1"
                required
                value={quantity}
                onChange={(e) => setQuantity(parseFloat(e.target.value) || 0)}
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Unit</label>
              <select
                value={unit}
                onChange={(e) => setUnit(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
              >
                <option value="QUINTAL">Quintal (100 kg)</option>
                <option value="METRIC_TON">Metric Ton (1000 kg)</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Moisture %</label>
              <input
                type="number"
                step="0.1"
                min="0"
                max="30"
                required
                value={moisturePercentage}
                onChange={(e) => setMoisturePercentage(parseFloat(e.target.value) || 0)}
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-medium"
              />
            </div>
          </div>

          {/* Physical Sample Assaying Parameters */}
          <div className="mt-4 p-4 bg-emerald-50/60 border border-emerald-200 rounded-2xl space-y-3">
            <h4 className="text-xs font-bold text-emerald-900 uppercase tracking-wider flex items-center space-x-1.5">
              <ShieldCheck className="w-4 h-4 text-emerald-600" />
              <span>Physical Sample Assaying Parameters (Pre-Dispatch QC)</span>
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
              <div>
                <label className="block text-[11px] font-semibold text-slate-700 mb-1">
                  Foreign Matter / Admixture %
                </label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  max="10"
                  value={foreignMatterPercent}
                  onChange={(e) => setForeignMatterPercent(parseFloat(e.target.value) || 0)}
                  className="w-full p-2 rounded-xl bg-white border border-slate-300 text-xs font-bold"
                />
                <span className="text-[10px] text-slate-500">Max allowed for Grade A: 2.0%</span>
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-700 mb-1">
                  Broken / Discolored Grains %
                </label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  max="15"
                  value={brokenGrainsPercent}
                  onChange={(e) => setBrokenGrainsPercent(parseFloat(e.target.value) || 0)}
                  className="w-full p-2 rounded-xl bg-white border border-slate-300 text-xs font-bold"
                />
                <span className="text-[10px] text-slate-500">Max allowed for Grade A: 3.0%</span>
              </div>
            </div>
          </div>

          {/* Smallholder FPO Aggregation Pooling Toggle */}
          <div className="mt-4 p-4 bg-blue-50/70 border border-blue-200 rounded-2xl space-y-2">
            <div className="flex items-center justify-between">
              <div>
                <span className="text-xs font-bold text-blue-900 block">
                  Enable FPO Batch Pooling (Smallholder Aggregation)
                </span>
                <span className="text-[11px] text-blue-700 block">
                  Allows small farmers (&lt;50 Quintals) to combine lots in the same village into a single wholesale truckload for bulk buyers.
                </span>
              </div>
              <input
                type="checkbox"
                checked={poolingEnabled}
                onChange={(e) => setPoolingEnabled(e.target.checked)}
                className="w-5 h-5 text-blue-600 rounded-lg focus:ring-blue-500 cursor-pointer"
              />
            </div>

            {poolingEnabled && (
              <div className="pt-2 border-t border-blue-200 text-xs flex items-center space-x-3">
                <span className="font-semibold text-blue-800">Minimum Order Split:</span>
                <input
                  type="number"
                  min="5"
                  value={minOrderQuantity}
                  onChange={(e) => setMinOrderQuantity(parseFloat(e.target.value) || 10)}
                  className="w-24 p-1.5 rounded-lg bg-white border border-blue-300 text-xs font-bold text-blue-900"
                />
                <span className="text-blue-700">Quintals</span>
              </div>
            )}
          </div>
        </div>

        {/* Pricing & Auction */}
        <div className="pt-4 border-t border-slate-100">
          <h3 className="text-sm font-bold uppercase tracking-wider text-emerald-800 mb-3">
            2. Reserve Price & Auction Schedule
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Min Reserve Price (₹ / Quintal)
              </label>
              <input
                type="number"
                min="100"
                step="1"
                required
                value={minPricePerQuintal}
                onChange={(e) => setMinPricePerQuintal(parseFloat(e.target.value) || 0)}
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-bold text-emerald-700"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Auction Closing Date</label>
              <input
                type="date"
                required
                value={auctionClosingDate}
                onChange={(e) => setAuctionClosingDate(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Expected Dispatch Date</label>
              <input
                type="date"
                required
                value={expectedDispatchDate}
                onChange={(e) => setExpectedDispatchDate(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm"
              />
            </div>
          </div>
        </div>

        {/* Location & Warehouse */}
        <div className="pt-4 border-t border-slate-100">
          <h3 className="text-sm font-bold uppercase tracking-wider text-emerald-800 mb-3">
            3. Farm Logistics & Location
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">State</label>
              <input
                type="text"
                disabled
                value={state}
                className="w-full p-2.5 rounded-xl bg-slate-50 border border-slate-300 text-sm text-slate-600 font-medium"
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
              <label className="block text-xs font-semibold text-slate-700 mb-1">Village</label>
              <input
                type="text"
                required
                value={village}
                onChange={(e) => setVillage(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-300 text-sm"
              />
            </div>
          </div>

          <div className="mt-4">
            <label className="block text-xs font-semibold text-slate-700 mb-1">Detailed Pickup Location Address</label>
            <input
              type="text"
              required
              value={pickupLocation}
              onChange={(e) => setPickupLocation(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm"
            />
          </div>
        </div>

        {/* Media & Remarks */}
        <div className="pt-4 border-t border-slate-100 space-y-4">
          <h3 className="text-sm font-bold uppercase tracking-wider text-emerald-800">
            4. Description & Photos
          </h3>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Produce Description</label>
            <textarea
              rows={3}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full p-3 rounded-xl border border-slate-300 text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Sample Image URL</label>
            <input
              type="url"
              value={imageUrl}
              onChange={(e) => setImageUrl(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-slate-300 text-sm text-slate-600"
            />
          </div>
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
            {loading ? <span>Listing Lot...</span> : <span>Publish Produce Lot</span>}
          </button>
        </div>
      </form>
    </div>
  );
};
