import React from 'react';
import { AlertTriangle } from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';

export const PrototypeBanner: React.FC = () => {
  const { t } = useLanguage();

  return (
    <div className="bg-amber-500 text-amber-950 px-4 py-1.5 text-xs sm:text-sm font-medium flex items-center justify-between shadow-inner">
      <div className="flex items-center gap-2 mx-auto max-w-7xl">
        <AlertTriangle className="w-4 h-4 flex-shrink-0 text-amber-900" />
        <span>
          <strong>Academic Prototype:</strong> {t('prototypeNotice')} Real-time OTP: <code className="bg-amber-400/60 px-1 py-0.5 rounded font-mono font-bold">123456</code> | Simulated Escrow & Mandi Benchmarks.
        </span>
      </div>
    </div>
  );
};
