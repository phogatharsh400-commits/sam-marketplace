import React, { useState, useEffect } from 'react';
import { Warehouse, CheckCircle2, XCircle, Calendar, Scale, AlertCircle } from 'lucide-react';
import { warehouseApi } from '../../api';
import { WarehouseBookingResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';

export const OperatorBookingsPage: React.FC = () => {
  const [bookings, setBookings] = useState<WarehouseBookingResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [processingId, setProcessingId] = useState<string | number | null>(null);

  const fetchBookings = async () => {
    setLoading(true);
    try {
      const res = await warehouseApi.getOperatorBookings();
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

  const handleUpdateStatus = async (bookingId: string | number, status: string) => {
    setProcessingId(bookingId);
    try {
      await warehouseApi.updateBookingStatus(bookingId, status);
      fetchBookings();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update booking status.');
    } finally {
      setProcessingId(null);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Storage Booking Requests</h1>
          <p className="text-xs text-slate-500">
            Review incoming farmer reservation requests and manage warehouse capacity
          </p>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs flex items-center space-x-2">
          <AlertCircle className="w-4 h-4 text-rose-600 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {loading ? (
        <div className="py-12 flex justify-center">
          <div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-600 border-t-transparent"></div>
        </div>
      ) : bookings.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center max-w-md mx-auto space-y-3">
          <Warehouse className="w-12 h-12 text-slate-400 mx-auto" />
          <h3 className="text-base font-bold text-slate-800">No Booking Requests</h3>
          <p className="text-xs text-slate-500">
            When farmers request storage slots for their produce lots, they will appear here.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {bookings.map((b) => (
            <div
              key={b.id}
              className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4 flex flex-col md:flex-row md:items-center justify-between gap-4"
            >
              <div className="space-y-2">
                <div className="flex items-center space-x-3">
                  <span className="font-bold text-slate-900 text-base">{b.warehouseName}</span>
                  <StatusBadge status={b.status} />
                </div>
                <div className="flex flex-wrap gap-4 text-xs text-slate-600">
                  <span>Farmer: <strong className="text-slate-800">{b.farmerName}</strong></span>
                  <span>Crop: <strong className="text-emerald-700">{b.crop}</strong></span>
                  <span>Quantity: <strong className="text-slate-800">{b.quantityQuintals} Quintals</strong></span>
                  <span>
                    Dates: <strong>{new Date(b.startDate).toLocaleDateString('en-IN')}</strong> to <strong>{new Date(b.endDate).toLocaleDateString('en-IN')}</strong>
                  </span>
                </div>
                {b.remarks && (
                  <p className="text-[11px] text-slate-500 italic">
                    Farmer Note: "{b.remarks}"
                  </p>
                )}
              </div>

              <div className="flex flex-col sm:flex-row sm:items-center gap-3 flex-shrink-0">
                <div className="text-right sm:mr-3">
                  <span className="text-[10px] text-slate-400 uppercase font-semibold block">Total Estimated Fee</span>
                  <span className="text-base font-black text-emerald-800">
                    ₹{b.totalCost?.toLocaleString('en-IN') || 'TBD'}
                  </span>
                </div>

                {b.status === 'REQUESTED' && (
                  <div className="flex items-center space-x-2">
                    <button
                      type="button"
                      disabled={processingId === b.id}
                      onClick={() => handleUpdateStatus(b.id, 'APPROVED')}
                      className="px-3.5 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs transition-colors flex items-center space-x-1 disabled:opacity-50"
                    >
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      <span>Approve</span>
                    </button>
                    <button
                      type="button"
                      disabled={processingId === b.id}
                      onClick={() => handleUpdateStatus(b.id, 'REJECTED')}
                      className="px-3.5 py-2 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 font-bold text-xs transition-colors flex items-center space-x-1 disabled:opacity-50"
                    >
                      <XCircle className="w-3.5 h-3.5" />
                      <span>Reject</span>
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
