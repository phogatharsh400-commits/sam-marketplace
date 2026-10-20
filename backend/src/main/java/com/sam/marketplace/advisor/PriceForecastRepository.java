package com.sam.marketplace.advisor;

import com.sam.marketplace.common.enums.CropType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceForecastRepository extends JpaRepository<PriceForecast, UUID> {
    Optional<PriceForecast> findTopByCropAndDistrictOrderByForecastDateDesc(CropType crop, String district);
    Optional<PriceForecast> findByCropAndDistrictAndForecastDate(CropType crop, String district, LocalDate forecastDate);
}
