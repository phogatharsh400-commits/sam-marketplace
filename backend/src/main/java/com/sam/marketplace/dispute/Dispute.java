package com.sam.marketplace.dispute;

import com.sam.marketplace.common.enums.DisputeStatus;
import com.sam.marketplace.contract.SaleContract;
import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "disputes")
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private SaleContract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_user_id", nullable = false)
    private User raisedByUser;

    @Column(nullable = false, length = 200)
    private String reason;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String evidenceFileUrl;

    @Column(nullable = false, length = 50)
    private String requestedResolution = "FULL_REFUND";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DisputeStatus status = DisputeStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    private Instant resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_admin_id")
    private User resolvedByAdmin;

    @OneToMany(mappedBy = "dispute", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DisputeEvidence> evidenceList = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Dispute() {}

    public Dispute(UUID id, SaleContract contract, User raisedByUser, String reason,
                   String description, String evidenceFileUrl, String requestedResolution,
                   DisputeStatus status, String resolutionNotes, Instant resolvedAt,
                   User resolvedByAdmin, List<DisputeEvidence> evidenceList,
                   Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.contract = contract;
        this.raisedByUser = raisedByUser;
        this.reason = reason;
        this.description = description;
        this.evidenceFileUrl = evidenceFileUrl;
        this.requestedResolution = requestedResolution != null ? requestedResolution : "FULL_REFUND";
        this.status = status != null ? status : DisputeStatus.OPEN;
        this.resolutionNotes = resolutionNotes;
        this.resolvedAt = resolvedAt;
        this.resolvedByAdmin = resolvedByAdmin;
        this.evidenceList = evidenceList != null ? evidenceList : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private SaleContract contract;
        private User raisedByUser;
        private String reason;
        private String description;
        private String evidenceFileUrl;
        private String requestedResolution = "FULL_REFUND";
        private DisputeStatus status = DisputeStatus.OPEN;
        private String resolutionNotes;
        private Instant resolvedAt;
        private User resolvedByAdmin;
        private List<DisputeEvidence> evidenceList = new ArrayList<>();
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder contract(SaleContract contract) { this.contract = contract; return this; }
        public Builder raisedByUser(User raisedByUser) { this.raisedByUser = raisedByUser; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder evidenceFileUrl(String evidenceFileUrl) { this.evidenceFileUrl = evidenceFileUrl; return this; }
        public Builder requestedResolution(String req) { this.requestedResolution = req; return this; }
        public Builder status(DisputeStatus status) { this.status = status; return this; }
        public Builder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }
        public Builder resolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public Builder resolvedByAdmin(User admin) { this.resolvedByAdmin = admin; return this; }
        public Builder evidenceList(List<DisputeEvidence> list) { this.evidenceList = list; return this; }
        public Builder createdAt(Instant c) { this.createdAt = c; return this; }
        public Builder updatedAt(Instant u) { this.updatedAt = u; return this; }

        public Dispute build() {
            return new Dispute(id, contract, raisedByUser, reason, description, evidenceFileUrl,
                    requestedResolution, status, resolutionNotes, resolvedAt, resolvedByAdmin,
                    evidenceList, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SaleContract getContract() { return contract; }
    public void setContract(SaleContract contract) { this.contract = contract; }
    public User getRaisedByUser() { return raisedByUser; }
    public void setRaisedByUser(User raisedByUser) { this.raisedByUser = raisedByUser; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEvidenceFileUrl() { return evidenceFileUrl; }
    public void setEvidenceFileUrl(String evidenceFileUrl) { this.evidenceFileUrl = evidenceFileUrl; }
    public String getRequestedResolution() { return requestedResolution; }
    public void setRequestedResolution(String requestedResolution) { this.requestedResolution = requestedResolution; }
    public DisputeStatus getStatus() { return status; }
    public void setStatus(DisputeStatus status) { this.status = status; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
    public User getResolvedByAdmin() { return resolvedByAdmin; }
    public void setResolvedByAdmin(User resolvedByAdmin) { this.resolvedByAdmin = resolvedByAdmin; }
    public List<DisputeEvidence> getEvidenceList() { return evidenceList; }
    public void setEvidenceList(List<DisputeEvidence> evidenceList) { this.evidenceList = evidenceList; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
