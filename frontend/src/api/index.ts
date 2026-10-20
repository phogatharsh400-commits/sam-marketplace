import { apiClient } from './client';
import {
  ApiResponse,
  PageResponse,
  AuthResponse,
  UserDto,
  LotDto,
  BidDto,
  ContractDto,
  PaymentDto,
  DeliveryDto,
  WarehouseDto,
  WarehouseBookingDto,
  DisputeDto,
  BenchmarkDto,
  PriceTrendDto,
  MspDto,
  NotificationDto,
  AuditLogDto,
  AdminDashboardDto,
  CropType,
  LotStatus,
  DisputeStatus,
  RoleType,
  AccountStatus,
  StorageAdviceDto,
  PriceForecastDto,
  PriceAlertDto,
  PriceAlertCreateDto,
  PriceAlertUpdateDto,
} from '../types';

// ================= AUTH API =================
export const authApi = {
  register: async (data: any) => {
    let endpoint = '/auth/register/farmer';
    if (data.role === 'BUYER') endpoint = '/auth/register/buyer';
    if (data.role === 'WAREHOUSE_OPERATOR') endpoint = '/auth/register/operator';
    const res = await apiClient.post<ApiResponse<AuthResponse>>(endpoint, data);
    return res.data;
  },
  registerFarmer: async (data: any) => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/register/farmer', data);
    return res.data;
  },
  registerBuyer: async (data: any) => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/register/buyer', data);
    return res.data;
  },
  registerOperator: async (data: any) => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/register/operator', data);
    return res.data;
  },
  login: async (credentials: { mobileNumber: string; password: string } | string, pass?: string) => {
    const payload = typeof credentials === 'string' ? { mobileNumber: credentials, password: pass || '' } : credentials;
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/login', payload);
    return res.data;
  },
  verifyOtp: async (data: { mobileNumber: string; otp: string }) => {
    const res = await apiClient.post<ApiResponse<{ verified: boolean }>>('/auth/verify-otp', data);
    return res.data;
  },
  forgotPassword: async (data: { mobileNumber: string; otp?: string; newPassword?: string }) => {
    const payload = {
      mobileNumber: data.mobileNumber,
      otp: data.otp || '123456',
      newPassword: data.newPassword || 'NewPassword@123',
    };
    const res = await apiClient.post<ApiResponse<void>>('/auth/forgot-password', payload);
    return res.data;
  },
  resetPassword: async (data: { mobileNumber: string; otp: string; newPassword: string }) => {
    const res = await apiClient.post<ApiResponse<void>>('/auth/forgot-password', data);
    return res.data;
  },
  logout: async () => {
    const res = await apiClient.post<ApiResponse<void>>('/auth/logout');
    return res.data;
  },
};

// ================= USER API =================
export const userApi = {
  getMe: async () => {
    const res = await apiClient.get<ApiResponse<UserDto>>('/users/me');
    if (res.data?.data) {
      res.data.data.name = res.data.data.fullName;
      res.data.data.accountStatus = res.data.data.status;
    }
    return res.data;
  },
  updateMe: async (data: Partial<UserDto>) => {
    const res = await apiClient.put<ApiResponse<UserDto>>('/users/me', data);
    return res.data;
  },
};

