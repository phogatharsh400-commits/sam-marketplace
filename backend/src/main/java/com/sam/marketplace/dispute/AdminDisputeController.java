package com.sam.marketplace.dispute;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/disputes")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Disputes", description = "Admin Dispute Review, Adjudication and Resolution")
public class AdminDisputeController {

    private final DisputeService disputeService;

    public AdminDisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @PutMapping("/{id}/start-review")
    @Operation(summary = "Start administrative review on dispute")
    public ResponseEntity<ApiResponse<DisputeResponseDto>> startReview(@PathVariable UUID id) {
        UUID adminId = SecurityUtils.getCurrentUserId();
        DisputeResponseDto response = disputeService.startReview(adminId, id);
        return ResponseEntity.ok(ApiResponse.success(response, "Dispute moved to UNDER_REVIEW"));
    }

    @PutMapping("/{id}/resolve")
    @Operation(summary = "Resolve dispute (Release to farmer / Refund buyer / Partial)")
    public ResponseEntity<ApiResponse<DisputeResponseDto>> resolveDispute(
            @PathVariable UUID id,
            @Valid @RequestBody DisputeResolveDto dto) {
        UUID adminId = SecurityUtils.getCurrentUserId();
        DisputeResponseDto response = disputeService.resolveDispute(adminId, id, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Dispute resolved successfully"));
    }
}
