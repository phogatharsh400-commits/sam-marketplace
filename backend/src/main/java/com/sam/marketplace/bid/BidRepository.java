package com.sam.marketplace.bid;

import com.sam.marketplace.common.enums.BidStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {

    List<Bid> findByLotIdOrderByBidPricePerQuintalDesc(UUID lotId);

    Page<Bid> findByLotIdOrderByBidPricePerQuintalDesc(UUID lotId, Pageable pageable);

    Page<Bid> findByBuyerIdOrderByCreatedAtDesc(UUID buyerId, Pageable pageable);

    @Query("SELECT b FROM Bid b WHERE b.lot.farmer.id = :farmerId ORDER BY b.createdAt DESC")
    Page<Bid> findByFarmerIdOrderByCreatedAtDesc(@Param("farmerId") UUID farmerId, Pageable pageable);

    long countByLotId(UUID lotId);

    @Query("SELECT COALESCE(MAX(b.bidPricePerQuintal), 0) FROM Bid b WHERE b.lot.id = :lotId AND b.status = 'ACTIVE'")
    BigDecimal findHighestBidPriceForLot(@Param("lotId") UUID lotId);

    List<Bid> findByLotIdAndStatus(UUID lotId, BidStatus status);
}