// ================= LOTS API =================
export const lotApi = {
  getOpenLots: async (params?: { crop?: string; district?: string; page?: number; size?: number; sort?: string }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<LotDto>>>('/lots', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content.map(l => ({ ...l, quantity: l.quantityQuintals, totalBids: l.bidsCount, imageUrls: l.images?.map(img => img.imageUrl) || [] })) };
  },
  getLotById: async (id: string | number) => {
    const res = await apiClient.get<ApiResponse<LotDto>>(`/lots/${id}`);
    if (res.data?.data) {
      const l = res.data.data;
      l.quantity = l.quantityQuintals;
      l.totalBids = l.bidsCount;
      l.imageUrls = l.images?.map(img => img.imageUrl) || [];
    }
    return res.data;
  },
  createLot: async (data: any) => {
    const payload = {
      ...data,
      quantityQuintals: data.quantityQuintals || data.quantity,
    };
    const res = await apiClient.post<ApiResponse<LotDto>>('/farmer/lots', payload);
    return res.data;
  },
  getMyLots: async (params?: { status?: LotStatus; page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<LotDto>>>('/farmer/lots', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content.map(l => ({ ...l, quantity: l.quantityQuintals, totalBids: l.bidsCount, imageUrls: l.images?.map(img => img.imageUrl) || [] })) };
  },
  updateLot: async (id: string | number, data: any) => {
    const res = await apiClient.put<ApiResponse<LotDto>>(`/farmer/lots/${id}`, data);
    return res.data;
  },
  cancelLot: async (id: string | number) => {
    const res = await apiClient.post<ApiResponse<LotDto>>(`/farmer/lots/${id}/cancel`);
    return res.data;
  },
};

// ================= BIDS API =================
export const bidApi = {
  placeBid: async (lotIdOrPayload: string | number | any, data?: { bidPricePerQuintal: number; quantityQuintals: number; remarks?: string }) => {
    let lotId = lotIdOrPayload;
    let payload = data;
    if (typeof lotIdOrPayload === 'object') {
      lotId = lotIdOrPayload.lotId;
      payload = lotIdOrPayload;
    }
    const res = await apiClient.post<ApiResponse<BidDto>>(`/lots/${lotId}/bids`, payload);
    return res.data;
  },
  getBidsForLot: async (lotId: string | number) => {
    const res = await apiClient.get<ApiResponse<BidDto[]>>(`/lots/${lotId}/bids`);
    return res.data;
  },
  getBidsByLot: async (lotId: string | number) => {
    const res = await apiClient.get<ApiResponse<BidDto[]>>(`/lots/${lotId}/bids`);
    return res.data;
  },
  getMyBids: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<BidDto>>>('/buyer/bids', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content };
  },
  withdrawBid: async (bidId: string | number) => {
    const res = await apiClient.post<ApiResponse<BidDto>>(`/buyer/bids/${bidId}/withdraw`);
    return res.data;
  },
  acceptBid: async (lotIdOrBidId: string | number, dataOrBidId?: any, extraData?: any) => {
    let bidId = typeof dataOrBidId === 'object' ? lotIdOrBidId : dataOrBidId;
    let payload = typeof dataOrBidId === 'object' ? dataOrBidId : (extraData || {});
    // Direct call to versatile accept endpoint
    const res = await apiClient.post<ApiResponse<ContractDto>>(`/bids/${bidId}/accept`, payload);
    return res.data;
  },
};

// ================= SALE CONTRACTS API =================
export const contractApi = {
  getContractById: async (id: string | number) => {
    const res = await apiClient.get<ApiResponse<ContractDto>>(`/contracts/${id}`);
    if (res.data?.data) {
      res.data.data.quantity = res.data.data.quantityQuintals;
      res.data.data.pricePerUnit = res.data.data.pricePerQuintal;
    }
    return res.data;
  },
  getMyContracts: async (params?: { page?: number; size?: number }) => {
    let res: any;
    try {
      res = await apiClient.get<ApiResponse<PageResponse<ContractDto>>>('/farmer/contracts', { params });
    } catch {
      res = await apiClient.get<ApiResponse<PageResponse<ContractDto>>>('/buyer/contracts', { params });
    }
    const content = res.data.data?.content || [];
    return {
      ...res.data,
      data: content.map((c: ContractDto) => ({
        ...c,
        quantity: c.quantityQuintals,
        pricePerUnit: c.pricePerQuintal,
      })),
    };
  },
  getFarmerContracts: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<ContractDto>>>('/farmer/contracts', { params });
    const content = res.data.data?.content || [];
    return {
      ...res.data,
      data: content.map((c: ContractDto) => ({
        ...c,
        quantity: c.quantityQuintals,
        pricePerUnit: c.pricePerQuintal,
      })),
    };
  },
  getBuyerContracts: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<ContractDto>>>('/buyer/contracts', { params });
    const content = res.data.data?.content || [];
    return {
      ...res.data,
      data: content.map((c: ContractDto) => ({
        ...c,
        quantity: c.quantityQuintals,
        pricePerUnit: c.pricePerQuintal,
      })),
    };
  },
  confirmDispatch: async (contractId: string | number) => {
    const res = await apiClient.post<ApiResponse<ContractDto>>(`/farmer/contracts/${contractId}/dispatch`);
    return res.data;
  },
};

