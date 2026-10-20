package com.sam.marketplace.lot;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.LotStatus;
import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lots")
@Tag(name = "Produce Lots", description = "Agricultural Lots Marketplace")
public class LotController {

    private final LotService lotService;

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Create a new produce lot (Farmer only)")
    public ResponseEntity<ApiResponse<LotResponseDto>> createLot(@Valid @RequestBody LotCreateDto dto) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        LotResponseDto created = lotService.createLot(farmerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Produce lot created successfully"));
    }

    @GetMapping
    @Operation(summary = "Browse open produce lots in marketplace with filters")
    public ResponseEntity<ApiResponse<PageResponse<LotResponseDto>>> getOpenLots(
            @RequestParam(required = false) CropType crop,
            @RequestParam(required = false) String district,
            @PageableDefault(size = 12) Pageable pageable) {
        PageResponse<LotResponseDto> lots = lotService.getOpenLots(crop, district, pageable);
        return ResponseEntity.ok(ApiResponse.success(lots, "Marketplace lots retrieved successfully"));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all lots (any status)")
    public ResponseEntity<ApiResponse<PageResponse<LotResponseDto>>> getAllLots(
            @RequestParam(required = false) LotStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<LotResponseDto> lots = lotService.getAllLots(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(lots, "Lots retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lot details by ID")
    public ResponseEntity<ApiResponse<LotResponseDto>> getLotById(@PathVariable UUID id) {
        LotResponseDto lot = lotService.getLotById(id);
        return ResponseEntity.ok(ApiResponse.success(lot, "Lot details retrieved"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Update produce lot details")
    public ResponseEntity<ApiResponse<LotResponseDto>> updateLot(
            @PathVariable UUID id, @RequestBody LotUpdateDto dto) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        LotResponseDto updated = lotService.updateLot(farmerId, id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Lot updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Delete a DRAFT produce lot")
    public ResponseEntity<ApiResponse<Void>> deleteLot(@PathVariable UUID id) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        lotService.deleteLot(farmerId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Draft lot deleted successfully"));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Publish a draft lot to the marketplace")
    public ResponseEntity<ApiResponse<LotResponseDto>> publishLot(@PathVariable UUID id) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        LotResponseDto published = lotService.publishLot(farmerId, id);
        return ResponseEntity.ok(ApiResponse.success(published, "Lot published to marketplace successfully"));
    }

    @PutMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Withdraw an open lot from the marketplace")
    public ResponseEntity<ApiResponse<LotResponseDto>> withdrawLot(@PathVariable UUID id) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        LotResponseDto withdrawn = lotService.withdrawLot(farmerId, id);
        return ResponseEntity.ok(ApiResponse.success(withdrawn, "Lot withdrawn successfully"));
    }
}
