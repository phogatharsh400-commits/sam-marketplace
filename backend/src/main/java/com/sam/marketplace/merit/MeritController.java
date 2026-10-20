package com.sam.marketplace.merit;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/merit")
@Tag(name = "Merit & Trust System", description = "Participant Reputation, Merit Points, and Penalty Auditing")
public class MeritController {

    private final MeritService meritService;

    public MeritController(MeritService meritService) {
        this.meritService = meritService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user's merit score and audit history")
    public ResponseEntity<ApiResponse<MeritDto>> getMyMeritProfile(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        MeritDto dto = meritService.getUserMeritProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(dto, "Merit profile retrieved"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get public merit score & trust tier for a farmer, warehouse or buyer")
    public ResponseEntity<ApiResponse<MeritDto>> getUserMeritProfile(
            @PathVariable UUID userId) {
        MeritDto dto = meritService.getUserMeritProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(dto, "User merit profile retrieved"));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get recent merit audit logs across platform (Admin only)")
    public ResponseEntity<ApiResponse<List<MeritAuditTransaction>>> getPlatformMeritAuditLogs() {
        return ResponseEntity.ok(ApiResponse.success(meritService.getRecentPlatformMeritAuditLogs(), "Merit audit logs retrieved"));
    }

    @PostMapping("/simulate-violation")
    @Operation(summary = "Simulate condition breach & merit deduction for testing")
    public ResponseEntity<ApiResponse<MeritDto>> simulateViolation(
            @RequestParam UUID userId,
            @RequestParam int deduction,
            @RequestParam String reason,
            @RequestParam(required = false) String referenceEntity) {
        MeritDto dto = meritService.deductMeritPoints(userId, deduction, reason, referenceEntity != null ? referenceEntity : "CONDITION_BREACH");
        return ResponseEntity.ok(ApiResponse.success(dto, "Merit points deducted successfully"));
    }

    @PostMapping("/simulate-reward")
    @Operation(summary = "Simulate condition fulfillment & merit reward for testing")
    public ResponseEntity<ApiResponse<MeritDto>> simulateReward(
            @RequestParam UUID userId,
            @RequestParam int bonus,
            @RequestParam String reason,
            @RequestParam(required = false) String referenceEntity) {
        MeritDto dto = meritService.awardMeritPoints(userId, bonus, reason, referenceEntity != null ? referenceEntity : "CONDITION_MET");
        return ResponseEntity.ok(ApiResponse.success(dto, "Merit points awarded successfully"));
    }
}
