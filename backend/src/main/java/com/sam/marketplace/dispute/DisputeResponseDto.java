package com.sam.marketplace.dispute;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.DisputeStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DisputeResponseDto {
    private UUID id;
    private UUID contractId;
    private String contractNumber;
    private CropType crop;
    private BigDecimal contractAmount;
    private UUID raisedByUserId;
    private String raisedByUserName;
    private String raisedByUserRole;
    private String reason;
    private String description;
    private String evidenceFileUrl;
    private String requestedResolution;
    private DisputeStatus status;
    private String resolutionNotes;
    private Instant resolvedAt;
    private UUID resolvedByAdminId;
    private String resolvedByAdminName;
    private List<String> evidenceFiles;
    private Instant createdAt;
    private Instant updatedAt;

    public DisputeResponseDto() {}

    public DisputeResponseDto(UUID id, UUID contractId, String contractNumber, CropType crop,
                              BigDecimal contractAmount, UUID raisedByUserId, String raisedByUserName,
                              String raisedByUserRole, String reason, String description,
                              String evidenceFileUrl, String requestedResolution, DisputeStatus status,
                              String resolutionNotes, Instant resolvedAt, UUID resolvedByAdminId,
                              String resolvedByAdminName, List<String> evidenceFiles,
                              Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.contractId = contractId;
        this.contractNumber = contractNumber;
        this.crop = crop;
        this.contractAmount = contractAmount;
        this.raisedByUserId = raisedByUserId;
        this.raisedByUserName = raisedByUserName;
        this.raisedByUserRole = raisedByUserRole;
        this.reason = reason;
        this.description = description;
        this.evidenceFileUrl = evidenceFileUrl;
        this.requestedResolution = requestedResolution;
        this.status = status;
        this.resolutionNotes = resolutionNotes;
        this.resolvedAt = resolvedAt;
        this.resolvedByAdminId = resolvedByAdminId;
        this.resolvedByAdminName = resolvedByAdminName;
        this.evidenceFiles = evidenceFiles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID contractId;
        private String contractNumber;
        private CropType crop;
        private BigDecimal contractAmount;
        private UUID raisedByUserId;
        private String raisedByUserName;
        private String raisedByUserRole;
        private String reason;
        private String description;
        private String evidenceFileUrl;
        private String requestedResolution;
        private DisputeStatus status;
        private String resolutionNotes;
        private Instant resolvedAt;
        private UUID resolvedByAdminId;
        private String resolvedByAdminName;
        private List<String> evidenceFiles;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder contractId(UUID contractId) { this.contractId = contractId; return this; }
        public Builder contractNumber(String contractNumber) { this.contractNumber = contractNumber; return this; }
        public Builder crop(CropType crop) { this.crop = crop; return this; }
        public Builder contractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; return this; }
        public Builder raisedByUserId(UUID raisedByUserId) { this.raisedByUserId = raisedByUserId; return this; }
        public Builder raisedByUserName(String raisedByUserName) { this.raisedByUserName = raisedByUserName; return this; }
        public Builder raisedByUserRole(String raisedByUserRole) { this.raisedByUserRole = raisedByUserRole; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder evidenceFileUrl(String evidenceFileUrl) { this.evidenceFileUrl = evidenceFileUrl; return this; }
        public Builder requestedResolution(String requestedResolution) { this.requestedResolution = requestedResolution; return this; }
        public Builder status(DisputeStatus status) { this.status = status; return this; }
        public Builder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }
        public Builder resolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public Builder resolvedByAdminId(UUID resolvedByAdminId) { this.resolvedByAdminId = resolvedByAdminId; return this; }
        public Builder resolvedByAdminName(String resolvedByAdminName) { this.resolvedByAdminName = resolvedByAdminName; return this; }
        public Builder evidenceFiles(List<String> evidenceFiles) { this.evidenceFiles = evidenceFiles; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public DisputeResponseDto build() {
            return new DisputeResponseDto(id, contractId, contractNumber, crop, contractAmount,
                    raisedByUserId, raisedByUserName, raisedByUserRole, reason, description,
                    evidenceFileUrl, requestedResolution, status, resolutionNotes, resolvedAt,
                    resolvedByAdminId, resolvedByAdminName, evidenceFiles, createdAt, updatedAt);
        }
    }

    public static DisputeResponseDto from(Dispute d) {
        if (d == null) return null;

        List<String> files = d.getEvidenceList() != null
                ? d.getEvidenceList().stream().map(DisputeEvidence::getFileUrl).toList()
                : Collections.emptyList();

        return DisputeResponseDto.builder()
                .id(d.getId())
                .contractId(d.getContract().getId())
                .contractNumber(d.getContract().getContractNumber())
                .crop(d.getContract().getLot().getCrop())
                .contractAmount(d.getContract().getTotalAmount())
                .raisedByUserId(d.getRaisedByUser().getId())
                .raisedByUserName(d.getRaisedByUser().getFullName())
                .raisedByUserRole(d.getRaisedByUser().getRole().name())
                .reason(d.getReason())
                .description(d.getDescription())
                .evidenceFileUrl(d.getEvidenceFileUrl())
                .requestedResolution(d.getRequestedResolution())
                .status(d.getStatus())
                .resolutionNotes(d.getResolutionNotes())
                .resolvedAt(d.getResolvedAt())
                .resolvedByAdminId(d.getResolvedByAdmin() != null ? d.getResolvedByAdmin().getId() : null)
                .resolvedByAdminName(d.getResolvedByAdmin() != null ? d.getResolvedByAdmin().getFullName() : null)
                .evidenceFiles(files)
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getContractId() { return contractId; }
    public void setContractId(UUID contractId) { this.contractId = contractId; }
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
    public UUID getRaisedByUserId() { return raisedByUserId; }
    public void setRaisedByUserId(UUID raisedByUserId) { this.raisedByUserId = raisedByUserId; }
    public String getRaisedByUserName() { return raisedByUserName; }
    public void setRaisedByUserName(String raisedByUserName) { this.raisedByUserName = raisedByUserName; }
    public String getRaisedByUserRole() { return raisedByUserRole; }
    public void setRaisedByUserRole(String raisedByUserRole) { this.raisedByUserRole = raisedByUserRole; }
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
    public UUID getResolvedByAdminId() { return resolvedByAdminId; }
    public void setResolvedByAdminId(UUID resolvedByAdminId) { this.resolvedByAdminId = resolvedByAdminId; }
    public String getResolvedByAdminName() { return resolvedByAdminName; }
    public void setResolvedByAdminName(String resolvedByAdminName) { this.resolvedByAdminName = resolvedByAdminName; }
    public List<String> getEvidenceFiles() { return evidenceFiles; }
    public void setEvidenceFiles(List<String> evidenceFiles) { this.evidenceFiles = evidenceFiles; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
