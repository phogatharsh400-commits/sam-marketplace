package com.sam.marketplace.lot;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.LotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LotRepository extends JpaRepository<ProduceLot, UUID>, JpaSpecificationExecutor<ProduceLot> {
    
    Optional<ProduceLot> findByLotNumber(String lotNumber);
    
    Page<ProduceLot> findByFarmerIdOrderByCreatedAtDesc(UUID farmerId, Pageable pageable);
    
    Page<ProduceLot> findByStatusOrderByCreatedAtDesc(LotStatus status, Pageable pageable);
    
    @Query("SELECT l FROM ProduceLot l WHERE l.status = :status " +
           "AND (:crop IS NULL OR l.crop = :crop) " +
           "AND (:district IS NULL OR LOWER(l.district) = LOWER(:district)) " +
           "ORDER BY l.createdAt DESC")
    Page<ProduceLot> findOpenLots(
            @Param("status") LotStatus status,
            @Param("crop") CropType crop,
            @Param("district") String district,
            Pageable pageable);

    long countByStatus(LotStatus status);
    
    long countByCrop(CropType crop);
}
