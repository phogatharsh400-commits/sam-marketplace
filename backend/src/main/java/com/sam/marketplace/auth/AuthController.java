package com.sam.marketplace.auth;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User Registration, Login, OTP, and Token Lifecycle")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/farmer")
    @Operation(summary = "Register a new Farmer account")
    public ResponseEntity<ApiResponse<AuthResponse>> registerFarmer(@Valid @RequestBody RegisterFarmerRequest request) {
        AuthResponse response = authService.registerFarmer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Farmer registration initiated successfully. Pending admin verification."));
    }

    @PostMapping("/register/buyer")
    @Operation(summary = "Register a new Buyer account")
    public ResponseEntity<ApiResponse<AuthResponse>> registerBuyer(@Valid @RequestBody RegisterBuyerRequest request) {
        AuthResponse response = authService.registerBuyer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Buyer registration initiated successfully. Pending admin verification."));
    }

    @PostMapping("/register/operator")
    @Operation(summary = "Register a new Warehouse Operator account")
    public ResponseEntity<ApiResponse<AuthResponse>> registerOperator(@Valid @RequestBody RegisterOperatorRequest request) {
        AuthResponse response = authService.registerOperator(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Warehouse operator registration initiated successfully."));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with mobile number and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP (Prototype mock: default 123456)")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        boolean verified = authService.verifyMockOtp(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("verified", verified), "OTP verified successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Access token refreshed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate refresh tokens")
    public ResponseEntity<ApiResponse<Void>> logout() {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            authService.logout(userId);
        } catch (Exception ignored) {
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Reset password using mock OTP")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }
}
