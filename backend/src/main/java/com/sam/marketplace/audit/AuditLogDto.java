package com.sam.marketplace.audit;

import com.sam.marketplace.common.enums.AuditAction;

import java.time.Instant;
import java.util.UUID;

public class AuditLogDto {
    private UUID id;
    private UUID userId;
    private String userName;
    private AuditAction action;
    private String resourceType;
    private String resourceId;
    private String details;
    private String ipAddress;
    private Instant createdAt;

    public AuditLogDto() {}

    public AuditLogDto(UUID id, UUID userId, String userName, AuditAction action, String resourceType, String resourceId, String details, String ipAddress, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.details = details;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    public static AuditLogDto from(AuditLog entity) {
        if (entity == null) return null;
        return new AuditLogDto(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getUser() != null ? entity.getUser().getFullName() : "SYSTEM",
                entity.getAction(),
                entity.getResourceType(),
                entity.getResourceId(),
                entity.getDetails(),
                entity.getIpAddress(),
                entity.getCreatedAt()
        );
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
