export type RoleType = 'FARMER' | 'BUYER' | 'WAREHOUSE_OPERATOR' | 'ADMIN';
export type Role = RoleType;

export type AccountStatus = 'PENDING_VERIFICATION' | 'ACTIVE' | 'SUSPENDED' | 'REJECTED';

export type CropType = 'WHEAT' | 'PADDY' | 'MUSTARD' | 'Wheat' | 'Paddy' | 'Mustard';

export type ProduceGrade = 'GRADE_A' | 'GRADE_B' | 'GRADE_C' | 'FAIR_AVERAGE_QUALITY' | 'A+' | 'A' | 'B' | 'C';

export type LotStatus =
  | 'DRAFT'
  | 'OPEN'
  | 'BID_ACCEPTED'
  | 'ESCROW_PENDING'
  | 'ESCROW_FUNDED'
  | 'READY_FOR_DISPATCH'
  | 'IN_TRANSIT'
  | 'DELIVERED'
  | 'PAID'
  | 'DISPUTED'
  | 'CANCELLED'
  | 'EXPIRED';

export type BidStatus = 'ACTIVE' | 'ACCEPTED' | 'REJECTED' | 'WITHDRAWN' | 'EXPIRED';

export type ContractStatus =
  | 'CREATED'
  | 'AWAITING_ESCROW'
  | 'ESCROW_FUNDED'
  | 'READY_FOR_DISPATCH'
  | 'DISPATCHED'
  | 'DELIVERED'
  | 'PAYMENT_RELEASED'
  | 'DISPUTED'
  | 'CANCELLED'
  | 'REFUNDED';

export type PaymentStatus =
  | 'INITIATED'
  | 'SUCCESS'
  | 'FAILED'
  | 'HELD_IN_ESCROW'
  | 'RELEASED'
  | 'REFUNDED'
  | 'DISPUTED';

export type PaymentMethod = 'UPI' | 'NET_BANKING' | 'BANK_TRANSFER' | 'ESCROW_WALLET';

export type WarehouseBookingStatus = 'REQUESTED' | 'APPROVED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED';

export type DisputeStatus =
  | 'OPEN'
  | 'UNDER_REVIEW'
  | 'RESOLVED_FOR_FARMER'
  | 'RESOLVED_FOR_BUYER'
  | 'PARTIALLY_RESOLVED'
  | 'CLOSED';

export type DisputeResolution = 'RELEASE_TO_FARMER' | 'REFUND_BUYER' | 'PARTIALLY_RESOLVED' | 'DISMISSED';

export type NotificationType =
  | 'REGISTRATION_SUCCESS'
  | 'USER_VERIFICATION'
  | 'NEW_BID'
  | 'BID_ACCEPTED'
  | 'BID_REJECTED'
  | 'ESCROW_FUNDED'
  | 'DISPATCH_REQUIRED'
  | 'DELIVERY_CONFIRMED'
  | 'PAYMENT_RELEASED'
  | 'PAYMENT_REFUNDED'
  | 'WAREHOUSE_BOOKING_REQUESTED'
  | 'WAREHOUSE_BOOKING_APPROVED'
  | 'WAREHOUSE_BOOKING_REJECTED'
  | 'DISPUTE_OPENED'
  | 'DISPUTE_RESOLVED'
  | 'SYSTEM_ALERT';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode?: string;
  timestamp: string;
  path?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  empty: boolean;
}

