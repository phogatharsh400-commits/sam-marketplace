package com.sam.marketplace.merit;

import com.sam.marketplace.common.enums.RoleType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "merit_audit_transactions")
public class MeritAuditTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 150)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoleType userRole;

    @Column(nullable = false)
    private int pointsDelta;

    @Column(nullable = false)
    private int resultingPoints;

    @Column(nullable = false, length = 30)
    private String trustTier;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(length = 150)
    private String referenceEntity;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public MeritAuditTransaction() {}

    public MeritAuditTransaction(UUID id, UUID userId, String userName, RoleType userRole,
                                 int pointsDelta, int resultingPoints, String trustTier,
                                 String reason, String referenceEntity, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.pointsDelta = pointsDelta;
        this.resultingPoints = resultingPoints;
        this.trustTier = trustTier;
        this.reason = reason;
        this.referenceEntity = referenceEntity;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public RoleType getUserRole() { return userRole; }
    public void setUserRole(RoleType userRole) { this.userRole = userRole; }
    public int getPointsDelta() { return pointsDelta; }
    public void setPointsDelta(int pointsDelta) { this.pointsDelta = pointsDelta; }
    public int getResultingPoints() { return resultingPoints; }
    public void setResultingPoints(int resultingPoints) { this.resultingPoints = resultingPoints; }
    public String getTrustTier() { return trustTier; }
    public void setTrustTier(String trustTier) { this.trustTier = trustTier; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getReferenceEntity() { return referenceEntity; }
    public void setReferenceEntity(String referenceEntity) { this.referenceEntity = referenceEntity; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private UUID userId;
        private String userName;
        private RoleType userRole;
        private int pointsDelta;
        private int resultingPoints;
        private String trustTier;
        private String reason;
        private String referenceEntity;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder userName(String userName) { this.userName = userName; return this; }
        public Builder userRole(RoleType userRole) { this.userRole = userRole; return this; }
        public Builder pointsDelta(int pointsDelta) { this.pointsDelta = pointsDelta; return this; }
        public Builder resultingPoints(int resultingPoints) { this.resultingPoints = resultingPoints; return this; }
        public Builder trustTier(String trustTier) { this.trustTier = trustTier; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder referenceEntity(String referenceEntity) { this.referenceEntity = referenceEntity; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public MeritAuditTransaction build() {
            return new MeritAuditTransaction(id, userId, userName, userRole, pointsDelta, resultingPoints, trustTier, reason, referenceEntity, createdAt);
        }
    }
}