// ================= PAYMENTS & ESCROW API =================
export const paymentApi = {
  fundEscrow: async (contractId: string | number, data: { paymentMethod: string; notes?: string; referenceNumber?: string; autoSimulateSuccess?: boolean }) => {
    const res = await apiClient.post<ApiResponse<PaymentDto>>(`/contracts/${contractId}/payments/escrow`, data);
    return res.data;
  },
  getPaymentById: async (id: string | number) => {
    const res = await apiClient.get<ApiResponse<PaymentDto>>(`/payments/${id}`);
    return res.data;
  },
  simulateSuccess: async (paymentId: string | number) => {
    const res = await apiClient.post<ApiResponse<PaymentDto>>(`/payments/${paymentId}/simulate-success`);
    return res.data;
  },
  simulateFailure: async (paymentId: string | number) => {
    const res = await apiClient.post<ApiResponse<PaymentDto>>(`/payments/${paymentId}/simulate-failure`);
    return res.data;
  },
  releasePayment: async (paymentId: string | number) => {
    const res = await apiClient.post<ApiResponse<PaymentDto>>(`/payments/${paymentId}/release`);
    return res.data;
  },
  refundPayment: async (paymentId: string | number, reason: string) => {
    const res = await apiClient.post<ApiResponse<PaymentDto>>(`/payments/${paymentId}/refund`, { reason });
    return res.data;
  },
};

// ================= DELIVERY API =================
export const deliveryApi = {
  recordDelivery: async (contractId: string | number, data: any) => {
    const payload = {
      quantityDeliveredQuintals: data.actualQuantityDelivered || data.quantityDeliveredQuintals,
      deliveryDate: data.deliveryDate || new Date().toISOString().split('T')[0],
      qualityRemarks: data.remarks || data.qualityRemarks,
      weightSlipUrl: data.weightSlipUrl,
      deliveryPhotosMetadata: data.deliveryPhotoUrls ? JSON.stringify(data.deliveryPhotoUrls) : data.deliveryPhotosMetadata,
    };
    const res = await apiClient.post<ApiResponse<DeliveryDto>>(`/contracts/${contractId}/delivery`, payload);
    return res.data;
  },
  getDeliveryByContract: async (contractId: string | number) => {
    const res = await apiClient.get<ApiResponse<DeliveryDto>>(`/contracts/${contractId}/delivery`);
    return res.data;
  },
  confirmDelivery: async (deliveryId: string | number, data: any) => {
    const res = await apiClient.put<ApiResponse<DeliveryDto>>(`/deliveries/${deliveryId}/confirm`, data);
    return res.data;
  },
};

