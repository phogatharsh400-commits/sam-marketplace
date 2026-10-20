package com.sam.marketplace.advisor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.marketplace.benchmark.BenchmarkRepository;
import com.sam.marketplace.benchmark.MandiPriceBenchmark;
import com.sam.marketplace.bid.Bid;
import com.sam.marketplace.bid.BidRepository;
import com.sam.marketplace.common.enums.BidStatus;
import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.NotificationType;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.exception.UnauthorizedException;
import com.sam.marketplace.lot.LotRepository;
import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import com.sam.marketplace.warehouse.Warehouse;
import com.sam.marketplace.warehouse.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class AdvisorService {

    private static final Logger log = LoggerFactory.getLogger(AdvisorService.class);

    private final StorageAdviceRepository storageAdviceRepository;
    private final PriceForecastRepository priceForecastRepository;
    private final PriceAlertRepository priceAlertRepository;
    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final BenchmarkRepository benchmarkRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public AdvisorService(StorageAdviceRepository storageAdviceRepository,
                          PriceForecastRepository priceForecastRepository,
                          PriceAlertRepository priceAlertRepository,
                          LotRepository lotRepository,
                          BidRepository bidRepository,
                          BenchmarkRepository benchmarkRepository,
                          WarehouseRepository warehouseRepository,
                          UserRepository userRepository,
                          NotificationService notificationService,
                          ObjectMapper objectMapper) {
        this.storageAdviceRepository = storageAdviceRepository;
        this.priceForecastRepository = priceForecastRepository;
        this.priceAlertRepository = priceAlertRepository;
        this.lotRepository = lotRepository;
        this.bidRepository = bidRepository;
        this.benchmarkRepository = benchmarkRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public StorageAdviceDto getOrGenerateAdvice(UUID lotId) {
        Optional<StorageAdvice> existing = storageAdviceRepository.findTopByLotIdOrderByCreatedAtDesc(lotId);
        if (existing.isPresent()) {
            StorageAdvice advice = existing.get();
            // If created within last 12 hours, return cached
            if (advice.getCreatedAt().isAfter(Instant.now().minusSeconds(12 * 3600))) {
                return StorageAdviceDto.from(advice, parseReasons(advice.getReasonsJson()));
            }
        }
        return generateAdvice(lotId);
    }

    @Transactional
    public StorageAdviceDto generateAdvice(UUID lotId) {
        ProduceLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Produce lot not found: " + lotId));

        CropType crop = lot.getCrop();
        String district = lot.getDistrict();
        BigDecimal quantity = lot.getQuantityQuintals();

        // 1. Current best buyer bid
        List<Bid> bids = bidRepository.findByLotIdOrderByBidPricePerQuintalDesc(lotId);
        BigDecimal currentBestBid = bids.stream()
                .filter(b -> b.getStatus() == BidStatus.ACTIVE || b.getStatus() == BidStatus.ACCEPTED)
                .map(Bid::getBidPricePerQuintal)
                .findFirst()
                .orElse(lot.getMinPricePerQuintal());

        // 2. Mandi Benchmark & MSP
        Optional<MandiPriceBenchmark> benchOpt = benchmarkRepository.findTopByCropAndDistrictOrderByPriceDateDesc(crop, district);
        BigDecimal mandiModal = benchOpt.map(MandiPriceBenchmark::getModalPrice).orElse(lot.getMinPricePerQuintal());
        BigDecimal msp = benchOpt.map(MandiPriceBenchmark::getMsp).orElse(BigDecimal.valueOf(2275.00));

        // 3. Simple 7-Day Moving Average & Linear Trend Forecast
        PriceForecast forecast = computeOrGetForecast(crop, district, mandiModal);
        BigDecimal future30Price = forecast.getForecastPrice30Day();
        BigDecimal trendSlope = forecast.getTrendSlope();

        // 4. Warehouse & Handling Costs
        BigDecimal monthlyStorage = getAverageStorageRate(district);
        BigDecimal handlingCost = BigDecimal.valueOf(15.00);
        BigDecimal totalStorage30Days = monthlyStorage.add(handlingCost);

        // 5. Compare Expected Net Gains
        // Max immediate price = max of best bid and current mandi modal
        BigDecimal currentMaxImmediate = currentBestBid.max(mandiModal);
        
        // Net gain if stored for 30 days
        BigDecimal grossFutureDiff = future30Price.subtract(currentMaxImmediate);
        BigDecimal netExpectedGain = grossFutureDiff.subtract(totalStorage30Days);
        BigDecimal totalBenefit = netExpectedGain.multiply(quantity).setScale(2, RoundingMode.HALF_UP);

        String recommendation;
        String confidence;
        List<String> reasons = new ArrayList<>();
        int holdingDays = 30;

        // Recommendation Logic
        if (netExpectedGain.compareTo(BigDecimal.valueOf(50.00)) >= 0 && trendSlope.compareTo(BigDecimal.ZERO) >= 0) {
            recommendation = "STORE";
            confidence = netExpectedGain.compareTo(BigDecimal.valueOf(100.00)) >= 0 ? "HIGH" : "MEDIUM";
            holdingDays = 35;
            reasons.add("Projected 30-day future APMC price (₹" + future30Price + "/qtl) exceeds warehouse storage and handling fees (₹" + totalStorage30Days + "/qtl) by ₹" + netExpectedGain + "/qtl.");
            reasons.add("Upward 7-day price trajectory (+₹" + trendSlope.abs() + "/qtl/day) indicates post-harvest supply tightening.");
            reasons.add("Farmer can leverage 70% instant e-NWR pledge liquidity (~₹" + quantity.multiply(currentMaxImmediate).multiply(BigDecimal.valueOf(0.70)).setScale(0, RoundingMode.HALF_UP) + ") while crops gain value in cold storage.");
        } else if (currentBestBid.compareTo(future30Price) >= 0 || trendSlope.compareTo(BigDecimal.valueOf(-0.50)) < 0 || netExpectedGain.compareTo(BigDecimal.ZERO) <= 0) {
            recommendation = "SELL_NOW";
            confidence = currentBestBid.compareTo(mandiModal) >= 0 ? "HIGH" : "MEDIUM";
            holdingDays = 0;
            totalBenefit = (currentBestBid.subtract(msp)).max(BigDecimal.ZERO).multiply(quantity).setScale(2, RoundingMode.HALF_UP);
            reasons.add("Current best offer (₹" + currentBestBid + "/qtl) secures immediate peak value with zero holding risks.");
            reasons.add("Storing would incur ₹" + totalStorage30Days + "/qtl in fees with negative/marginal projected price appreciation.");
            reasons.add("Instant simulated escrow settlement upon dispatch, avoiding storage shrinkage or moisture degradation.");
        } else {
            recommendation = "WAIT_AND_MONITOR";
            confidence = "LOW";
            holdingDays = 5;
            totalBenefit = BigDecimal.valueOf(25.00).multiply(quantity).setScale(2, RoundingMode.HALF_UP);
            reasons.add("Mandi modal rates are stabilizing around ₹" + mandiModal + "/qtl with narrow off-season premium.");
            reasons.add("Net storage upside (₹" + netExpectedGain + "/qtl) is currently marginal after deducting transport and warehouse rents.");
            reasons.add("Recommended to monitor buyer bids for 3–5 days before committing to warehouse storage.");
        }

        String reasonsJson = writeReasons(reasons);

        StorageAdvice advice = StorageAdvice.builder()
                .lot(lot)
                .farmer(lot.getFarmer())
                .recommendation(recommendation)
                .currentBestBidPrice(currentBestBid)
                .currentMandiModalPrice(mandiModal)
                .mspPrice(msp)
                .estimatedFuturePrice(future30Price)
                .monthlyStorageCost(monthlyStorage)
                .handlingCost(handlingCost)
                .netExpectedGainPerQuintal(netExpectedGain)
                .totalEstimatedBenefit(totalBenefit)
                .confidence(confidence)
                .reasonsJson(reasonsJson)
                .recommendedHoldingDays(holdingDays)
                .build();

        StorageAdvice saved = storageAdviceRepository.save(advice);
        return StorageAdviceDto.from(saved, reasons);
    }

    @Transactional
    public PriceForecastDto getForecastDto(CropType crop, String district) {
        PriceForecast forecast = computeOrGetForecast(crop, district, null);
        return PriceForecastDto.from(forecast);
    }

    private PriceForecast computeOrGetForecast(CropType crop, String district, BigDecimal fallbackPrice) {
        LocalDate today = LocalDate.now();
        Optional<PriceForecast> existing = priceForecastRepository.findByCropAndDistrictAndForecastDate(crop, district, today);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Compute 7-day Moving Average and slope
        LocalDate thirtyDaysAgo = today.minusDays(30);
        List<MandiPriceBenchmark> history = benchmarkRepository
                .findByCropAndDistrictAndPriceDateBetweenOrderByPriceDateAsc(crop, district, thirtyDaysAgo, today);

        BigDecimal currentPrice = fallbackPrice != null ? fallbackPrice : BigDecimal.valueOf(2450.00);
        BigDecimal ma7 = currentPrice;
        BigDecimal slope = BigDecimal.valueOf(3.50); // Default positive off-season velocity

        if (!history.isEmpty()) {
            currentPrice = history.get(history.size() - 1).getModalPrice();
            int n = Math.min(history.size(), 7);
            BigDecimal sum = BigDecimal.ZERO;
            for (int i = history.size() - n; i < history.size(); i++) {
                sum = sum.add(history.get(i).getModalPrice());
            }
            ma7 = sum.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);

            if (history.size() >= 7) {
                BigDecimal oldest7 = history.get(history.size() - 7).getModalPrice();
                slope = currentPrice.subtract(oldest7).divide(BigDecimal.valueOf(7), 2, RoundingMode.HALF_UP);
            }
        }

        // 30-Day and 60-Day Linear Trend Forecast
        BigDecimal forecast30 = ma7.add(slope.multiply(BigDecimal.valueOf(30))).setScale(2, RoundingMode.HALF_UP);
        BigDecimal forecast60 = ma7.add(slope.multiply(BigDecimal.valueOf(60))).setScale(2, RoundingMode.HALF_UP);

        // Cap within realistic bounds
        if (forecast30.compareTo(currentPrice.multiply(BigDecimal.valueOf(1.30))) > 0) {
            forecast30 = currentPrice.multiply(BigDecimal.valueOf(1.15)).setScale(2, RoundingMode.HALF_UP);
        }
        if (forecast60.compareTo(currentPrice.multiply(BigDecimal.valueOf(1.40))) > 0) {
            forecast60 = currentPrice.multiply(BigDecimal.valueOf(1.22)).setScale(2, RoundingMode.HALF_UP);
        }

        PriceForecast forecast = PriceForecast.builder()
                .crop(crop)
                .district(district)
                .forecastDate(today)
                .currentPrice(currentPrice)
                .movingAverage7Day(ma7)
                .trendSlope(slope)
                .forecastPrice30Day(forecast30)
                .forecastPrice60Day(forecast60)
                .forecastConfidence("MEDIUM")
                .rationale("Computed using 7-day moving average (" + ma7 + " ₹/qtl) + daily trend velocity (" + slope + " ₹/qtl/day) based on Agmarknet APMC arrivals.")
                .build();

        return priceForecastRepository.save(forecast);
    }

    private BigDecimal getAverageStorageRate(String district) {
        List<Warehouse> warehouses = warehouseRepository.findByDistrictIgnoreCase(district);
        if (warehouses.isEmpty()) {
            return BigDecimal.valueOf(20.00);
        }
        BigDecimal sum = warehouses.stream()
                .map(w -> w.getStorageChargePerQuintalMonth() != null ? w.getStorageChargePerQuintalMonth() : BigDecimal.valueOf(20.00))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(warehouses.size()), 2, RoundingMode.HALF_UP);
    }

    // ================= TARGET-PRICE ALERTS =================

    @Transactional
    public PriceAlertDto createPriceAlert(UUID userId, PriceAlertCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        CropType cropType;
        try {
            cropType = CropType.valueOf(dto.getCrop().toUpperCase());
        } catch (Exception e) {
            cropType = CropType.PADDY;
        }

        PriceAlert alert = PriceAlert.builder()
                .user(user)
                .crop(cropType)
                .district(dto.getDistrict())
                .targetPrice(dto.getTargetPrice())
                .condition(dto.getCondition() != null ? dto.getCondition() : "EQUAL_OR_ABOVE")
                .notificationChannel(dto.getNotificationChannel() != null ? dto.getNotificationChannel() : "IN_APP")
                .notes(dto.getNotes())
                .isActive(true)
                .isTriggered(false)
                .build();

        PriceAlert saved = priceAlertRepository.save(alert);
        return PriceAlertDto.from(saved);
    }

    @Transactional(readOnly = true)
    public List<PriceAlertDto> getMyAlerts(UUID userId) {
        return priceAlertRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(PriceAlertDto::from)
                .toList();
    }

    @Transactional
    public PriceAlertDto updatePriceAlert(UUID userId, UUID alertId, PriceAlertUpdateDto dto) {
        PriceAlert alert = priceAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Price alert not found: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this price alert");
        }

        if (dto.getTargetPrice() != null) alert.setTargetPrice(dto.getTargetPrice());
        if (dto.getCondition() != null) alert.setCondition(dto.getCondition());
        if (dto.getIsActive() != null) alert.setActive(dto.getIsActive());
        if (dto.getNotificationChannel() != null) alert.setNotificationChannel(dto.getNotificationChannel());
        if (dto.getNotes() != null) alert.setNotes(dto.getNotes());

        PriceAlert saved = priceAlertRepository.save(alert);
        return PriceAlertDto.from(saved);
    }

    @Transactional
    public void deletePriceAlert(UUID userId, UUID alertId) {
        PriceAlert alert = priceAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Price alert not found: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this price alert");
        }

        priceAlertRepository.delete(alert);
    }

    @Transactional
    public PriceAlertDto toggleAlert(UUID userId, UUID alertId) {
        PriceAlert alert = priceAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Price alert not found: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this price alert");
        }

        alert.setActive(!alert.isActive());
        PriceAlert saved = priceAlertRepository.save(alert);
        return PriceAlertDto.from(saved);
    }

    @Transactional
    public void checkAndTriggerAlerts(CropType crop, String district, BigDecimal newPrice) {
        List<PriceAlert> activeAlerts = priceAlertRepository.findByCropAndDistrictAndIsActiveTrueAndIsTriggeredFalse(crop, district);
        for (PriceAlert alert : activeAlerts) {
            boolean triggered = false;
            if ("EQUAL_OR_ABOVE".equalsIgnoreCase(alert.getCondition()) || "ABOVE".equalsIgnoreCase(alert.getCondition())) {
                if (newPrice.compareTo(alert.getTargetPrice()) >= 0) {
                    triggered = true;
                }
            } else if ("BELOW".equalsIgnoreCase(alert.getCondition())) {
                if (newPrice.compareTo(alert.getTargetPrice()) <= 0) {
                    triggered = true;
                }
            }

            if (triggered) {
                alert.setTriggered(true);
                alert.setTriggeredAt(Instant.now());
                priceAlertRepository.save(alert);

                notificationService.createNotification(
                        alert.getUser().getId(),
                        "🔔 Target Price Alert Reached!",
                        "Mandi price for " + crop.name() + " in " + district + " reached ₹" + newPrice + "/qtl (Target: ₹" + alert.getTargetPrice() + "/qtl). Check Sell-or-Store Advisor!",
                        NotificationType.SYSTEM_ALERT,
                        alert.getId().toString(),
                        "PRICE_ALERT"
                );
            }
        }
    }

    private List<String> parseReasons(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.singletonList(json);
        }
    }

    private String writeReasons(List<String> reasons) {
        try {
            return objectMapper.writeValueAsString(reasons);
        } catch (Exception e) {
            return "[]";
        }
    }
}
