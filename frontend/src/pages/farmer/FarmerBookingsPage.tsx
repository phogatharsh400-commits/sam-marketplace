import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Warehouse, Calendar, Scale, PlusCircle, AlertCircle, CheckCircle2 } from 'lucide-react';
import { warehouseApi } from '../../api';
import { WarehouseBookingResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';

export const FarmerBookingsPage: React.FC = () => {
  const [bookings, setBookings] = useState<WarehouseBookingResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchBookings = async () => {
    setLoading(true);
    try {
      const res = await warehouseApi.getMyBookings();
      setBookings(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load bookings.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-black text-slate-900">My Warehouse Storage Bookings</h1>
          <p className="text-xs text-slate-500">
            Track stored grain lots and reservation requests across Haryana warehouses
          </p>
        </div>
        <Link
          to="/warehouses"
          className="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold shadow-xs transition-colors"
        >
          <PlusCircle className="w-4 h-4" />
          <span>Browse Warehouses & Book</span>
        </Link>
      </div>

      {loading ? (
        <div className="py-12 flex justify-center">
          <div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-600 border-t-transparent"></div>
        </div>
      ) : bookings.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center max-w-md mx-auto space-y-3">
          <Warehouse className="w-12 h-12 text-slate-400 mx-auto" />
          <h3 className="text-base font-bold text-slate-800">No Storage Bookings</h3>
          <p className="text-xs text-slate-500">
            You haven't reserved warehouse storage slots yet. Explore accredited warehouses to preserve produce quality.
          </p>
          <Link
            to="/warehouses"
            className="inline-flex items-center space-x-1.5 px-4 py-2 rounded-xl bg-emerald-600 text-white text-xs font-bold mt-2"
          >
            <span>Explore Warehouses</span>
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {bookings.map((b) => (
            <div
              key={b.id}
              className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-3 flex flex-col md:flex-row md:items-center justify-between gap-4"
            >
              <div className="space-y-2">
                <div className="flex items-center space-x-3">
                  <h3 className="font-bold text-slate-900 text-base">{b.warehouseName}</h3>
                  <StatusBadge status={b.status} />
                </div>
                <div className="flex flex-wrap gap-4 text-xs text-slate-600">
                  <span>Crop: <strong className="text-slate-800">{b.crop}</strong></span>
                  <span>Quantity: <strong className="text-slate-800">{b.quantityQuintals} Quintals</strong></span>
                  <span>
                    Duration: <strong>{new Date(b.startDate).toLocaleDateString('en-IN')}</strong> to <strong>{new Date(b.endDate).toLocaleDateString('en-IN')}</strong>
                  </span>
                </div>
                {b.remarks && (
                  <p className="text-[11px] text-slate-500 italic">
                    Remarks: "{b.remarks}"
                  </p>
                )}
              </div>

              <div className="text-right flex-shrink-0 bg-slate-50 p-3 rounded-xl border border-slate-100">
                <span className="text-[10px] text-slate-400 uppercase font-semibold block">Total Storage Fee</span>
                <span className="text-lg font-black text-emerald-800">
                  ₹{b.totalCost ? b.totalCost.toLocaleString('en-IN') : 'TBD'}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
