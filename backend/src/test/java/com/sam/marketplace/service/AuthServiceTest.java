package com.sam.marketplace.service;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.auth.*;
import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.RoleType;
import com.sam.marketplace.common.exception.ConflictException;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.security.JwtTokenProvider;
import com.sam.marketplace.security.UserPrincipal;
import com.sam.marketplace.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FarmerProfileRepository farmerProfileRepository;

    @Mock
    private BuyerProfileRepository buyerProfileRepository;

    @Mock
    private WarehouseOperatorProfileRepository warehouseOperatorProfileRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Successfully register farmer with PENDING_VERIFICATION status")
    void testRegisterFarmer_Success() {
        RegisterFarmerRequest request = new RegisterFarmerRequest();
        request.setFullName("Ramesh Singh");
        request.setMobileNumber("9000000002");
        request.setEmail("ramesh@sam.in");
        request.setPassword("Farmer@123");
        request.setState("Haryana");
        request.setDistrict("Karnal");
        request.setVillage("Nilokheri");

        when(userRepository.existsByMobileNumber(request.getMobileNumber())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_pass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(tokenProvider.generateAccessToken(any(UserPrincipal.class))).thenReturn("mock_jwt_access");
        when(tokenProvider.generateRefreshToken(any(UUID.class))).thenReturn("mock_jwt_refresh");

        AuthResponse response = authService.registerFarmer(request);

        assertNotNull(response);
        assertEquals(RoleType.FARMER, response.getRole());
        assertEquals(AccountStatus.PENDING_VERIFICATION, response.getStatus());
        assertEquals("mock_jwt_access", response.getAccessToken());
        verify(farmerProfileRepository, times(1)).save(any(FarmerProfile.class));
    }

    @Test
    @DisplayName("Fail registration when mobile number already exists")
    void testRegisterFarmer_DuplicateMobile() {
        RegisterFarmerRequest request = new RegisterFarmerRequest();
        request.setMobileNumber("9000000002");

        when(userRepository.existsByMobileNumber(request.getMobileNumber())).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.registerFarmer(request));
        verify(userRepository, never()).save(any());
    }
}
