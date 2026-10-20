package com.sam.marketplace.advisor;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Smart Sell-or-Store Advisor", description = "Farmer crop decision support, price forecasting, and warehousing viability")
public class AdvisorController {

    private final AdvisorService advisorService;

    public AdvisorController(AdvisorService advisorService) {
        this.advisorService = advisorService;
    }

    @GetMapping("/lots/{lotId}/advice")
    @Operation(summary = "Get latest Sell-or-Store decision recommendation for a farmer produce lot")
    public ResponseEntity<ApiResponse<StorageAdviceDto>> getAdvice(@PathVariable UUID lotId) {
        StorageAdviceDto advice = advisorService.getOrGenerateAdvice(lotId);
        return ResponseEntity.ok(ApiResponse.success(advice, "Sell-or-Store advice retrieved successfully"));
    }

    @PostMapping("/lots/{lotId}/advice/generate")
    @Operation(summary = "Force generate / recalculate fresh Sell-or-Store advice comparing live bids and storage costs")
    public ResponseEntity<ApiResponse<StorageAdviceDto>> generateFreshAdvice(@PathVariable UUID lotId) {
        StorageAdviceDto advice = advisorService.generateAdvice(lotId);
        return ResponseEntity.ok(ApiResponse.success(advice, "Fresh Sell-or-Store advice generated successfully"));
    }

    @GetMapping("/forecasts/{crop}/{district}")
    @Operation(summary = "Get 7-day Moving Average & Trend-based Price Forecast for a crop and district")
    public ResponseEntity<ApiResponse<PriceForecastDto>> getPriceForecast(
            @PathVariable String crop,
            @PathVariable String district) {
        CropType cropType;
        try {
            cropType = CropType.valueOf(crop.toUpperCase());
        } catch (Exception e) {
            cropType = CropType.PADDY;
        }
        PriceForecastDto forecast = advisorService.getForecastDto(cropType, district);
        return ResponseEntity.ok(ApiResponse.success(forecast, "Price forecast retrieved successfully"));
    }
}
