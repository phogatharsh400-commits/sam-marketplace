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
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Warehouse Bookings Management", description = "Operator Bookings Approval and Tracking")
public class OperatorWarehouseBookingController {

    private final WarehouseService warehouseService;

    public OperatorWarehouseBookingController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/operator/bookings")
    @PreAuthorize("hasRole('WAREHOUSE_OPERATOR')")
    @Operation(summary = "Get all storage bookings for warehouses owned by current operator")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDto>>> getOperatorBookings(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID operatorId = SecurityUtils.getCurrentUserId();
        PageResponse<BookingResponseDto> bookings = warehouseService.getOperatorBookings(operatorId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings, "Operator warehouse bookings retrieved"));
    }

    @PutMapping("/bookings/{id}/approve")
    @PreAuthorize("hasRole('WAREHOUSE_OPERATOR')")
    @Operation(summary = "Approve warehouse storage booking request")
    public ResponseEntity<ApiResponse<BookingResponseDto>> approveBooking(@PathVariable UUID id) {
        UUID operatorId = SecurityUtils.getCurrentUserId();
        BookingResponseDto response = warehouseService.approveBooking(operatorId, id);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking approved and capacity updated"));
    }

    @PutMapping("/bookings/{id}/reject")
    @PreAuthorize("hasRole('WAREHOUSE_OPERATOR')")
    @Operation(summary = "Reject warehouse storage booking request")
    public ResponseEntity<ApiResponse<BookingResponseDto>> rejectBooking(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> payload) {
        UUID operatorId = SecurityUtils.getCurrentUserId();
        String reason = payload != null ? payload.get("reason") : "Capacity unavailable";
        BookingResponseDto response = warehouseService.rejectBooking(operatorId, id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking rejected"));
    }
}
