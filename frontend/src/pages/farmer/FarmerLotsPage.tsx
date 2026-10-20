import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Sprout, PlusCircle, ArrowRight, Gavel, Droplets, MapPin, Calendar, Trash2 } from 'lucide-react';
import { lotApi } from '../../api';
import { LotResponse } from '../../types';
import { StatusBadge } from '../../components/common/StatusBadge';

export const FarmerLotsPage: React.FC = () => {
  const [lots, setLots] = useState<LotResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchLots = async () => {
    setLoading(true);
    try {
      const res = await lotApi.getMyLots();
      setLots(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load your lots.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLots();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-black text-slate-900">My Listed Produce Lots</h1>
          <p className="text-xs text-slate-500">
            Manage your harvest listings, review live bids, and accept contracts
          </p>
        </div>
        <Link
          to="/farmer/create-lot"
          className="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold shadow-xs transition-colors"
        >
          <PlusCircle className="w-4 h-4" />
          <span>List New Produce Lot</span>
        </Link>
      </div>

      {loading ? (
        <div className="py-12 flex justify-center">
          <div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-600 border-t-transparent"></div>
        </div>
      ) : lots.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center max-w-md mx-auto space-y-3">
          <Sprout className="w-12 h-12 text-slate-400 mx-auto" />
          <h3 className="text-base font-bold text-slate-800">No Produce Lots Listed</h3>
          <p className="text-xs text-slate-500">
            Start listing your wheat, paddy, or mustard harvest lots to receive bids from verified buyers.
          </p>
          <Link
            to="/farmer/create-lot"
            className="inline-flex items-center space-x-1.5 px-4 py-2 rounded-xl bg-emerald-600 text-white text-xs font-bold mt-2"
          >
            <PlusCircle className="w-4 h-4" />
            <span>Create First Lot</span>
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {lots.map((lot) => {
            const imageUrl =
              lot.imageUrls && lot.imageUrls.length > 0
                ? lot.imageUrls[0]
                : 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=600';

            return (
              <div
                key={lot.id}
                className="bg-white rounded-2xl border border-slate-200 shadow-xs hover:shadow-md transition-all overflow-hidden flex flex-col justify-between"
              >
                <div>
                  <div className="relative h-40 bg-slate-100">
                    <img src={imageUrl} alt={lot.crop} className="w-full h-full object-cover" />
                    <div className="absolute top-3 left-3">
                      <StatusBadge status={lot.status} />
                    </div>
                    <div className="absolute top-3 right-3 bg-slate-900/80 backdrop-blur-md text-white px-2 py-0.5 rounded text-[11px] font-bold">
                      Grade {lot.grade}
                    </div>
                  </div>

                  <div className="p-5 space-y-3">
                    <div>
                      <span className="text-[11px] font-bold uppercase tracking-wider text-emerald-600">
                        {lot.crop}
                      </span>
                      <h3 className="text-base font-bold text-slate-900">{lot.variety}</h3>
                    </div>

                    <div className="grid grid-cols-2 gap-2 bg-slate-50 p-2.5 rounded-xl border border-slate-100 text-xs">
                      <div>
                        <span className="text-[10px] text-slate-400 block">Quantity</span>
                        <span className="font-bold text-slate-800">{lot.quantity} {lot.unit}</span>
                      </div>
                      <div>
                        <span className="text-[10px] text-slate-400 block">Reserve Price</span>
                        <span className="font-extrabold text-emerald-700">₹{lot.minPricePerQuintal}/qtl</span>
                      </div>
                    </div>

                    <div className="text-xs text-slate-500 flex items-center justify-between pt-1">
                      <span className="flex items-center">
                        <Gavel className="w-3.5 h-3.5 mr-1 text-slate-400" />
                        <span>{lot.totalBids || 0} active bids</span>
                      </span>
                      <span>Closes {new Date(lot.auctionClosingDate).toLocaleDateString('en-IN')}</span>
                    </div>
                  </div>
                </div>

                <div className="p-4 bg-slate-50 border-t border-slate-100 flex items-center justify-between">
                  <Link
                    to={`/lots/${lot.id}`}
                    className="w-full py-2 px-3 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs transition-colors flex items-center justify-center space-x-1"
                  >
                    <span>Manage Lot & Bids</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </Link>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
