package com.sam.marketplace.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryRecord, UUID> {
    Optional<DeliveryRecord> findByContractId(UUID contractId);
}
