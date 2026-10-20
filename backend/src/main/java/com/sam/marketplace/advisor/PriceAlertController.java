package com.sam.marketplace.advisor;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/price-alerts")
@Tag(name = "Target-Price Alerts", description = "Farmer and buyer target price alert management and notifications")
public class PriceAlertController {

    private final AdvisorService advisorService;

    public PriceAlertController(AdvisorService advisorService) {
        this.advisorService = advisorService;
    }

    @GetMapping
    @Operation(summary = "Get all target price alerts created by the currently authenticated user")
    public ResponseEntity<ApiResponse<List<PriceAlertDto>>> getMyAlerts() {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<PriceAlertDto> alerts = advisorService.getMyAlerts(userId);
        return ResponseEntity.ok(ApiResponse.success(alerts, "Price alerts retrieved successfully"));
    }

    @PostMapping
    @Operation(summary = "Create a new target-price alert for a crop and district")
    public ResponseEntity<ApiResponse<PriceAlertDto>> createAlert(@Valid @RequestBody PriceAlertCreateDto createDto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        PriceAlertDto alert = advisorService.createPriceAlert(userId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(alert, "Price alert created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing price alert")
    public ResponseEntity<ApiResponse<PriceAlertDto>> updateAlert(
            @PathVariable UUID id,
            @RequestBody PriceAlertUpdateDto updateDto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        PriceAlertDto alert = advisorService.updatePriceAlert(userId, id, updateDto);
        return ResponseEntity.ok(ApiResponse.success(alert, "Price alert updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a price alert")
    public ResponseEntity<ApiResponse<Void>> deleteAlert(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        advisorService.deletePriceAlert(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Price alert deleted successfully"));
    }

    @PostMapping("/{id}/toggle")
    @Operation(summary = "Toggle a price alert between active and inactive")
    public ResponseEntity<ApiResponse<PriceAlertDto>> toggleAlert(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        PriceAlertDto alert = advisorService.toggleAlert(userId, id);
        return ResponseEntity.ok(ApiResponse.success(alert, "Price alert state toggled successfully"));
    }
}
