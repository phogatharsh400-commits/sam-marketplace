package com.sam.marketplace.merit;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MeritDto {

    private UUID userId;
    private String userName;
    private int meritPoints;
    private String trustTier;
    private int totalViolations;
    private int totalSuccessfulTrades;
    private boolean isWatchlistFlagged;
    private List<MeritTransactionDto> history;

    public MeritDto() {}

    public MeritDto(UUID userId, String userName, int meritPoints, String trustTier,
                    int totalViolations, int totalSuccessfulTrades, boolean isWatchlistFlagged,
                    List<MeritTransactionDto> history) {
        this.userId = userId;
        this.userName = userName;
        this.meritPoints = meritPoints;
        this.trustTier = trustTier;
        this.totalViolations = totalViolations;
        this.totalSuccessfulTrades = totalSuccessfulTrades;
        this.isWatchlistFlagged = isWatchlistFlagged;
        this.history = history;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getMeritPoints() { return meritPoints; }
    public void setMeritPoints(int meritPoints) { this.meritPoints = meritPoints; }
    public String getTrustTier() { return trustTier; }
    public void setTrustTier(String trustTier) { this.trustTier = trustTier; }
    public int getTotalViolations() { return totalViolations; }
    public void setTotalViolations(int totalViolations) { this.totalViolations = totalViolations; }
    public int getTotalSuccessfulTrades() { return totalSuccessfulTrades; }
    public void setTotalSuccessfulTrades(int totalSuccessfulTrades) { this.totalSuccessfulTrades = totalSuccessfulTrades; }
    public boolean isWatchlistFlagged() { return isWatchlistFlagged; }
    public void setWatchlistFlagged(boolean watchlistFlagged) { isWatchlistFlagged = watchlistFlagged; }
    public List<MeritTransactionDto> getHistory() { return history; }
    public void setHistory(List<MeritTransactionDto> history) { this.history = history; }

    public static class MeritTransactionDto {
        private UUID id;
        private int pointsDelta;
        private int resultingPoints;
        private String reason;
        private String referenceEntity;
        private Instant createdAt;

        public MeritTransactionDto() {}

        public MeritTransactionDto(UUID id, int pointsDelta, int resultingPoints,
                                   String reason, String referenceEntity, Instant createdAt) {
            this.id = id;
            this.pointsDelta = pointsDelta;
            this.resultingPoints = resultingPoints;
            this.reason = reason;
            this.referenceEntity = referenceEntity;
            this.createdAt = createdAt;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public int getPointsDelta() { return pointsDelta; }
        public void setPointsDelta(int pointsDelta) { this.pointsDelta = pointsDelta; }
        public int getResultingPoints() { return resultingPoints; }
        public void setResultingPoints(int resultingPoints) { this.resultingPoints = resultingPoints; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getReferenceEntity() { return referenceEntity; }
        public void setReferenceEntity(String referenceEntity) { this.referenceEntity = referenceEntity; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }
}