export interface UserDto {
  id: string | number;
  mobileNumber: string;
  email?: string;
  fullName: string;
  name?: string;
  role: RoleType;
  status: AccountStatus;
  accountStatus?: AccountStatus;
  isMobileVerified: boolean;
  createdAt: string;
  meritPoints?: number;
  trustTier?: string;
  totalViolations?: number;
  totalSuccessfulTrades?: number;
  state?: string;
  district?: string;
  village?: string;
  bankAccountNumber?: string;
  bankIfsc?: string;
  ifscCode?: string;
  preferredLanguage?: string;
  landAreaAcres?: number;
  businessName?: string;
  gstin?: string;
  panNumber?: string;
  businessAddress?: string;
  buyerType?: string;
  licenseNumber?: string;
}
export type UserResponse = UserDto;

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: string | number;
  mobileNumber: string;
  email?: string;
  fullName: string;
  name?: string;
  role: RoleType;
  status: AccountStatus;
  accountStatus?: AccountStatus;
  isMobileVerified: boolean;
  mockOtpNote?: string;
  mockOtp?: string;
}

export interface LotImageDto {
  id: string | number;
  imageUrl: string;
  caption?: string;
  isPrimary: boolean;
}

export interface LotDto {
  id: string | number;
  lotNumber: string;
  farmerId: string | number;
  farmerName: string;
  farmerMobile: string;
  crop: string;
  variety: string;
  quantityQuintals: number;
  quantity?: number;
  unit: string;
  grade: string;
  moisturePercentage: number;
  state: string;
  district: string;
  village: string;
  pickupLocation: string;
  minPricePerQuintal: number;
  expectedDispatchDate: string;
  auctionClosingDate: string;
  description: string;
  storageRequirement?: string;
  status: LotStatus;
  poolingEnabled?: boolean;
  minOrderQuantity?: number;
  sampleApproved?: boolean;
  sampleStatus?: string;
  foreignMatterPercent?: number;
  brokenGrainsPercent?: number;
  qualityCertificateNumber?: string;
  images: LotImageDto[];
  imageUrls?: string[];
  bidsCount: number;
  totalBids?: number;
  highestBid?: number;
  createdAt: string;
  updatedAt: string;
}
export type LotResponse = LotDto;

export interface BidDto {
  id: string | number;
  lotId: string | number;
  lotNumber: string;
  crop: string;
  variety: string;
  lotDistrict: string;
  buyerId: string | number;
  buyerName: string;
  buyerBusinessName: string;
  buyerMobile: string;
  bidPricePerQuintal: number;
  quantityQuintals: number;
  totalAmount: number;
  bidTime: string;
  status: BidStatus;
  remarks?: string;
  createdAt?: string;
}
export type BidResponse = BidDto;

export interface ContractDto {
  id: string | number;
  contractNumber: string;
  lotId: string | number;
  lotNumber: string;
  crop: string;
  variety: string;
  farmerId: string | number;
  farmerName: string;
  farmerMobile: string;
  buyerId: string | number;
  buyerName: string;
  buyerBusinessName: string;
  buyerMobile: string;
  acceptedBidId: string | number;
  quantityQuintals: number;
  quantity?: number;
  pricePerQuintal: number;
  pricePerUnit?: number;
  totalAmount: number;
  deliveryTerms: string;
  dispatchDeadline: string;
  deliveryDeadline: string;
  status: ContractStatus;
  createdAt: string;
  paymentTransactionId?: string;
  paymentStatus?: PaymentStatus;
  escrowTokenCode?: string;
  escrowTokenHash?: string;
  bankGuaranteeRef?: string;
  qualityCertificateNumber?: string;
  sampleStatus?: string;
}
export type SaleContractResponse = ContractDto;

export interface PaymentDto {
  id: string | number;
  contractId: string | number;
  contractNumber: string;
  payerId: string | number;
  payerName: string;
  payeeId: string | number;
  payeeName: string;
  amount: number;
  paymentMethod: PaymentMethod | string;
  status: PaymentStatus;
  transactionReference: string;
  simulationMode: boolean;
  notes?: string;
  escrowTokenCode?: string;
  escrowTokenHash?: string;
  bankGuaranteeRef?: string;
  createdAt: string;
  updatedAt: string;
}
export type PaymentResponse = PaymentDto;

