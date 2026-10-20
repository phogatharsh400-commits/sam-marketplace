package com.sam.marketplace.warehouse;

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
@RequestMapping("/api/warehouses")
@Tag(name = "Warehouses", description = "Warehouse Operations and Farmer Bookings")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('WAREHOUSE_OPERATOR')")
    @Operation(summary = "Register a new warehouse facility (Operator only)")
    public ResponseEntity<ApiResponse<WarehouseResponseDto>> createWarehouse(
            @Valid @RequestBody WarehouseCreateDto dto) {
        UUID operatorId = SecurityUtils.getCurrentUserId();
        WarehouseResponseDto response = warehouseService.createWarehouse(operatorId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Warehouse registered successfully"));
    }

    @GetMapping
    @Operation(summary = "Browse and search warehouses by district and crop")
    public ResponseEntity<ApiResponse<PageResponse<WarehouseResponseDto>>> searchWarehouses(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String crop,
            @PageableDefault(size = 12) Pageable pageable) {
        PageResponse<WarehouseResponseDto> response = warehouseService.searchWarehouses(district, crop, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Warehouses retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse facility details by ID")
    public ResponseEntity<ApiResponse<WarehouseResponseDto>> getWarehouse(@PathVariable UUID id) {
        WarehouseResponseDto response = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Warehouse details retrieved"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_OPERATOR')")
    @Operation(summary = "Update warehouse facility details (Operator only)")
    public ResponseEntity<ApiResponse<WarehouseResponseDto>> updateWarehouse(
            @PathVariable UUID id,
            @Valid @RequestBody WarehouseCreateDto dto) {
        UUID operatorId = SecurityUtils.getCurrentUserId();
        WarehouseResponseDto response = warehouseService.updateWarehouse(operatorId, id, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Warehouse updated successfully"));
    }

    @PostMapping("/{id}/bookings")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Request storage booking at warehouse (Farmer only)")
    public ResponseEntity<ApiResponse<BookingResponseDto>> createBooking(
            @PathVariable UUID id,
            @Valid @RequestBody BookingCreateDto dto) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        BookingResponseDto response = warehouseService.createBookingRequest(farmerId, id, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Warehouse storage booking requested successfully"));
    }
}
