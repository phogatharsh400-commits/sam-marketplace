import React from 'react';
import { Link } from 'react-router-dom';
import { Sprout, ShieldCheck, HeartHandshake, PhoneCall, HelpCircle, FileText } from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';

export const Footer: React.FC = () => {
  const { t } = useLanguage();

  return (
    <footer className="bg-emerald-950 text-emerald-100 border-t border-emerald-900 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand & Mission */}
          <div className="space-y-4">
            <div className="flex items-center space-x-2">
              <div className="w-9 h-9 rounded-lg bg-emerald-600 flex items-center justify-center text-white font-bold shadow-md shadow-emerald-900">
                <Sprout className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-bold tracking-tight text-white">SAM</span>
            </div>
            <p className="text-sm text-emerald-300/80 leading-relaxed">
              Smart Agricultural Marketplace connects verified Indian farmers with verified grain & oilseed buyers through transparent digital auctions, simulated escrow, and integrated warehousing.
            </p>
            <div className="pt-2">
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-800 text-emerald-200 border border-emerald-700">
                Academic SRS Prototype v1.0
              </span>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="text-sm font-semibold uppercase tracking-wider text-emerald-400 mb-4">
              Marketplace
            </h3>
            <ul className="space-y-2.5 text-sm text-emerald-300">
              <li>
                <Link to="/marketplace" className="hover:text-white transition-colors">
                  {t('lots')}
                </Link>
              </li>
              <li>
                <Link to="/benchmarks" className="hover:text-white transition-colors">
                  {t('mandiBenchmarks')}
                </Link>
              </li>
              <li>
                <Link to="/warehouses" className="hover:text-white transition-colors">
                  {t('warehouses')}
                </Link>
              </li>
              <li>
                <Link to="/login" className="hover:text-white transition-colors">
                  {t('login')} / {t('register')}
                </Link>
              </li>
            </ul>
          </div>

          {/* Prototype Features */}
          <div>
            <h3 className="text-sm font-semibold uppercase tracking-wider text-emerald-400 mb-4">
              Key Features
            </h3>
            <ul className="space-y-2.5 text-sm text-emerald-300">
              <li className="flex items-center space-x-2">
                <ShieldCheck className="w-4 h-4 text-emerald-400" />
                <span>Simulated Escrow Protection</span>
              </li>
              <li className="flex items-center space-x-2">
                <HeartHandshake className="w-4 h-4 text-emerald-400" />
                <span>Transparent Direct Bidding</span>
              </li>
              <li className="flex items-center space-x-2">
                <FileText className="w-4 h-4 text-emerald-400" />
                <span>Audited Sale Contracts</span>
              </li>
              <li className="flex items-center space-x-2">
                <HelpCircle className="w-4 h-4 text-emerald-400" />
                <span>Admin Dispute Resolution</span>
              </li>
            </ul>
          </div>

          {/* Contact & Disclaimer */}
          <div>
            <h3 className="text-sm font-semibold uppercase tracking-wider text-emerald-400 mb-4">
              Academic Notice
            </h3>
            <p className="text-xs text-emerald-400/90 leading-relaxed mb-4">
              This application is an educational MVP demonstrating agricultural e-trading workflows for Haryana districts (Karnal, Hisar, Ambala, Rohtak, Sirsa, Panipat).
            </p>
            <div className="flex items-center text-xs text-emerald-300/80 space-x-2">
              <PhoneCall className="w-4 h-4 text-emerald-400" />
              <span>Demo Toll-Free: 1800-SAM-AGRI</span>
            </div>
          </div>
        </div>

        <div className="border-t border-emerald-900/80 mt-10 pt-6 flex flex-col sm:flex-row items-center justify-between text-xs text-emerald-400/70">
          <p>© {new Date().getFullYear()} Smart Agricultural Marketplace (SAM). Built for academic demonstration.</p>
          <p className="mt-2 sm:mt-0">Haryana Agri-Tech Innovation Framework</p>
        </div>
      </div>
    </footer>
  );
};