// ================= WAREHOUSES API =================
export const warehouseApi = {
  searchWarehouses: async (params?: { district?: string; crop?: string; page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<WarehouseDto>>>('/warehouses', { params });
    const content = res.data.data?.content || [];
    return {
      ...res.data,
      data: content.map(w => ({
        ...w,
        supportedCrops: typeof w.supportedCrops === 'string' ? w.supportedCrops.split(',').map(s => s.trim()) : w.supportedCrops,
        storageChargePerQuintalPerMonth: w.storageChargePerQuintalMonth || w.storageChargePerQuintalPerMonth,
      })),
    };
  },
  getWarehouseById: async (id: string | number) => {
    const res = await apiClient.get<ApiResponse<WarehouseDto>>(`/warehouses/${id}`);
    if (res.data?.data) {
      const w = res.data.data;
      w.supportedCrops = typeof w.supportedCrops === 'string' ? w.supportedCrops.split(',').map(s => s.trim()) : w.supportedCrops;
      w.storageChargePerQuintalPerMonth = w.storageChargePerQuintalMonth || w.storageChargePerQuintalPerMonth;
    }
    return res.data;
  },
  getMyWarehouses: async () => {
    const res = await apiClient.get<ApiResponse<PageResponse<WarehouseDto>>>('/warehouses');
    const content = res.data.data?.content || [];
    return {
      ...res.data,
      data: content.map(w => ({
        ...w,
        supportedCrops: typeof w.supportedCrops === 'string' ? w.supportedCrops.split(',').map(s => s.trim()) : w.supportedCrops,
        storageChargePerQuintalPerMonth: w.storageChargePerQuintalMonth || w.storageChargePerQuintalPerMonth,
      })),
    };
  },
  createWarehouse: async (data: any) => {
    const payload = {
      ...data,
      supportedCrops: Array.isArray(data.supportedCrops) ? data.supportedCrops.join(',') : data.supportedCrops,
      storageChargePerQuintalMonth: data.storageChargePerQuintalPerMonth || data.storageChargePerQuintalMonth,
    };
    const res = await apiClient.post<ApiResponse<WarehouseDto>>('/warehouses', payload);
    return res.data;
  },
  bookWarehouse: async (warehouseId: string | number, data: any) => {
    const res = await apiClient.post<ApiResponse<WarehouseBookingDto>>(`/warehouses/${warehouseId}/bookings`, data);
    return res.data;
  },
  requestBooking: async (warehouseId: string | number, data: any) => {
    const res = await apiClient.post<ApiResponse<WarehouseBookingDto>>(`/warehouses/${warehouseId}/bookings`, data);
    return res.data;
  },
  getMyBookings: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<WarehouseBookingDto>>>('/farmer/bookings', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content.map(b => ({ ...b, totalCost: b.totalEstimatedCost })) };
  },
  getFarmerBookings: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<WarehouseBookingDto>>>('/farmer/bookings', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content.map(b => ({ ...b, totalCost: b.totalEstimatedCost })) };
  },
  getOperatorBookings: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<WarehouseBookingDto>>>('/operator/bookings', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content.map(b => ({ ...b, totalCost: b.totalEstimatedCost })) };
  },
  updateBookingStatus: async (bookingId: string | number, status: string) => {
    if (status === 'APPROVED') {
      const res = await apiClient.put<ApiResponse<WarehouseBookingDto>>(`/bookings/${bookingId}/approve`);
      return res.data;
    } else {
      const res = await apiClient.put<ApiResponse<WarehouseBookingDto>>(`/bookings/${bookingId}/reject`);
      return res.data;
    }
  },
  approveBooking: async (bookingId: string | number) => {
    const res = await apiClient.put<ApiResponse<WarehouseBookingDto>>(`/bookings/${bookingId}/approve`);
    return res.data;
  },
  rejectBooking: async (bookingId: string | number, reason?: string) => {
    const res = await apiClient.put<ApiResponse<WarehouseBookingDto>>(`/bookings/${bookingId}/reject`, { reason });
    return res.data;
  },
};

