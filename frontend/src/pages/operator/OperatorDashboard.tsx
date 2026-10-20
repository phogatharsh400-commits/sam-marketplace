import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Warehouse, PlusCircle, Scale, Calendar, CheckCircle2, XCircle, Clock, MapPin, Phone } from 'lucide-react';
import { warehouseApi } from '../../api';
import { WarehouseResponse, WarehouseBookingResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useAuth } from '../../context/AuthContext';

export const OperatorDashboard: React.FC = () => {
  const { user } = useAuth();
  const [warehouses, setWarehouses] = useState<WarehouseResponse[]>([]);
  const [bookings, setBookings] = useState<WarehouseBookingResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchOperatorData = async () => {
    setLoading(true);
    try {
      const [whRes, bookRes] = await Promise.allSettled([
        warehouseApi.getMyWarehouses(),
        warehouseApi.getOperatorBookings(),
      ]);

      if (whRes.status === 'fulfilled') setWarehouses(whRes.value.data);
      if (bookRes.status === 'fulfilled') setBookings(bookRes.value.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOperatorData();
  }, []);

  const totalCapacity = warehouses.reduce((sum, w) => sum + w.totalCapacityQuintals, 0);
  const totalAvailable = warehouses.reduce((sum, w) => sum + w.availableCapacityQuintals, 0);
  const pendingRequestsCount = bookings.filter((b) => b.status === 'REQUESTED').length;

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-amber-950 via-slate-900 to-emerald-950 text-white p-6 sm:p-8 rounded-3xl border border-amber-900 shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="space-y-1.5">
          <div className="inline-flex items-center space-x-1 px-3 py-1 rounded-full bg-amber-500/20 text-amber-300 text-xs font-semibold">
            <Warehouse className="w-3.5 h-3.5 mr-1" />
            <span>Warehouse Operator Dashboard</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black">
            Welcome, {user?.name}!
          </h1>
          <p className="text-xs sm:text-sm text-amber-200/80">
            Account: <strong className="text-white">{user?.accountStatus}</strong> • Manage storage capacities and booking approvals.
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <Link
            to="/operator/register-warehouse"
            className="px-5 py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-md transition-all flex items-center space-x-2"
          >
            <PlusCircle className="w-4 h-4" />
            <span>Register New Warehouse</span>
          </Link>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <span className="text-xs font-semibold text-slate-500 uppercase">Registered Facilities</span>
          <p className="text-2xl font-black text-slate-900">{warehouses.length}</p>
          <p className="text-[11px] text-slate-500">Warehouses in Haryana</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <span className="text-xs font-semibold text-slate-500 uppercase">Total Capacity</span>
          <p className="text-2xl font-black text-slate-900">{totalCapacity.toLocaleString('en-IN')} qtl</p>
          <p className="text-[11px] text-slate-500">Gross storage volume</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <span className="text-xs font-semibold text-slate-500 uppercase">Available Space</span>
          <p className="text-2xl font-black text-emerald-700">{totalAvailable.toLocaleString('en-IN')} qtl</p>
          <p className="text-[11px] text-emerald-600">Ready for incoming harvest</p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-2">
          <span className="text-xs font-semibold text-slate-500 uppercase">Pending Requests</span>
          <p className="text-2xl font-black text-amber-700">{pendingRequestsCount}</p>
          <p className="text-[11px] text-slate-500">Awaiting your approval</p>
        </div>
      </div>

      {/* Warehouses list & pending bookings */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        <div className="lg:col-span-7 space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="font-bold text-slate-900 text-base">My Warehouses</h3>
              <Link to="/operator/register-warehouse" className="text-xs font-bold text-emerald-700 hover:text-emerald-800">
                + Add Facility
              </Link>
            </div>

            {warehouses.length === 0 ? (
              <div className="text-center py-8 text-slate-500 text-xs">
                You haven't registered any warehouses yet. Click "Register New Warehouse" to list storage capacity.
              </div>
            ) : (
              <div className="space-y-4">
                {warehouses.map((wh) => (
                  <div key={wh.id} className="p-4 rounded-xl bg-slate-50 border border-slate-200 space-y-2 text-xs">
                    <div className="flex justify-between items-center">
                      <span className="font-bold text-slate-900 text-sm">{wh.warehouseName}</span>
                      <span className="text-emerald-700 font-bold bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                        ₹{wh.storageChargePerQuintalPerMonth}/qtl/mo
                      </span>
                    </div>
                    <div className="flex flex-wrap gap-4 text-slate-600">
                      <span>District: <strong>{wh.district}</strong></span>
                      <span>Available: <strong className="text-emerald-700">{wh.availableCapacityQuintals}</strong> / {wh.totalCapacityQuintals} qtl</span>
                      <span>Crops: <strong>{wh.supportedCrops.join(', ')}</strong></span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="lg:col-span-5 space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="font-bold text-slate-900 text-base">Incoming Storage Requests</h3>
              <Link to="/operator/bookings" className="text-xs font-bold text-emerald-700 hover:text-emerald-800">
                Manage All ({bookings.length})
              </Link>
            </div>

            {bookings.length === 0 ? (
              <div className="text-center py-8 text-slate-500 text-xs">
                No storage requests yet.
              </div>
            ) : (
              <div className="space-y-3">
                {bookings.slice(0, 4).map((b) => (
                  <div key={b.id} className="p-3.5 rounded-xl border border-slate-200 bg-slate-50/50 space-y-1.5 text-xs">
                    <div className="flex justify-between items-center">
                      <span className="font-bold text-slate-900">Farmer: {b.farmerName}</span>
                      <StatusBadge status={b.status} />
                    </div>
                    <div className="flex justify-between items-center text-slate-600">
                      <span>{b.quantityQuintals} qtl of {b.crop}</span>
                      <span className="font-bold text-emerald-800">₹{b.totalCost?.toLocaleString('en-IN') || 'TBD'}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
