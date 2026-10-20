package com.sam.marketplace.benchmark;

import com.sam.marketplace.common.enums.CropType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class BenchmarkService {

    private static final Logger log = LoggerFactory.getLogger(BenchmarkService.class);

    private final BenchmarkRepository benchmarkRepository;

    private static final Map<CropType, BigDecimal> MSP_MAP = Map.of(
            CropType.WHEAT, BigDecimal.valueOf(2275.00),
            CropType.PADDY, BigDecimal.valueOf(2183.00),
            CropType.MUSTARD, BigDecimal.valueOf(5650.00)
    );

    public BenchmarkService(BenchmarkRepository benchmarkRepository) {
        this.benchmarkRepository = benchmarkRepository;
    }

    @Transactional(readOnly = true)
    public BenchmarkDto getLatestBenchmark(CropType crop, String district) {
        return benchmarkRepository.findTopByCropAndDistrictOrderByPriceDateDesc(crop, district)
                .map(BenchmarkDto::from)
                .orElseGet(() -> getDefaultBenchmark(crop, district));
    }

    @Transactional(readOnly = true)
    public List<BenchmarkDto> getAllLatestBenchmarks() {
        List<MandiPriceBenchmark> all = benchmarkRepository.findAll();
        Map<String, MandiPriceBenchmark> latestMap = new HashMap<>();
        for (MandiPriceBenchmark b : all) {
            String key = b.getCrop() + "_" + b.getDistrict();
            if (!latestMap.containsKey(key) || b.getPriceDate().isAfter(latestMap.get(key).getPriceDate())) {
                latestMap.put(key, b);
            }
        }
        return latestMap.values().stream().map(BenchmarkDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<BenchmarkDto> getBenchmarksByCrop(CropType crop) {
        return benchmarkRepository.findByCropOrderByPriceDateDesc(crop).stream()
                .map(BenchmarkDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PriceTrendDto> get30DayTrend(CropType crop, String district) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        List<MandiPriceBenchmark> records;
        if (district != null && !district.isBlank()) {
            records = benchmarkRepository.findByCropAndDistrictAndPriceDateBetweenOrderByPriceDateAsc(
                    crop, district, startDate, endDate);
        } else {
            records = benchmarkRepository.findByCropAndPriceDateBetweenOrderByPriceDateAsc(
                    crop, startDate, endDate);
        }

        if (records.isEmpty()) {
            return generateSyntheticTrend(crop);
        }

        Map<LocalDate, PriceTrendDto> trendMap = new TreeMap<>();
        for (MandiPriceBenchmark b : records) {
            trendMap.put(b.getPriceDate(), PriceTrendDto.builder()
                    .date(b.getPriceDate())
                    .modalPrice(b.getModalPrice())
                    .minPrice(b.getMinPrice())
                    .maxPrice(b.getMaxPrice())
                    .msp(b.getMsp())
                    .build());
        }
        return new ArrayList<>(trendMap.values());
    }

    @Transactional(readOnly = true)
    public List<MspDto> getMspList() {
        return List.of(
                MspDto.builder().crop(CropType.WHEAT).mspPerQuintal(BigDecimal.valueOf(2275.00)).marketingSeason("Rabi 2024-25").source("Ministry of Agriculture & Farmers Welfare (Simulated)").build(),
                MspDto.builder().crop(CropType.PADDY).mspPerQuintal(BigDecimal.valueOf(2183.00)).marketingSeason("Kharif 2024-25").source("Ministry of Agriculture & Farmers Welfare (Simulated)").build(),
                MspDto.builder().crop(CropType.MUSTARD).mspPerQuintal(BigDecimal.valueOf(5650.00)).marketingSeason("Rabi 2024-25").source("Ministry of Agriculture & Farmers Welfare (Simulated)").build()
        );
    }

    @Transactional(readOnly = true)
    public List<String> getSupportedDistricts() {
        List<String> list = benchmarkRepository.findDistinctDistricts();
        if (list.isEmpty()) {
            return List.of("Karnal", "Hisar", "Ambala", "Rohtak", "Sirsa", "Panipat");
        }
        return list;
    }

    @Transactional
    public BenchmarkDto createOrUpdateBenchmark(BenchmarkDto dto) {
        CropType crop = dto.getCrop() != null ? dto.getCrop() : CropType.WHEAT;
        BigDecimal msp = dto.getMsp() != null ? dto.getMsp() : MSP_MAP.getOrDefault(crop, BigDecimal.valueOf(2275.00));
        BigDecimal modalPrice = dto.getModalPrice() != null ? dto.getModalPrice() : (dto.getCurrentMandiPrice() != null ? dto.getCurrentMandiPrice() : BigDecimal.valueOf(2400.00));
        BigDecimal currentMandiPrice = dto.getCurrentMandiPrice() != null ? dto.getCurrentMandiPrice() : modalPrice;
        BigDecimal minPrice = dto.getMinPrice() != null ? dto.getMinPrice() : modalPrice.multiply(BigDecimal.valueOf(0.95));
        BigDecimal maxPrice = dto.getMaxPrice() != null ? dto.getMaxPrice() : modalPrice.multiply(BigDecimal.valueOf(1.05));

        MandiPriceBenchmark entity = MandiPriceBenchmark.builder()
                .id(dto.getId())
                .crop(crop)
                .district(dto.getDistrict() != null ? dto.getDistrict() : "Karnal")
                .state(dto.getState() != null ? dto.getState() : "Haryana")
                .currentMandiPrice(currentMandiPrice)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .modalPrice(modalPrice)
                .msp(msp)
                .dataSource(dto.getDataSource() != null ? dto.getDataSource() : "Haryana State APMC Mandi Board")
                .priceDate(dto.getPriceDate() != null ? dto.getPriceDate() : LocalDate.now())
                .build();

        return BenchmarkDto.from(benchmarkRepository.save(entity));
    }

    private BenchmarkDto getDefaultBenchmark(CropType crop, String district) {
        BigDecimal msp = MSP_MAP.getOrDefault(crop, BigDecimal.valueOf(2200.00));
        BigDecimal base = switch (crop) {
            case WHEAT -> BigDecimal.valueOf(2400.00);
            case PADDY -> BigDecimal.valueOf(2350.00);
            case MUSTARD -> BigDecimal.valueOf(5900.00);
        };

        return BenchmarkDto.builder()
                .crop(crop)
                .district(district != null ? district : "Karnal")
                .state("Haryana")
                .currentMandiPrice(base)
                .minPrice(base.multiply(BigDecimal.valueOf(0.95)))
                .maxPrice(base.multiply(BigDecimal.valueOf(1.08)))
                .modalPrice(base)
                .msp(msp)
                .dataSource("Agmarknet Simulated Mandi API (Prototype Data)")
                .priceDate(LocalDate.now())
                .indicativeMinPrice(base.multiply(BigDecimal.valueOf(0.90)))
                .indicativeMaxPrice(base.multiply(BigDecimal.valueOf(1.10)))
                .build();
    }

    private List<PriceTrendDto> generateSyntheticTrend(CropType crop) {
        List<PriceTrendDto> list = new ArrayList<>();
        BigDecimal msp = MSP_MAP.getOrDefault(crop, BigDecimal.valueOf(2200.00));
        double basePrice = switch (crop) {
            case WHEAT -> 2380.0;
            case PADDY -> 2320.0;
            case MUSTARD -> 5850.0;
        };

        LocalDate today = LocalDate.now();
        for (int i = 30; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            double variation = Math.sin(i * 0.4) * (basePrice * 0.03);
            double modal = basePrice + variation;
            double min = modal * 0.96;
            double max = modal * 1.05;

            list.add(PriceTrendDto.builder()
                    .date(date)
                    .modalPrice(BigDecimal.valueOf(Math.round(modal * 100.0) / 100.0))
                    .minPrice(BigDecimal.valueOf(Math.round(min * 100.0) / 100.0))
                    .maxPrice(BigDecimal.valueOf(Math.round(max * 100.0) / 100.0))
                    .msp(msp)
                    .build());
        }
        return list;
    }
}
