package com.sam.marketplace.merit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeritRepository extends JpaRepository<MeritAuditTransaction, UUID> {
    List<MeritAuditTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<MeritAuditTransaction> findTop50ByOrderByCreatedAtDesc();
}
