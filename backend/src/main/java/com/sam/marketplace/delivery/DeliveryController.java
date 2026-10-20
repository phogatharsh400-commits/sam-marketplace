package com.sam.marketplace.delivery;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Delivery", description = "Delivery Recording and Confirmation")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/contracts/{contractId}/delivery")
    @Operation(summary = "Record produce dispatch/delivery details")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> recordDelivery(
            @PathVariable UUID contractId,
            @Valid @RequestBody DeliveryCreateDto dto) {
        UUID actorId = SecurityUtils.getCurrentUserId();
        DeliveryResponseDto response = deliveryService.recordDelivery(actorId, contractId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Delivery details recorded successfully"));
    }

    @GetMapping("/contracts/{contractId}/delivery")
    @Operation(summary = "Get delivery details for contract")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> getDelivery(@PathVariable UUID contractId) {
        DeliveryResponseDto response = deliveryService.getDeliveryByContract(contractId);
        return ResponseEntity.ok(ApiResponse.success(response, "Delivery details retrieved"));
    }

    @PutMapping("/deliveries/{id}/confirm")
    @Operation(summary = "Buyer confirms or disputes produce delivery")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> confirmDelivery(
            @PathVariable UUID id,
            @RequestBody DeliveryConfirmDto dto) {
        UUID buyerId = SecurityUtils.getCurrentUserId();
        DeliveryResponseDto response = deliveryService.confirmDelivery(buyerId, id, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Delivery confirmation processed"));
    }
}
