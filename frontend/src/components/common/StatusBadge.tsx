import React from 'react';

interface StatusBadgeProps {
  status: string;
  size?: 'sm' | 'md' | 'lg';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, size = 'md' }) => {
  const getColors = (s: string) => {
    switch (s?.toUpperCase()) {
      case 'ACTIVE':
      case 'OPEN':
      case 'APPROVED':
      case 'DELIVERED':
      case 'PAID':
      case 'PAYMENT_RELEASED':
      case 'CONFIRMED':
      case 'RESOLVED_FOR_FARMER':
      case 'RESOLVED_FOR_BUYER':
        return 'bg-emerald-50 text-emerald-700 border-emerald-300';

      case 'ESCROW_FUNDED':
      case 'HELD_IN_ESCROW':
      case 'READY_FOR_DISPATCH':
      case 'DISPATCHED':
      case 'IN_TRANSIT':
      case 'BID_ACCEPTED':
      case 'ACCEPTED':
        return 'bg-blue-50 text-blue-700 border-blue-300';

      case 'PENDING_VERIFICATION':
      case 'REQUESTED':
      case 'AWAITING_ESCROW':
      case 'ESCROW_PENDING':
      case 'UNDER_REVIEW':
      case 'INITIATED':
      case 'DRAFT':
        return 'bg-amber-50 text-amber-700 border-amber-300';

      case 'SUSPENDED':
      case 'REJECTED':
      case 'FAILED':
      case 'DISPUTED':
      case 'CANCELLED':
      case 'REFUNDED':
        return 'bg-rose-50 text-rose-700 border-rose-300';

      case 'EXPIRED':
      case 'WITHDRAWN':
      case 'CLOSED':
      default:
        return 'bg-slate-50 text-slate-700 border-slate-300';
    }
  };

  const formatText = (s: string) => {
    if (!s) return '';
    return s.replace(/_/g, ' ');
  };

  const sizeClasses = {
    sm: 'text-xs px-2 py-0.5',
    md: 'text-xs px-2.5 py-1 font-medium',
    lg: 'text-sm px-3 py-1.5 font-medium',
  };

  return (
    <span
      className={`inline-flex items-center rounded-full border ${getColors(
        status
      )} ${sizeClasses[size]} shadow-sm transition-colors`}
    >
      <span className="w-1.5 h-1.5 rounded-full bg-current mr-1.5 opacity-75"></span>
      {formatText(status)}
    </span>
  );
};
