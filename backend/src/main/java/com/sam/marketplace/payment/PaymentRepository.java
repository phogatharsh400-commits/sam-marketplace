package com.sam.marketplace.payment;

import com.sam.marketplace.common.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentTransaction, UUID> {
    Optional<PaymentTransaction> findByTransactionReference(String ref);
    Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);
    List<PaymentTransaction> findByContractId(UUID contractId);
    Page<PaymentTransaction> findByPayerIdOrderByCreatedAtDesc(UUID payerId, Pageable pageable);
    Page<PaymentTransaction> findByPayeeIdOrderByCreatedAtDesc(UUID payeeId, Pageable pageable);
    Page<PaymentTransaction> findByStatusOrderByCreatedAtDesc(PaymentStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentTransaction p WHERE p.status = 'HELD_IN_ESCROW'")
    BigDecimal getTotalHeldInEscrow();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentTransaction p WHERE p.status = 'RELEASED'")
    BigDecimal getTotalReleasedAmount();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentTransaction p WHERE p.payee.id = :farmerId AND p.status = 'RELEASED'")
    BigDecimal getTotalFarmerEarnings(@Param("farmerId") UUID farmerId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentTransaction p WHERE p.payer.id = :buyerId AND (p.status = 'HELD_IN_ESCROW' OR p.status = 'RELEASED')")
    BigDecimal getTotalBuyerSpend(@Param("buyerId") UUID buyerId);
}