export interface DeliveryDto {
  id: string | number;
  contractId: string | number;
  contractNumber: string;
  quantityDeliveredQuintals: number;
  deliveryDate: string;
  qualityRemarks?: string;
  buyerConfirmed: boolean;
  farmerConfirmed: boolean;
  weightSlipUrl?: string;
  deliveryPhotosMetadata?: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}
export type DeliveryResponse = DeliveryDto;

export interface WarehouseDto {
  id: string | number;
  operatorId: string | number;
  operatorName: string;
  warehouseName: string;
  ownerName: string;
  contactNumber: string;
  state: string;
  district: string;
  address: string;
  totalCapacityQuintals: number;
  availableCapacityQuintals: number;
  supportedCrops: string[] | any;
  storageChargePerQuintalMonth?: number;
  storageChargePerQuintalPerMonth?: number;
  isAccredited?: boolean;
  accreditationStatus?: string;
  description?: string;
  createdAt: string;
}
export type WarehouseResponse = WarehouseDto;

export interface WarehouseBookingDto {
  id: string | number;
  warehouseId: string | number;
  warehouseName: string;
  warehouseDistrict: string;
  warehouseAddress: string;
  farmerId: string | number;
  farmerName: string;
  farmerMobile: string;
  crop: string;
  quantityQuintals: number;
  startDate: string;
  endDate: string;
  totalEstimatedCost?: number;
  totalCost?: number;
  status: WarehouseBookingStatus;
  remarks?: string;
  createdAt: string;
  updatedAt: string;
}
export type WarehouseBookingResponse = WarehouseBookingDto;

export interface DisputeDto {
  id: string | number;
  contractId: string | number;
  contractNumber: string;
  crop: string;
  contractAmount: number;
  raisedByUserId: string | number;
  raisedByName?: string;
  raisedByUserName?: string;
  raisedByUserRole?: string;
  reason: string;
  description: string;
  evidenceFileUrl?: string;
  requestedResolution: string;
  status: DisputeStatus;
  resolutionNotes?: string;
  resolvedAt?: string;
  resolvedByAdminId?: string | number;
  resolvedByAdminName?: string;
  evidenceFiles?: string[];
  createdAt: string;
  updatedAt: string;
}
export type DisputeResponse = DisputeDto;

export interface BenchmarkDto {
  id?: string | number;
  crop: string;
  district: string;
  state: string;
  currentMandiPrice?: number;
  minPrice: number;
  maxPrice: number;
  modalPrice: number;
  msp: number;
  dataSource?: string;
  source?: string;
  priceDate: string;
  isPrototypeBenchmark?: boolean;
}
export type PriceBenchmarkResponse = BenchmarkDto;

export interface PriceTrendPoint {
  date: string;
  modalPrice: number;
  minPrice: number;
  maxPrice: number;
  msp: number;
}

export interface PriceTrendDto {
  crop: string;
  district: string;
  currentPrice: number;
  msp: number;
  history30Days: PriceTrendPoint[];
  indicativeMinPrice: number;
  indicativeMaxPrice: number;
  recommendationNote: string;
}

export interface MspDto {
  crop: string;
  marketingYear: string;
  mspPrice: number;
  bonus?: number;
  effectiveDate: string;
}

export interface NotificationDto {
  id: string | number;
  title: string;
  message: string;
  notificationType: NotificationType;
  isRead: boolean;
  referenceId?: string | number;
  referenceType?: string;
  createdAt: string;
}

export interface AuditLogDto {
  id: string | number;
  userId?: string | number;
  userFullName?: string;
  userMobile?: string;
  userRole?: string;
  action: string;
  entityType: string;
  entityId?: string | number;
  details?: string;
  ipAddress?: string;
  createdAt: string;
}

export interface AdminDashboardDto {
  totalUsers: number;
  totalFarmers: number;
  totalBuyers: number;
  totalOperators: number;
  pendingVerifications: number;
  totalLots: number;
  openLots: number;
  totalBids: number;
  totalContracts: number;
  activeDisputes: number;
  totalWarehouses: number;
  totalEscrowHeld: number;
  totalReleasedAmount: number;
  lotsByCrop: Record<string, number>;
  contractsByStatus: Record<string, number>;
}

