package com.sam.marketplace.audit;

import com.sam.marketplace.common.enums.AuditAction;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(UUID userId, AuditAction action, String resourceType, String resourceId, String details, String ipAddress) {
        try {
            User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;
            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .details(details)
                    .ipAddress(ipAddress)
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit logged: action={}, resourceType={}, resourceId={}, userId={}", action, resourceType, resourceId, userId);
        } catch (Exception e) {
            log.error("Failed to persist audit log: action={}, error={}", action, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogDto> getAuditLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PageResponse.from(page.map(AuditLogDto::from));
    }
}
