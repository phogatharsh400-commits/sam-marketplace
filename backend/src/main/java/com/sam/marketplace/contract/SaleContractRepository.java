package com.sam.marketplace.contract;

import com.sam.marketplace.common.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaleContractRepository extends JpaRepository<SaleContract, UUID> {
    Optional<SaleContract> findByContractNumber(String contractNumber);
    Optional<SaleContract> findByLotId(UUID lotId);
    Page<SaleContract> findByFarmerIdOrderByCreatedAtDesc(UUID farmerId, Pageable pageable);
    Page<SaleContract> findByBuyerIdOrderByCreatedAtDesc(UUID buyerId, Pageable pageable);
    Page<SaleContract> findByStatusOrderByCreatedAtDesc(ContractStatus status, Pageable pageable);
    long countByStatus(ContractStatus status);
}
