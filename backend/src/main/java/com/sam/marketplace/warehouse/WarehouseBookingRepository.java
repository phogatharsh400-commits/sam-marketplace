package com.sam.marketplace.warehouse;

import com.sam.marketplace.common.enums.WarehouseBookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WarehouseBookingRepository extends JpaRepository<WarehouseBooking, UUID> {
    Page<WarehouseBooking> findByFarmerIdOrderByCreatedAtDesc(UUID farmerId, Pageable pageable);

    @Query("SELECT b FROM WarehouseBooking b WHERE b.warehouse.operator.id = :operatorId ORDER BY b.createdAt DESC")
    Page<WarehouseBooking> findByOperatorIdOrderByCreatedAtDesc(@Param("operatorId") UUID operatorId, Pageable pageable);

    long countByStatus(WarehouseBookingStatus status);
}
