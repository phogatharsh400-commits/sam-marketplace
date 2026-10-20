package com.sam.marketplace.notification;

import com.sam.marketplace.common.enums.NotificationType;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public NotificationDto createNotification(UUID userId, String title, String message,
                                              NotificationType type, String refId, String refType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(type)
                .isRead(false)
                .referenceId(refId)
                .referenceType(refType)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationDto dto = NotificationDto.from(saved);

        try {
            messagingTemplate.convertAndSend("/topic/notifications/" + userId, dto);
        } catch (Exception e) {
            log.warn("Could not send websocket notification to user {}: {}", userId, e.getMessage());
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationDto> getUserNotifications(UUID userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(NotificationDto::from));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getRecentNotifications(UUID userId) {
        return notificationRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        if (notification.getUser().getId().equals(userId)) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }
}
