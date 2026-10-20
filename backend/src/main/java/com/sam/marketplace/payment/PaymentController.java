package com.sam.marketplace.payment;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Payments & Escrow", description = "Simulated Escrow Payment Lifecycle")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/contracts/{contractId}/payments/escrow")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Fund simulated escrow for contract (Buyer only)")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> fundEscrow(
            @PathVariable UUID contractId,
            @Valid @RequestBody PaymentInitiateDto dto) {
        UUID buyerId = SecurityUtils.getCurrentUserId();
        PaymentResponseDto response = paymentService.fundEscrow(buyerId, contractId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Escrow funding processed in prototype simulation mode"));
    }

    @GetMapping("/payments/{id}")
    @Operation(summary = "Get payment transaction details")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPayment(@PathVariable UUID id) {
        PaymentResponseDto response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment transaction details retrieved"));
    }

    @PostMapping("/payments/{id}/simulate-success")
    @Operation(summary = "Simulate payment gateway success callback")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> simulateSuccess(@PathVariable UUID id) {
        PaymentResponseDto response = paymentService.simulateSuccess(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment marked as SUCCESS (Held in Escrow)"));
    }

    @PostMapping("/payments/{id}/simulate-failure")
    @Operation(summary = "Simulate payment gateway failure callback")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> simulateFailure(@PathVariable UUID id) {
        PaymentResponseDto response = paymentService.simulateFailure(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment marked as FAILED"));
    }

    @PostMapping("/payments/{id}/release")
    @Operation(summary = "Release escrow funds to farmer (Admin / Post-Delivery)")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> releasePayment(@PathVariable UUID id) {
        UUID actorId = SecurityUtils.getCurrentUserId();
        PaymentResponseDto response = paymentService.releasePayment(id, actorId);
        return ResponseEntity.ok(ApiResponse.success(response, "Escrow funds released to farmer successfully"));
    }

    @PostMapping("/payments/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refund escrow funds to buyer (Admin only)")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> refundPayment(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> payload) {
        UUID adminId = SecurityUtils.getCurrentUserId();
        String reason = payload != null ? payload.get("reason") : "Administrative refund";
        PaymentResponseDto response = paymentService.refundPayment(id, adminId, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Escrow payment refunded to buyer successfully"));
    }
}
