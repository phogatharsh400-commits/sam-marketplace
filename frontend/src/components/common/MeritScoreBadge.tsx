import React, { useState } from 'react';
import { Award, ShieldCheck, AlertTriangle, Star, Info } from 'lucide-react';
import { MeritHistoryModal } from './MeritHistoryModal';

interface MeritScoreBadgeProps {
  score?: number;
  tier?: string;
  userId?: string;
  userName?: string;
  showModalOnClick?: boolean;
  size?: 'sm' | 'md' | 'lg';
}

export const MeritScoreBadge: React.FC<MeritScoreBadgeProps> = ({
  score = 100,
  tier = 'PLATINUM',
  userId,
  userName,
  showModalOnClick = true,
  size = 'md',
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const getTierConfig = (points: number, t: string) => {
    if (points >= 90 || t === 'PLATINUM') {
      return {
        label: 'Platinum Pioneer',
        bg: 'bg-emerald-50',
        border: 'border-emerald-200',
        text: 'text-emerald-800',
        iconColor: 'text-emerald-600',
        starColor: 'fill-emerald-500 text-emerald-500',
      };
    }
    if (points >= 75 || t === 'GOLD') {
      return {
        label: 'Gold Verified',
        bg: 'bg-amber-50',
        border: 'border-amber-200',
        text: 'text-amber-800',
        iconColor: 'text-amber-600',
        starColor: 'fill-amber-500 text-amber-500',
      };
    }
    if (points >= 60 || t === 'SILVER') {
      return {
        label: 'Silver Standard',
        bg: 'bg-blue-50',
        border: 'border-blue-200',
        text: 'text-blue-800',
        iconColor: 'text-blue-600',
        starColor: 'fill-blue-500 text-blue-500',
      };
    }
    return {
      label: 'Watchlist (<60 pts)',
      bg: 'bg-rose-50',
      border: 'border-rose-200',
      text: 'text-rose-800',
      iconColor: 'text-rose-600',
      starColor: 'fill-rose-500 text-rose-500',
    };
  };

  const config = getTierConfig(score, tier);

  const sizeClasses = {
    sm: 'px-2 py-0.5 text-[10px]',
    md: 'px-2.5 py-1 text-xs',
    lg: 'px-3.5 py-2 text-sm',
  };

  return (
    <>
      <button
        type="button"
        onClick={() => showModalOnClick && setIsModalOpen(true)}
        className={`inline-flex items-center gap-1.5 font-bold rounded-xl border transition-all ${
          config.bg
        } ${config.border} ${config.text} ${sizeClasses[size]} ${
          showModalOnClick ? 'hover:shadow-xs cursor-pointer active:scale-95' : 'cursor-default'
        }`}
        title="Click to inspect Merit & Trust Score history"
      >
        {score < 60 ? (
          <AlertTriangle className={`w-3.5 h-3.5 ${config.iconColor}`} />
        ) : (
          <Star className={`w-3.5 h-3.5 ${config.starColor}`} />
        )}
        <span>{score}/100</span>
        <span className="hidden sm:inline opacity-80 font-medium">| {config.label}</span>
      </button>

      {isModalOpen && (
        <MeritHistoryModal
          userId={userId}
          userName={userName}
          currentScore={score}
          currentTier={tier}
          onClose={() => setIsModalOpen(false)}
        />
      )}
    </>
  );
};
