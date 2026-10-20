package com.sam.marketplace.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID>, JpaSpecificationExecutor<Warehouse> {
    Page<Warehouse> findByOperatorIdOrderByCreatedAtDesc(UUID operatorId, Pageable pageable);

    @Query("SELECT w FROM Warehouse w WHERE (:district IS NULL OR LOWER(w.district) = LOWER(:district)) " +
           "AND (:crop IS NULL OR LOWER(w.supportedCrops) LIKE LOWER(CONCAT('%', :crop, '%'))) " +
           "ORDER BY w.availableCapacityQuintals DESC")
    Page<Warehouse> searchWarehouses(
            @Param("district") String district,
            @Param("crop") String crop,
            Pageable pageable);

    List<Warehouse> findByDistrictIgnoreCase(String district);

    long count();
}
