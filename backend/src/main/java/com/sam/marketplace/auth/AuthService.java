package com.sam.marketplace.auth;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.AuditAction;
import com.sam.marketplace.common.enums.NotificationType;
import com.sam.marketplace.common.enums.RoleType;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.ConflictException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.exception.UnauthorizedException;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.security.JwtTokenProvider;
import com.sam.marketplace.security.UserPrincipal;
import com.sam.marketplace.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final WarehouseOperatorProfileRepository warehouseOperatorProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Value("${app.prototype.default-mock-otp:123456}")
    private String defaultMockOtp;

    public AuthService(UserRepository userRepository,
                       FarmerProfileRepository farmerProfileRepository,
                       BuyerProfileRepository buyerProfileRepository,
                       WarehouseOperatorProfileRepository warehouseOperatorProfileRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       NotificationService notificationService,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.warehouseOperatorProfileRepository = warehouseOperatorProfileRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public AuthResponse registerFarmer(RegisterFarmerRequest request) {
        validateUniqueMobileAndEmail(request.getMobileNumber(), request.getEmail());

        User user = User.builder()
                .fullName(request.getFullName())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.FARMER)
                .status(AccountStatus.PENDING_VERIFICATION)
                .isMobileVerified(true) // Verified via prototype mock OTP
                .build();

        User savedUser = userRepository.save(user);

        FarmerProfile profile = FarmerProfile.builder()
                .user(savedUser)
                .state(request.getState())
                .district(request.getDistrict())
                .village(request.getVillage())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankIfsc(request.getBankIfsc())
                .preferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "en")
                .landAreaAcres(request.getLandAreaAcres())
                .build();

        farmerProfileRepository.save(profile);

        auditService.logAction(savedUser.getId(), AuditAction.USER_REGISTER, "USER",
                savedUser.getId().toString(), "Farmer registered: " + savedUser.getMobileNumber(), null);

        notificationService.createNotification(savedUser.getId(),
                "Registration Received",
                "Welcome to SAM! Your account has been registered and is pending verification by the administrator.",
                NotificationType.USER_VERIFICATION, savedUser.getId().toString(), "USER");

        return generateAuthResponse(savedUser, "Registration successful. Mock OTP auto-verified for prototype demonstration.");
    }

    @Transactional
    public AuthResponse registerBuyer(RegisterBuyerRequest request) {
        validateUniqueMobileAndEmail(request.getMobileNumber(), request.getEmail());

        User user = User.builder()
                .fullName(request.getFullName())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.BUYER)
                .status(AccountStatus.PENDING_VERIFICATION)
                .isMobileVerified(true)
                .build();

        User savedUser = userRepository.save(user);

        BuyerProfile profile = BuyerProfile.builder()
                .user(savedUser)
                .businessName(request.getBusinessName())
                .gstin(request.getGstin())
                .panNumber(request.getPanNumber())
                .businessAddress(request.getBusinessAddress())
                .state(request.getState())
                .district(request.getDistrict())
                .buyerType(request.getBuyerType() != null ? request.getBuyerType() : "TRADER")
                .build();

        buyerProfileRepository.save(profile);

        auditService.logAction(savedUser.getId(), AuditAction.USER_REGISTER, "USER",
                savedUser.getId().toString(), "Buyer registered: " + savedUser.getMobileNumber(), null);

        notificationService.createNotification(savedUser.getId(),
                "Registration Received",
                "Welcome to SAM! Your business account is pending admin verification before bidding is enabled.",
                NotificationType.USER_VERIFICATION, savedUser.getId().toString(), "USER");

        return generateAuthResponse(savedUser, "Buyer registration successful. Pending verification.");
    }

    @Transactional
    public AuthResponse registerOperator(RegisterOperatorRequest request) {
        validateUniqueMobileAndEmail(request.getMobileNumber(), request.getEmail());

        User user = User.builder()
                .fullName(request.getFullName())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.WAREHOUSE_OPERATOR)
                .status(AccountStatus.PENDING_VERIFICATION)
                .isMobileVerified(true)
                .build();

        User savedUser = userRepository.save(user);

        WarehouseOperatorProfile profile = WarehouseOperatorProfile.builder()
                .user(savedUser)
                .businessName(request.getBusinessName())
                .licenseNumber(request.getLicenseNumber())
                .state(request.getState())
                .district(request.getDistrict())
                .build();

        warehouseOperatorProfileRepository.save(profile);

        auditService.logAction(savedUser.getId(), AuditAction.USER_REGISTER, "USER",
                savedUser.getId().toString(), "Warehouse operator registered: " + savedUser.getMobileNumber(), null);

        notificationService.createNotification(savedUser.getId(),
                "Registration Received",
                "Welcome to SAM! Your warehouse operator profile is pending admin verification.",
                NotificationType.USER_VERIFICATION, savedUser.getId().toString(), "USER");

        return generateAuthResponse(savedUser, "Warehouse operator registration successful.");
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMobileNumber(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (principal.getStatus() == AccountStatus.SUSPENDED) {
            throw new UnauthorizedException("Your account is suspended. Please contact platform support.");
        }
        if (principal.getStatus() == AccountStatus.REJECTED) {
            throw new UnauthorizedException("Your account verification was rejected. Please contact support.");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return generateAuthResponse(user, "Login successful. Simulated OTP: " + defaultMockOtp);
    }

    @Transactional
    public boolean verifyMockOtp(OtpVerifyRequest request) {
        if (defaultMockOtp.equals(request.getOtp()) || "123456".equals(request.getOtp())) {
            userRepository.findByMobileNumber(request.getMobileNumber()).ifPresent(u -> {
                u.setMobileVerified(true);
                userRepository.save(u);
            });
            return true;
        }
        throw new BadRequestException("Invalid OTP entered. (For prototype simulation, use OTP: 123456)");
    }

    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token has expired or been revoked. Please log in again.");
        }

        User user = refreshToken.getUser();
        UserPrincipal principal = UserPrincipal.create(user);
        String newAccessToken = tokenProvider.generateAccessToken(principal);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .userId(user.getId())
                .mobileNumber(user.getMobileNumber())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .isMobileVerified(user.isMobileVerified())
                .build();
    }

    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        if (!defaultMockOtp.equals(request.getOtp()) && !"123456".equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP for password reset. (Prototype default OTP: 123456)");
        }

        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + request.getMobileNumber()));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        refreshTokenRepository.revokeAllUserTokens(user.getId());
    }

    private void validateUniqueMobileAndEmail(String mobile, String email) {
        if (userRepository.existsByMobileNumber(mobile)) {
            throw new ConflictException("Mobile number is already registered: " + mobile);
        }
        if (email != null && !email.isBlank() && userRepository.existsByEmail(email)) {
            throw new ConflictException("Email is already registered: " + email);
        }
    }

    private AuthResponse generateAuthResponse(User user, String note) {
        UserPrincipal principal = UserPrincipal.create(user);
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshTokenStr = tokenProvider.generateRefreshToken(user.getId());

        // Save refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .userId(user.getId())
                .mobileNumber(user.getMobileNumber())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .isMobileVerified(user.isMobileVerified())
                .mockOtpNote(note)
                .build();
    }
}
