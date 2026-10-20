package com.sam.marketplace.advisor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StorageAdviceRepository extends JpaRepository<StorageAdvice, UUID> {
    Optional<StorageAdvice> findTopByLotIdOrderByCreatedAtDesc(UUID lotId);
    List<StorageAdvice> findByFarmerIdOrderByCreatedAtDesc(UUID farmerId);
}
