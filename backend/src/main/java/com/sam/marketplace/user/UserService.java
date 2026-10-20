package com.sam.marketplace.user;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.AuditAction;
import com.sam.marketplace.common.enums.NotificationType;
import com.sam.marketplace.common.enums.RoleType;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final WarehouseOperatorProfileRepository warehouseOperatorProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public UserService(UserRepository userRepository,
                       FarmerProfileRepository farmerProfileRepository,
                       BuyerProfileRepository buyerProfileRepository,
                       WarehouseOperatorProfileRepository warehouseOperatorProfileRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       NotificationService notificationService,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.warehouseOperatorProfileRepository = warehouseOperatorProfileRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserDto.from(user);
    }

    @Transactional
    public UserDto updateUserProfile(UUID userId, UserDto updateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (updateDto.getFullName() != null && !updateDto.getFullName().isBlank()) {
            user.setFullName(updateDto.getFullName());
        }
        if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank()) {
            user.setEmail(updateDto.getEmail());
        }

        if (user.getRole() == RoleType.FARMER && user.getFarmerProfile() != null) {
            FarmerProfile fp = user.getFarmerProfile();
            if (updateDto.getState() != null) fp.setState(updateDto.getState());
            if (updateDto.getDistrict() != null) fp.setDistrict(updateDto.getDistrict());
            if (updateDto.getVillage() != null) fp.setVillage(updateDto.getVillage());
            if (updateDto.getBankAccountNumber() != null) fp.setBankAccountNumber(updateDto.getBankAccountNumber());
            if (updateDto.getBankIfsc() != null) fp.setBankIfsc(updateDto.getBankIfsc());
            if (updateDto.getPreferredLanguage() != null) fp.setPreferredLanguage(updateDto.getPreferredLanguage());
            if (updateDto.getLandAreaAcres() != null) fp.setLandAreaAcres(updateDto.getLandAreaAcres());
            farmerProfileRepository.save(fp);
        } else if (user.getRole() == RoleType.BUYER && user.getBuyerProfile() != null) {
            BuyerProfile bp = user.getBuyerProfile();
            if (updateDto.getBusinessName() != null) bp.setBusinessName(updateDto.getBusinessName());
            if (updateDto.getGstin() != null) bp.setGstin(updateDto.getGstin());
            if (updateDto.getPanNumber() != null) bp.setPanNumber(updateDto.getPanNumber());
            if (updateDto.getBusinessAddress() != null) bp.setBusinessAddress(updateDto.getBusinessAddress());
            if (updateDto.getState() != null) bp.setState(updateDto.getState());
            if (updateDto.getDistrict() != null) bp.setDistrict(updateDto.getDistrict());
            buyerProfileRepository.save(bp);
        } else if (user.getRole() == RoleType.WAREHOUSE_OPERATOR && user.getWarehouseOperatorProfile() != null) {
            WarehouseOperatorProfile wp = user.getWarehouseOperatorProfile();
            if (updateDto.getBusinessName() != null) wp.setBusinessName(updateDto.getBusinessName());
            if (updateDto.getLicenseNumber() != null) wp.setLicenseNumber(updateDto.getLicenseNumber());
            if (updateDto.getState() != null) wp.setState(updateDto.getState());
            if (updateDto.getDistrict() != null) wp.setDistrict(updateDto.getDistrict());
            warehouseOperatorProfileRepository.save(wp);
        }

        User saved = userRepository.save(user);
        return UserDto.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserDto> getAllUsers(RoleType role, AccountStatus status, Pageable pageable) {
        Page<User> page;
        if (role != null && status != null) {
            page = userRepository.findByRoleAndStatus(role, status, pageable);
        } else if (role != null) {
            page = userRepository.findByRole(role, pageable);
        } else if (status != null) {
            page = userRepository.findByStatus(status, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        return PageResponse.from(page.map(UserDto::from));
    }

    @Transactional
    public UserDto verifyUser(UUID adminId, UUID targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));

        user.setStatus(AccountStatus.ACTIVE);
        User saved = userRepository.save(user);

        auditService.logAction(adminId, AuditAction.USER_VERIFIED, "USER",
                user.getId().toString(), "User verified by admin: " + user.getMobileNumber(), null);

        notificationService.createNotification(user.getId(),
                "Account Verified",
                "Congratulations! Your account has been officially verified by the platform admin. You can now fully participate in the marketplace.",
                NotificationType.USER_VERIFICATION, user.getId().toString(), "USER");

        return UserDto.from(saved);
    }

    @Transactional
    public UserDto suspendUser(UUID adminId, UUID targetUserId, String reason) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));

        if (user.getRole() == RoleType.ADMIN) {
            throw new BadRequestException("Administrator accounts cannot be suspended");
        }

        user.setStatus(AccountStatus.SUSPENDED);
        User saved = userRepository.save(user);

        // Revoke active sessions
        refreshTokenRepository.revokeAllUserTokens(targetUserId);

        auditService.logAction(adminId, AuditAction.USER_SUSPENDED, "USER",
                user.getId().toString(), "User suspended. Reason: " + reason, null);

        notificationService.createNotification(user.getId(),
                "Account Suspended",
                "Your account has been suspended by the platform administrator. Reason: " + (reason != null ? reason : "Compliance policy violation"),
                NotificationType.SYSTEM_ALERT, user.getId().toString(), "USER");

        return UserDto.from(saved);
    }

    @Transactional
    public UserDto rejectUser(UUID adminId, UUID targetUserId, String reason) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));

        user.setStatus(AccountStatus.REJECTED);
        User saved = userRepository.save(user);

        refreshTokenRepository.revokeAllUserTokens(targetUserId);

        auditService.logAction(adminId, AuditAction.USER_REJECTED, "USER",
                user.getId().toString(), "User rejected. Reason: " + reason, null);

        notificationService.createNotification(user.getId(),
                "Account Verification Rejected",
                "Your verification request was rejected. Reason: " + (reason != null ? reason : "Information mismatch"),
                NotificationType.USER_VERIFICATION, user.getId().toString(), "USER");

        return UserDto.from(saved);
    }
}
