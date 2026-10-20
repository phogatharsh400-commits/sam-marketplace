package com.sam.marketplace.dispute;

import com.sam.marketplace.common.enums.DisputeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
    List<Dispute> findByContractId(UUID contractId);
    Page<Dispute> findByStatusOrderByCreatedAtDesc(DisputeStatus status, Pageable pageable);
    Page<Dispute> findByRaisedByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByStatus(DisputeStatus status);
}
