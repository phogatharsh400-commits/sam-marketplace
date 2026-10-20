package com.sam.marketplace.merit;

import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MeritService {

    private static final Logger log = LoggerFactory.getLogger(MeritService.class);

    private final UserRepository userRepository;
    private final MeritRepository meritRepository;

    public MeritService(UserRepository userRepository, MeritRepository meritRepository) {
        this.userRepository = userRepository;
        this.meritRepository = meritRepository;
    }

    @Transactional
    public MeritDto deductMeritPoints(UUID userId, int deduction, String reason, String referenceEntity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        int currentPoints = user.getMeritPoints();
        int newPoints = Math.max(0, currentPoints - deduction);
        user.setMeritPoints(newPoints);
        user.setTotalViolations(user.getTotalViolations() + 1);
        userRepository.save(user);

        MeritAuditTransaction audit = MeritAuditTransaction.builder()
                .userId(user.getId())
                .userName(user.getFullName())
                .userRole(user.getRole())
                .pointsDelta(-deduction)
                .resultingPoints(newPoints)
                .trustTier(user.getTrustTier())
                .reason(reason)
                .referenceEntity(referenceEntity)
                .build();
        meritRepository.save(audit);

        log.warn("[MeritEngine] Deducted {} merit points from user {} ({}). New Score: {}/100, Tier: {}, Reason: {}",
                deduction, user.getFullName(), user.getId(), newPoints, user.getTrustTier(), reason);

        return getUserMeritProfile(userId);
    }

    @Transactional
    public MeritDto awardMeritPoints(UUID userId, int bonus, String reason, String referenceEntity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        int currentPoints = user.getMeritPoints();
        int newPoints = Math.min(100, currentPoints + bonus);
        user.setMeritPoints(newPoints);
        user.setTotalSuccessfulTrades(user.getTotalSuccessfulTrades() + 1);
        userRepository.save(user);

        MeritAuditTransaction audit = MeritAuditTransaction.builder()
                .userId(user.getId())
                .userName(user.getFullName())
                .userRole(user.getRole())
                .pointsDelta(bonus)
                .resultingPoints(newPoints)
                .trustTier(user.getTrustTier())
                .reason(reason)
                .referenceEntity(referenceEntity)
                .build();
        meritRepository.save(audit);

        log.info("[MeritEngine] Awarded +{} merit points to user {} ({}). New Score: {}/100, Tier: {}, Reason: {}",
                bonus, user.getFullName(), user.getId(), newPoints, user.getTrustTier(), reason);

        return getUserMeritProfile(userId);
    }

    @Transactional(readOnly = true)
    public MeritDto getUserMeritProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        List<MeritAuditTransaction> historyList = meritRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<MeritDto.MeritTransactionDto> historyDtos = historyList.stream()
                .map(h -> new MeritDto.MeritTransactionDto(
                        h.getId(),
                        h.getPointsDelta(),
                        h.getResultingPoints(),
                        h.getReason(),
                        h.getReferenceEntity(),
                        h.getCreatedAt()
                ))
                .toList();

        int points = user.getMeritPoints();
        boolean isWatchlist = points < 60;

        return new MeritDto(
                user.getId(),
                user.getFullName(),
                points,
                user.getTrustTier(),
                user.getTotalViolations(),
                user.getTotalSuccessfulTrades(),
                isWatchlist,
                historyDtos
        );
    }

    @Transactional(readOnly = true)
    public List<MeritAuditTransaction> getRecentPlatformMeritAuditLogs() {
        return meritRepository.findTop50ByOrderByCreatedAtDesc();
    }
}
