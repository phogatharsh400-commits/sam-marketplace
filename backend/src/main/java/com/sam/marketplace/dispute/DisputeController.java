package com.sam.marketplace.dispute;

import com.sam.marketplace.common.enums.DisputeStatus;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Disputes", description = "Dispute Filing and Tracking")
public class DisputeController {

    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @PostMapping("/contracts/{contractId}/disputes")
    @Operation(summary = "Raise a dispute on a sale contract")
    public ResponseEntity<ApiResponse<DisputeResponseDto>> raiseDispute(
            @PathVariable UUID contractId,
            @Valid @RequestBody DisputeCreateDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        DisputeResponseDto response = disputeService.raiseDispute(userId, contractId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Dispute raised successfully. Admin has been notified."));
    }

    @GetMapping("/disputes")
    @Operation(summary = "Get user disputes or all disputes")
    public ResponseEntity<ApiResponse<PageResponse<DisputeResponseDto>>> getDisputes(
            @RequestParam(required = false) DisputeStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID userId = SecurityUtils.getCurrentUserId();
        if (SecurityUtils.hasRole(com.sam.marketplace.common.enums.RoleType.ADMIN)) {
            return ResponseEntity.ok(ApiResponse.success(disputeService.getAllDisputes(status, pageable), "Disputes retrieved"));
        } else {
            return ResponseEntity.ok(ApiResponse.success(disputeService.getDisputesByUser(userId, pageable), "User disputes retrieved"));
        }
    }

    @GetMapping("/disputes/{id}")
    @Operation(summary = "Get dispute details by ID")
    public ResponseEntity<ApiResponse<DisputeResponseDto>> getDisputeById(@PathVariable UUID id) {
        DisputeResponseDto response = disputeService.getDisputeById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Dispute details retrieved"));
    }

    @GetMapping("/contracts/{contractId}/disputes")
    @Operation(summary = "Get all disputes for a contract")
    public ResponseEntity<ApiResponse<List<DisputeResponseDto>>> getDisputesForContract(@PathVariable UUID contractId) {
        List<DisputeResponseDto> list = disputeService.getDisputesForContract(contractId);
        return ResponseEntity.ok(ApiResponse.success(list, "Contract disputes retrieved"));
    }
}