// ================= DISPUTES API =================
export const disputeApi = {
  createDispute: async (data: any) => {
    const res = await apiClient.post<ApiResponse<DisputeDto>>(`/contracts/${data.contractId}/disputes`, data);
    return res.data;
  },
  raiseDispute: async (contractId: string | number, data: any) => {
    const res = await apiClient.post<ApiResponse<DisputeDto>>(`/contracts/${contractId}/disputes`, data);
    return res.data;
  },
  getAllDisputes: async (params?: { status?: DisputeStatus; page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<DisputeDto>>>('/disputes', { params });
    const content = res.data.data?.content || [];
    return {
      ...res.data,
      data: content.map(d => ({
        ...d,
        raisedByName: d.raisedByUserName || 'Buyer/Farmer',
      })),
    };
  },
  getDisputes: async (params?: { status?: DisputeStatus; page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<DisputeDto>>>('/disputes', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content };
  },
  getDisputeById: async (id: string | number) => {
    const res = await apiClient.get<ApiResponse<DisputeDto>>(`/disputes/${id}`);
    return res.data;
  },
  resolveDispute: async (disputeId: string | number, data: any) => {
    const payload = {
      resolution: data.status || data.resolution,
      resolutionNotes: data.resolutionNotes,
      partialBuyerRefund: data.refundPercentage,
    };
    const res = await apiClient.put<ApiResponse<DisputeDto>>(`/admin/disputes/${disputeId}/resolve`, payload);
    return res.data;
  },
};

// ================= MANDI BENCHMARKS API =================
export const benchmarkApi = {
  getLatestBenchmark: async (crop: string, district: string) => {
    const res = await apiClient.get<ApiResponse<BenchmarkDto>>(`/benchmarks/${crop.toUpperCase()}/${district}`);
    if (res.data?.data) {
      res.data.data.source = res.data.data.dataSource || 'Haryana State APMC Mandi Board';
    }
    return res.data;
  },
  getBenchmarkTrends: async (crop: string, district: string, days = 30) => {
    const res = await apiClient.get<ApiResponse<any>>(`/benchmarks/${crop.toUpperCase()}/${district}/trends`);
    const raw = res.data?.data;
    const history = Array.isArray(raw) ? raw : (raw?.history30Days || []);
    return {
      ...res.data,
      data: history.map((h: any) => ({
        crop,
        district,
        state: 'Haryana',
        minPrice: h.minPrice,
        maxPrice: h.maxPrice,
        modalPrice: h.modalPrice,
        msp: h.msp,
        priceDate: h.date || h.priceDate || new Date().toISOString(),
      })),
    };
  },
  getMsps: async () => {
    const defaultMsps: Record<string, number> = {
      Wheat: 2275,
      Paddy: 2183,
      Mustard: 5650,
      WHEAT: 2275,
      PADDY: 2183,
      MUSTARD: 5650,
    };
    try {
      const res = await apiClient.get<ApiResponse<MspDto[]>>('/msp');
      const map: Record<string, number> = { ...defaultMsps };
      (res.data?.data || []).forEach((m: any) => {
        const val = Number(m.mspPerQuintal || m.mspPrice || defaultMsps[m.crop] || 2200);
        map[m.crop] = val;
        if (m.crop === 'WHEAT') map['Wheat'] = val;
        if (m.crop === 'PADDY') map['Paddy'] = val;
        if (m.crop === 'MUSTARD') map['Mustard'] = val;
      });
      return { ...res.data, data: map };
    } catch {
      return { success: true, message: 'Default MSP', data: defaultMsps, timestamp: new Date().toISOString() };
    }
  },
  getIndicativeRange: async (crop: string, district: string) => {
    const defaults: Record<string, { min: number; max: number; modal: number; msp: number }> = {
      Wheat: { min: 2350, max: 2550, modal: 2450, msp: 2275 },
      Paddy: { min: 2200, max: 2450, modal: 2350, msp: 2183 },
      Mustard: { min: 5600, max: 6100, modal: 5850, msp: 5650 },
    };
    try {
      const res = await apiClient.get<ApiResponse<any>>(`/benchmarks/${crop.toUpperCase()}/${district}/trends`);
      const raw = res.data?.data;
      if (Array.isArray(raw) && raw.length > 0) {
        const latest = raw[raw.length - 1];
        return {
          ...res.data,
          data: {
            min: latest.minPrice || defaults[crop]?.min || 2300,
            max: latest.maxPrice || defaults[crop]?.max || 2600,
            modal: latest.modalPrice || defaults[crop]?.modal || 2450,
            msp: latest.msp || defaults[crop]?.msp || 2275,
          },
        };
      }
      return { success: true, message: 'Indicative range', data: defaults[crop] || defaults['Wheat'], timestamp: new Date().toISOString() };
    } catch {
      return { success: true, message: 'Indicative range fallback', data: defaults[crop] || defaults['Wheat'], timestamp: new Date().toISOString() };
    }
  },
  getAllBenchmarks: async () => {
    const res = await apiClient.get<ApiResponse<BenchmarkDto[]>>('/benchmarks');
    return res.data;
  },
  syncDailyMandiPrices: async () => {
    const res = await apiClient.post<ApiResponse<any>>('/benchmarks/sync');
    return res.data;
  },
  getSyncStatus: async () => {
    const res = await apiClient.get<ApiResponse<any>>('/benchmarks/sync-status');
    return res.data;
  },
};

// ================= NOTIFICATIONS API =================
export const notificationApi = {
  getNotifications: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<NotificationDto>>>('/notifications', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content };
  },
  getUnreadCount: async () => {
    const res = await apiClient.get<ApiResponse<{ unreadCount: number }>>('/notifications/unread-count');
    return res.data;
  },
  markAsRead: async (id: string | number) => {
    const res = await apiClient.put<ApiResponse<void>>(`/notifications/${id}/read`);
    return res.data;
  },
  markAllAsRead: async () => {
    const res = await apiClient.put<ApiResponse<void>>('/notifications/read-all');
    return res.data;
  },
};

// ================= ADMIN API =================
export const adminApi = {
  getMarketplaceStats: async () => {
    const res = await apiClient.get<ApiResponse<AdminDashboardDto>>('/admin/dashboard');
    return res.data;
  },
  getDashboard: async () => {
    const res = await apiClient.get<ApiResponse<AdminDashboardDto>>('/admin/dashboard');
    return res.data;
  },
  getAllUsers: async (params?: { role?: RoleType; status?: AccountStatus; page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<UserDto>>>('/admin/users', { params });
    const content = res.data.data?.content || [];
    return {
      ...res.data,
      data: content.map(u => ({
        ...u,
        name: u.fullName,
        accountStatus: u.status,
      })),
    };
  },
  getUsers: async (params?: { role?: RoleType; status?: AccountStatus; page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<UserDto>>>('/admin/users', { params });
    return res.data;
  },
  updateUserStatus: async (userId: string | number, status: string) => {
    if (status === 'ACTIVE') {
      const res = await apiClient.put<ApiResponse<UserDto>>(`/admin/users/${userId}/verify`);
      return res.data;
    } else if (status === 'SUSPENDED') {
      const res = await apiClient.put<ApiResponse<UserDto>>(`/admin/users/${userId}/suspend`);
      return res.data;
    } else {
      const res = await apiClient.put<ApiResponse<UserDto>>(`/admin/users/${userId}/reject`);
      return res.data;
    }
  },
  verifyUser: async (userId: string | number) => {
    const res = await apiClient.put<ApiResponse<UserDto>>(`/admin/users/${userId}/verify`);
    return res.data;
  },
  suspendUser: async (userId: string | number, reason?: string) => {
    const res = await apiClient.put<ApiResponse<UserDto>>(`/admin/users/${userId}/suspend`, { reason });
    return res.data;
  },
  getAuditLogs: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<PageResponse<AuditLogDto>>>('/admin/audit-logs', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content };
  },
  createBenchmark: async (data: any) => {
    const payload = {
      ...data,
      crop: data.crop.toUpperCase(),
      dataSource: data.source || data.dataSource || 'Haryana State APMC Mandi Board',
    };
    const res = await apiClient.post<ApiResponse<BenchmarkDto>>('/admin/benchmarks', payload);
    return res.data;
  },
};

// ================= MERIT & TRUST API =================
export const meritApi = {
  getMyMeritProfile: async () => {
    const res = await apiClient.get<ApiResponse<any>>('/merit/me');
    return res.data;
  },
  getUserMeritProfile: async (userId: string | number) => {
    const res = await apiClient.get<ApiResponse<any>>(`/merit/user/${userId}`);
    return res.data;
  },
  getPlatformMeritAuditLogs: async () => {
    const res = await apiClient.get<ApiResponse<any>>('/merit/audit-logs');
    return res.data;
  },
  simulateViolation: async (userId: string | number, deduction: number, reason: string, referenceEntity?: string) => {
    const res = await apiClient.post<ApiResponse<any>>('/merit/simulate-violation', null, {
      params: { userId, deduction, reason, referenceEntity },
    });
    return res.data;
  },
  simulateReward: async (userId: string | number, bonus: number, reason: string, referenceEntity?: string) => {
    const res = await apiClient.post<ApiResponse<any>>('/merit/simulate-reward', null, {
      params: { userId, bonus, reason, referenceEntity },
    });
    return res.data;
  },
};

// ================= WALLET & SECURITY TOKEN VAULT API =================
export const walletApi = {
  getWallet: async () => {
    const res = await apiClient.get<ApiResponse<any>>('/wallet');
    return res.data;
  },
  deposit: async (data: { amount: number; paymentMethod?: string; referenceNumber?: string; notes?: string }) => {
    const res = await apiClient.post<ApiResponse<any>>('/wallet/deposit', data);
    return res.data;
  },
  withdraw: async (data: {
    amount: number;
    withdrawMethod?: string;
    bankAccountNumber?: string;
    ifscCode?: string;
    accountHolderName?: string;
    upiId?: string;
  }) => {
    const res = await apiClient.post<ApiResponse<any>>('/wallet/withdraw', data);
    return res.data;
  },
  getTransactions: async (params?: { page?: number; size?: number }) => {
    const res = await apiClient.get<ApiResponse<any>>('/wallet/transactions', { params });
    const content = res.data.data?.content || [];
    return { ...res.data, data: content };
  },
  getRecentTransactions: async () => {
    const res = await apiClient.get<ApiResponse<any>>('/wallet/recent-transactions');
    return res.data;
  },
  requestENWRAdvance: async (bookingId: string | number, amount: number) => {
    const res = await apiClient.post<ApiResponse<any>>('/wallet/enwr-advance', null, {
      params: { bookingId, amount },
    });
    return res.data;
  },
};

// ================= SMART SELL-OR-STORE ADVISOR API =================
export const advisorApi = {
  getAdvice: async (lotId: string | number) => {
    const res = await apiClient.get<ApiResponse<StorageAdviceDto>>(`/lots/${lotId}/advice`);
    return res.data;
  },
  generateAdvice: async (lotId: string | number) => {
    const res = await apiClient.post<ApiResponse<StorageAdviceDto>>(`/lots/${lotId}/advice/generate`);
    return res.data;
  },
  getForecast: async (crop: string, district: string) => {
    const res = await apiClient.get<ApiResponse<PriceForecastDto>>(`/forecasts/${crop}/${district}`);
    return res.data;
  },
};

// ================= PRICE ALERTS API =================
export const priceAlertApi = {
  getMyAlerts: async () => {
    const res = await apiClient.get<ApiResponse<PriceAlertDto[]>>('/price-alerts');
    return res.data;
  },
  createAlert: async (data: PriceAlertCreateDto) => {
    const res = await apiClient.post<ApiResponse<PriceAlertDto>>('/price-alerts', data);
    return res.data;
  },
  updateAlert: async (id: string, data: PriceAlertUpdateDto) => {
    const res = await apiClient.put<ApiResponse<PriceAlertDto>>(`/price-alerts/${id}`, data);
    return res.data;
  },
  deleteAlert: async (id: string) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/price-alerts/${id}`);
    return res.data;
  },
  toggleAlert: async (id: string) => {
    const res = await apiClient.post<ApiResponse<PriceAlertDto>>(`/price-alerts/${id}/toggle`);
    return res.data;
  },
};


