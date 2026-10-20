package com.sam.marketplace.warehouse;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/farmer/bookings")
@PreAuthorize("hasRole('FARMER')")
@Tag(name = "Farmer Warehouse Bookings", description = "Farmer warehouse storage bookings")
public class FarmerWarehouseBookingController {

    private final WarehouseService warehouseService;

    public FarmerWarehouseBookingController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    @Operation(summary = "Get all storage bookings requested by current farmer")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDto>>> getMyBookings(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        PageResponse<BookingResponseDto> bookings = warehouseService.getFarmerBookings(farmerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings, "Farmer warehouse bookings retrieved"));
    }
}
