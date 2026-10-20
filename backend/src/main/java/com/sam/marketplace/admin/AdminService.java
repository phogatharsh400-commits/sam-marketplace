package com.sam.marketplace.admin;

import com.sam.marketplace.bid.BidRepository;
import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.ContractStatus;
import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.DisputeStatus;
import com.sam.marketplace.common.enums.LotStatus;
import com.sam.marketplace.common.enums.RoleType;
import com.sam.marketplace.contract.SaleContractRepository;
import com.sam.marketplace.dispute.DisputeRepository;
import com.sam.marketplace.lot.LotRepository;
import com.sam.marketplace.payment.PaymentRepository;
import com.sam.marketplace.user.UserRepository;
import com.sam.marketplace.warehouse.WarehouseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final SaleContractRepository saleContractRepository;
    private final DisputeRepository disputeRepository;
    private final WarehouseRepository warehouseRepository;
    private final PaymentRepository paymentRepository;

    public AdminService(UserRepository userRepository,
                        LotRepository lotRepository,
                        BidRepository bidRepository,
                        SaleContractRepository saleContractRepository,
                        DisputeRepository disputeRepository,
                        WarehouseRepository warehouseRepository,
                        PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.lotRepository = lotRepository;
        this.bidRepository = bidRepository;
        this.saleContractRepository = saleContractRepository;
        this.disputeRepository = disputeRepository;
        this.warehouseRepository = warehouseRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalFarmers = userRepository.findByRole(RoleType.FARMER, PageRequest.of(0, 1)).getTotalElements();
        long totalBuyers = userRepository.findByRole(RoleType.BUYER, PageRequest.of(0, 1)).getTotalElements();
        long totalOperators = userRepository.findByRole(RoleType.WAREHOUSE_OPERATOR, PageRequest.of(0, 1)).getTotalElements();
        long pendingVerifications = userRepository.findByStatus(AccountStatus.PENDING_VERIFICATION, PageRequest.of(0, 1)).getTotalElements();

        long totalLots = lotRepository.count();
        long openLots = lotRepository.countByStatus(LotStatus.OPEN);
        long totalBids = bidRepository.count();
        long totalContracts = saleContractRepository.count();
        long activeDisputes = disputeRepository.countByStatus(DisputeStatus.OPEN) + disputeRepository.countByStatus(DisputeStatus.UNDER_REVIEW);
        long totalWarehouses = warehouseRepository.count();

        BigDecimal totalEscrowHeld = paymentRepository.getTotalHeldInEscrow();
        BigDecimal totalReleasedAmount = paymentRepository.getTotalReleasedAmount();

        Map<String, Long> lotsByCrop = new HashMap<>();
        for (CropType crop : CropType.values()) {
            lotsByCrop.put(crop.name(), lotRepository.countByCrop(crop));
        }

        Map<String, Long> contractsByStatus = new HashMap<>();
        for (ContractStatus status : ContractStatus.values()) {
            contractsByStatus.put(status.name(), saleContractRepository.countByStatus(status));
        }

        return AdminDashboardDto.builder()
                .totalUsers(totalUsers)
                .totalFarmers(totalFarmers)
                .totalBuyers(totalBuyers)
                .totalOperators(totalOperators)
                .pendingVerifications(pendingVerifications)
                .totalLots(totalLots)
                .openLots(openLots)
                .totalBids(totalBids)
                .totalContracts(totalContracts)
                .activeDisputes(activeDisputes)
                .totalWarehouses(totalWarehouses)
                .totalEscrowHeld(totalEscrowHeld)
                .totalReleasedAmount(totalReleasedAmount)
                .lotsByCrop(lotsByCrop)
                .contractsByStatus(contractsByStatus)
                .build();
    }
}
