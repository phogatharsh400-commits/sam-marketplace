package com.sam.marketplace.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseOperatorProfileRepository extends JpaRepository<WarehouseOperatorProfile, UUID> {
    Optional<WarehouseOperatorProfile> findByUserId(UUID userId);
}
