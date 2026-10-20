package com.sam.marketplace.advisor;

import com.sam.marketplace.common.enums.CropType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, UUID> {
    List<PriceAlert> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<PriceAlert> findByIsActiveTrueAndIsTriggeredFalse();
    List<PriceAlert> findByCropAndDistrictAndIsActiveTrueAndIsTriggeredFalse(CropType crop, String district);
}
