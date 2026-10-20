package com.sam.marketplace.dispute;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class DisputeCreateDto {

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotBlank(message = "Detailed description is required")
    private String description;

    private String evidenceFileUrl;
    private String requestedResolution = "FULL_REFUND";
    private List<String> evidenceFileUrls;

    public DisputeCreateDto() {}

    public DisputeCreateDto(String reason, String description, String evidenceFileUrl,
                            String requestedResolution, List<String> evidenceFileUrls) {
        this.reason = reason;
        this.description = description;
        this.evidenceFileUrl = evidenceFileUrl;
        this.requestedResolution = requestedResolution != null ? requestedResolution : "FULL_REFUND";
        this.evidenceFileUrls = evidenceFileUrls;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEvidenceFileUrl() { return evidenceFileUrl; }
    public void setEvidenceFileUrl(String evidenceFileUrl) { this.evidenceFileUrl = evidenceFileUrl; }
    public String getRequestedResolution() { return requestedResolution; }
    public void setRequestedResolution(String requestedResolution) { this.requestedResolution = requestedResolution; }
    public List<String> getEvidenceFileUrls() { return evidenceFileUrls; }
    public void setEvidenceFileUrls(List<String> evidenceFileUrls) { this.evidenceFileUrls = evidenceFileUrls; }
}