export type WalletTransactionType =
  | 'DEPOSIT'
  | 'WITHDRAWAL'
  | 'TOKEN_PURCHASE'
  | 'TOKEN_REDEEM_TO_BANK'
  | 'BID_LOCK'
  | 'BID_UNLOCK'
  | 'ESCROW_LOCK'
  | 'ESCROW_RELEASE'
  | 'ESCROW_PAYOUT'
  | 'ENWR_PLEDGE_ADVANCE'
  | 'REFUND'
  | 'DEMO_CREDIT';

export interface WalletTransaction {
  id: string;
  type: WalletTransactionType;
  amount: number;
  balanceAfter: number;
  referenceId: string;
  tokenCode?: string;
  paymentMethod?: string;
  description?: string;
  bankAccountNumber?: string;
  ifscCode?: string;
  upiId?: string;
  status: 'COMPLETED' | 'PENDING' | 'FAILED' | 'REVERSED';
  createdAt: string;
}

export interface WalletResponse {
  id: string;
  userId: string;
  userName: string;
  userRole: string;
  walletNumber: string;
  availableBalance: number;
  lockedBalance: number;
  totalBalance: number;
  totalDeposited: number;
  totalWithdrawn: number;
  activeTokenBatch?: string;
  updatedAt: string;
}

// ================= SMART SELL-OR-STORE ADVISOR TYPES =================
export type AdvisorRecommendation = 'SELL_NOW' | 'STORE' | 'WAIT_AND_MONITOR';
export type ConfidenceLevel = 'LOW' | 'MEDIUM' | 'HIGH';

export interface StorageAdviceDto {
  id: string;
  lotId: string;
  lotNumber: string;
  crop: string;
  variety: string;
  district: string;
  quantityQuintals: number;
  recommendation: AdvisorRecommendation;
  currentBestBidPrice: number;
  currentMandiModalPrice: number;
  mspPrice: number;
  estimatedFuturePrice: number;
  monthlyStorageCost: number;
  handlingCost: number;
  totalStorageAndHandlingCost: number;
  netExpectedGainPerQuintal: number;
  totalEstimatedBenefit: number;
  confidence: ConfidenceLevel;
  reasons: string[];
  recommendedHoldingDays: number;
  disclaimer: string;
  createdAt: string;
}
export type StorageAdviceResponse = StorageAdviceDto;

export interface PriceForecastDto {
  id: string;
  crop: string;
  district: string;
  forecastDate: string;
  currentPrice: number;
  movingAverage7Day: number;
  trendSlope: number;
  forecastPrice30Day: number;
  forecastPrice60Day: number;
  forecastConfidence: string;
  rationale: string;
  createdAt: string;
}
export type PriceForecastResponse = PriceForecastDto;

export interface PriceAlertDto {
  id: string;
  userId: string;
  crop: string;
  district: string;
  targetPrice: number;
  condition: 'EQUAL_OR_ABOVE' | 'BELOW';
  isActive: boolean;
  isTriggered: boolean;
  triggeredAt?: string;
  notificationChannel: 'IN_APP' | 'SMS' | 'ALL';
  notes?: string;
  createdAt: string;
}
export type PriceAlertResponse = PriceAlertDto;

export interface PriceAlertCreateDto {
  crop: string;
  district: string;
  targetPrice: number;
  condition?: 'EQUAL_OR_ABOVE' | 'BELOW';
  notificationChannel?: 'IN_APP' | 'SMS' | 'ALL';
  notes?: string;
}

export interface PriceAlertUpdateDto {
  targetPrice?: number;
  condition?: 'EQUAL_OR_ABOVE' | 'BELOW';
  notificationChannel?: 'IN_APP' | 'SMS' | 'ALL';
  notes?: string;
  isActive?: boolean;
}


