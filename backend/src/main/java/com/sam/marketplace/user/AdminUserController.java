package com.sam.marketplace.user;

import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.RoleType;
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
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Users", description = "Administrator User Management and Verification")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users with role and status filtering")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getAllUsers(
            @RequestParam(required = false) RoleType role,
            @RequestParam(required = false) AccountStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<UserDto> users = userService.getAllUsers(role, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @PutMapping("/{id}/verify")
    @Operation(summary = "Verify a user account (FARMER / BUYER / WAREHOUSE_OPERATOR)")
    public ResponseEntity<ApiResponse<UserDto>> verifyUser(@PathVariable UUID id) {
        UUID adminId = SecurityUtils.getCurrentUserId();
        UserDto user = userService.verifyUser(adminId, id);
        return ResponseEntity.ok(ApiResponse.success(user, "User successfully verified and activated"));
    }

    @PutMapping("/{id}/suspend")
    @Operation(summary = "Suspend a user account")
    public ResponseEntity<ApiResponse<UserDto>> suspendUser(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> payload) {
        UUID adminId = SecurityUtils.getCurrentUserId();
        String reason = payload != null ? payload.get("reason") : "Administrative review";
        UserDto user = userService.suspendUser(adminId, id, reason);
        return ResponseEntity.ok(ApiResponse.success(user, "User successfully suspended"));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a user registration")
    public ResponseEntity<ApiResponse<UserDto>> rejectUser(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> payload) {
        UUID adminId = SecurityUtils.getCurrentUserId();
        String reason = payload != null ? payload.get("reason") : "Documentation invalid";
        UserDto user = userService.rejectUser(adminId, id, reason);
        return ResponseEntity.ok(ApiResponse.success(user, "User registration rejected"));
    }
}
