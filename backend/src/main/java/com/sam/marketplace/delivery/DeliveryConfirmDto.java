package com.sam.marketplace.delivery;

public class DeliveryConfirmDto {
    private boolean satisfied = true;
    private String qualityRemarks;
    private String inspectionNotes;
    private boolean raiseDisputeOnIssue = false;
    private String disputeReason;
    private String disputeDescription;

    public DeliveryConfirmDto() {}

    public DeliveryConfirmDto(boolean satisfied, String qualityRemarks, String inspectionNotes,
                              boolean raiseDisputeOnIssue, String disputeReason, String disputeDescription) {
        this.satisfied = satisfied;
        this.qualityRemarks = qualityRemarks;
        this.inspectionNotes = inspectionNotes;
        this.raiseDisputeOnIssue = raiseDisputeOnIssue;
        this.disputeReason = disputeReason;
        this.disputeDescription = disputeDescription;
    }

    public boolean isSatisfied() { return satisfied; }
    public void setSatisfied(boolean satisfied) { this.satisfied = satisfied; }
    public String getQualityRemarks() { return qualityRemarks; }
    public void setQualityRemarks(String qualityRemarks) { this.qualityRemarks = qualityRemarks; }
    public String getInspectionNotes() { return inspectionNotes; }
    public void setInspectionNotes(String inspectionNotes) { this.inspectionNotes = inspectionNotes; }
    public boolean isRaiseDisputeOnIssue() { return raiseDisputeOnIssue; }
    public void setRaiseDisputeOnIssue(boolean raiseDisputeOnIssue) { this.raiseDisputeOnIssue = raiseDisputeOnIssue; }
    public String getDisputeReason() { return disputeReason; }
    public void setDisputeReason(String disputeReason) { this.disputeReason = disputeReason; }
    public String getDisputeDescription() { return disputeDescription; }
    public void setDisputeDescription(String disputeDescription) { this.disputeDescription = disputeDescription; }
}
