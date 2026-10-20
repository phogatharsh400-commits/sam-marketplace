import React, { useState, useEffect } from 'react';
import { Warehouse as WarehouseIcon, MapPin, Phone, Scale, Award, Search, Calendar, ShieldCheck, CheckCircle2 } from 'lucide-react';
import { warehouseApi } from '../api';
import { WarehouseResponse } from '../types';
import { BookingModal } from '../components/modals/BookingModal';
import { MeritScoreBadge } from '../components/common/MeritScoreBadge';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';

export const WarehousesPage: React.FC = () => {
  const { user, isAuthenticated } = useAuth();
  const [warehouses, setWarehouses] = useState<WarehouseResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [selectedDistrict, setSelectedDistrict] = useState('');
  const [selectedCrop, setSelectedCrop] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const [selectedWarehouseForBooking, setSelectedWarehouseForBooking] = useState<WarehouseResponse | null>(null);
  const [bookingSuccess, setBookingSuccess] = useState(false);

  const districts = ['Karnal', 'Hisar', 'Ambala', 'Rohtak', 'Sirsa', 'Panipat'];
  const crops = ['Wheat', 'Paddy', 'Mustard'];

  const fetchWarehouses = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await warehouseApi.searchWarehouses({
        district: selectedDistrict || undefined,
        crop: selectedCrop || undefined,
      });
      setWarehouses(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load warehouses.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWarehouses();
  }, [selectedDistrict, selectedCrop]);

  const filteredWarehouses = warehouses.filter((wh) => {
    if (!searchQuery) return true;
    const q = searchQuery.toLowerCase();
    return (
      wh.warehouseName.toLowerCase().includes(q) ||
      wh.district.toLowerCase().includes(q) ||
      wh.address.toLowerCase().includes(q) ||
      wh.ownerName.toLowerCase().includes(q)
    );
  });

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-slate-200">
        <div>
          <div className="flex items-center space-x-2 text-emerald-600 font-bold text-xs uppercase tracking-wider">
            <WarehouseIcon className="w-4 h-4" />
            <span>Integrated Storage Network</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight mt-1">
            Haryana Agri Warehouses & Cold Storage
          </h1>
          <p className="text-slate-500 text-sm mt-0.5">
            WDRA accredited and state-registered storage facilities with real-time capacity booking
          </p>
        </div>

        {user?.role === 'FARMER' && (
          <Link
            to="/farmer/bookings"
            className="inline-flex items-center space-x-1.5 px-4 py-2.5 rounded-xl bg-emerald-50 text-emerald-800 border border-emerald-200 text-xs font-bold hover:bg-emerald-100 transition-colors"
          >
            <span>My Storage Bookings</span>
          </Link>
        )}
      </div>

      {bookingSuccess && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-2xl text-emerald-800 text-xs flex items-center space-x-2">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0" />
          <span>Storage booking request submitted! The warehouse operator will review your slot.</span>
        </div>
      )}

      {/* Filter Bar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs grid grid-cols-1 sm:grid-cols-12 gap-3">
        <div className="sm:col-span-5 relative">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search warehouse name, location, or operator..."
            className="w-full pl-9 pr-4 py-2.5 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-emerald-500 font-medium"
          />
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3.5" />
        </div>

        <div className="sm:col-span-3">
          <select
            value={selectedDistrict}
            onChange={(e) => setSelectedDistrict(e.target.value)}
            className="w-full p-2.5 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-emerald-500 font-medium text-slate-700"
          >
            <option value="">All Districts</option>
            {districts.map((d) => (
              <option key={d} value={d}>{d}</option>
            ))}
          </select>
        </div>

        <div className="sm:col-span-3">
          <select
            value={selectedCrop}
            onChange={(e) => setSelectedCrop(e.target.value)}
            className="w-full p-2.5 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-emerald-500 font-medium text-slate-700"
          >
            <option value="">All Supported Crops</option>
            {crops.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </div>

        <div className="sm:col-span-1 flex items-center justify-center">
          <button
            type="button"
            onClick={() => {
              setSelectedDistrict('');
              setSelectedCrop('');
              setSearchQuery('');
            }}
            className="w-full py-2.5 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-600 text-xs font-semibold transition-colors"
          >
            Reset
          </button>
        </div>
      </div>

      {/* Warehouse Cards Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[1, 2, 3].map((n) => (
            <div key={n} className="bg-white rounded-2xl border border-slate-200 p-6 animate-pulse space-y-4">
              <div className="h-6 bg-slate-200 rounded w-2/3"></div>
              <div className="h-4 bg-slate-100 rounded w-1/2"></div>
              <div className="h-20 bg-slate-50 rounded"></div>
            </div>
          ))}
        </div>
      ) : filteredWarehouses.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center max-w-lg mx-auto space-y-3">
          <WarehouseIcon className="w-12 h-12 text-slate-400 mx-auto" />
          <h3 className="text-lg font-bold text-slate-800">No Warehouses Found</h3>
          <p className="text-xs text-slate-500">
            No active storage facilities match your filter criteria in Haryana.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredWarehouses.map((wh) => {
            const occupancyPct = Math.round(
              ((wh.totalCapacityQuintals - wh.availableCapacityQuintals) / wh.totalCapacityQuintals) * 100
            );

            return (
              <div
                key={wh.id}
                className="bg-white rounded-2xl border border-slate-200 shadow-xs hover:shadow-lg transition-all p-6 flex flex-col justify-between space-y-4"
              >
                <div className="space-y-3">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <span className="text-[11px] font-bold uppercase tracking-wider text-emerald-600">
                        {wh.district} District
                      </span>
                      <h3 className="text-lg font-bold text-slate-900 mt-0.5">{wh.warehouseName}</h3>
                    </div>
                    <div className="flex flex-col items-end gap-1">
                      <MeritScoreBadge
                        score={95}
                        tier="PLATINUM"
                        userName={wh.ownerName || 'Warehouse Facility'}
                        size="sm"
                      />
                      {wh.accreditationStatus && (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-bold bg-blue-50 text-blue-700 border border-blue-200">
                          <Award className="w-3 h-3 mr-1 text-blue-600" />
                          {wh.accreditationStatus}
                        </span>
                      )}
                    </div>
                  </div>

                  <p className="text-xs text-slate-500 line-clamp-2">
                    {wh.description || wh.address}
                  </p>

                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-100 space-y-2">
                    <div className="flex justify-between text-xs font-semibold">
                      <span className="text-slate-600">Available Storage:</span>
                      <span className="text-emerald-700 font-bold">
                        {wh.availableCapacityQuintals.toLocaleString('en-IN')} / {wh.totalCapacityQuintals.toLocaleString('en-IN')} qtl
                      </span>
                    </div>

                    {/* Capacity Progress Bar */}
                    <div className="w-full bg-slate-200 rounded-full h-2 overflow-hidden">
                      <div
                        className={`h-full rounded-full ${
                          occupancyPct > 80 ? 'bg-amber-500' : 'bg-emerald-500'
                        }`}
                        style={{ width: `${occupancyPct}%` }}
                      ></div>
                    </div>
                  </div>

                  <div className="flex items-center justify-between text-xs text-slate-700 pt-1">
                    <div>
                      <span className="text-[10px] text-slate-400 block uppercase font-semibold">Charge</span>
                      <span className="font-extrabold text-slate-900">
                        ₹{wh.storageChargePerQuintalPerMonth}
                      </span>
                      <span className="text-[10px] text-slate-500"> / qtl / month</span>
                    </div>

                    <div className="text-right">
                      <span className="text-[10px] text-slate-400 block uppercase font-semibold">Crops</span>
                      <span className="font-semibold text-xs text-emerald-800">
                        {wh.supportedCrops.join(', ')}
                      </span>
                    </div>
                  </div>

                  <div className="pt-2 border-t border-slate-100 text-xs text-slate-500 flex items-center justify-between">
                    <span className="flex items-center">
                      <MapPin className="w-3.5 h-3.5 mr-1 text-slate-400" />
                      {wh.address}
                    </span>
                    <span className="flex items-center">
                      <Phone className="w-3.5 h-3.5 mr-1 text-slate-400" />
                      {wh.contactNumber}
                    </span>
                  </div>
                </div>

                <div className="pt-2">
                  {isAuthenticated && user?.role === 'FARMER' ? (
                    <button
                      type="button"
                      onClick={() => setSelectedWarehouseForBooking(wh)}
                      disabled={wh.availableCapacityQuintals <= 0}
                      className="w-full py-2.5 px-4 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold transition-colors flex items-center justify-center space-x-1.5 shadow-xs disabled:opacity-50"
                    >
                      <Calendar className="w-4 h-4" />
                      <span>Request Storage Booking</span>
                    </button>
                  ) : (
                    <Link
                      to="/login"
                      className="w-full py-2.5 px-4 rounded-xl bg-slate-900 hover:bg-emerald-600 text-white text-xs font-bold transition-colors flex items-center justify-center space-x-1.5 shadow-xs"
                    >
                      <span>Login to Book Slot</span>
                    </Link>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Booking Modal */}
      {selectedWarehouseForBooking && (
        <BookingModal
          warehouse={selectedWarehouseForBooking}
          isOpen={!!selectedWarehouseForBooking}
          onClose={() => setSelectedWarehouseForBooking(null)}
          onSuccess={() => {
            setBookingSuccess(true);
            setTimeout(() => setBookingSuccess(false), 5000);
            fetchWarehouses();
          }}
        />
      )}
    </div>
  );
};
