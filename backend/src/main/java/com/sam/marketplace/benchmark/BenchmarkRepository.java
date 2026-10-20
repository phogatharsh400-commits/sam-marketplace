package com.sam.marketplace.benchmark;

import com.sam.marketplace.common.enums.CropType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BenchmarkRepository extends JpaRepository<MandiPriceBenchmark, UUID> {
    
    Optional<MandiPriceBenchmark> findTopByCropAndDistrictOrderByPriceDateDesc(CropType crop, String district);
    
    List<MandiPriceBenchmark> findByCropAndDistrictAndPriceDateBetweenOrderByPriceDateAsc(
            CropType crop, String district, LocalDate startDate, LocalDate endDate);
    
    List<MandiPriceBenchmark> findByCropAndPriceDateBetweenOrderByPriceDateAsc(
            CropType crop, LocalDate startDate, LocalDate endDate);
            
    @Query("SELECT DISTINCT b.district FROM MandiPriceBenchmark b ORDER BY b.district")
    List<String> findDistinctDistricts();

    List<MandiPriceBenchmark> findByCropOrderByPriceDateDesc(CropType crop);

    Optional<MandiPriceBenchmark> findByCropAndDistrictAndPriceDate(CropType crop, String district, LocalDate priceDate);
}
