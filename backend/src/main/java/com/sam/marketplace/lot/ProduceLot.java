package com.sam.marketplace.lot;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.LotStatus;
import com.sam.marketplace.common.enums.ProduceGrade;
import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lots")
public class ProduceLot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String lotNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CropType crop;

    @Column(nullable = false, length = 100)
    private String variety;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityQuintals;

    @Column(nullable = false, length = 20)
    private String unit = "QUINTAL";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProduceGrade grade = ProduceGrade.GRADE_A;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal moisturePercentage;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 150)
    private String village;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pickupLocation;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minPricePerQuintal;

    @Column(nullable = false)
    private LocalDate expectedDispatchDate;

    @Column(nullable = false)
    private LocalDate auctionClosingDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String storageRequirement = "DRY_COVERED";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LotStatus status = LotStatus.OPEN;

    @Column(nullable = false)
    private boolean poolingEnabled = false;

    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderQuantity;

    @Column(nullable = false)
    private boolean sampleApproved = true;

    @Column(length = 50)
    private String sampleStatus = "QUALITY_VERIFIED_AND_APPROVED";

    @Column(precision = 5, scale = 2)
    private BigDecimal foreignMatterPercent = BigDecimal.valueOf(0.80);

    @Column(precision = 5, scale = 2)
    private BigDecimal brokenGrainsPercent = BigDecimal.valueOf(1.20);

    @Column(length = 100)
    private String qualityCertificateNumber;

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LotImage> images = new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public ProduceLot() {}

    public ProduceLot(UUID id, String lotNumber, User farmer, CropType crop, String variety,
                      BigDecimal quantityQuintals, String unit, ProduceGrade grade,
                      BigDecimal moisturePercentage, String state, String district, String village,
                      String pickupLocation, BigDecimal minPricePerQuintal, LocalDate expectedDispatchDate,
                      LocalDate auctionClosingDate, String description, String storageRequirement,
                      LotStatus status, List<LotImage> images, Long version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.lotNumber = lotNumber;
        this.farmer = farmer;
        this.crop = crop;
        this.variety = variety;
        this.quantityQuintals = quantityQuintals;
        this.unit = unit != null ? unit : "QUINTAL";
        this.grade = grade != null ? grade : ProduceGrade.GRADE_A;
        this.moisturePercentage = moisturePercentage;
        this.state = state;
        this.district = district;
        this.village = village;
        this.pickupLocation = pickupLocation;
        this.minPricePerQuintal = minPricePerQuintal;
        this.expectedDispatchDate = expectedDispatchDate;
        this.auctionClosingDate = auctionClosingDate;
        this.description = description;
        this.storageRequirement = storageRequirement != null ? storageRequirement : "DRY_COVERED";
        this.status = status != null ? status : LotStatus.OPEN;
        this.images = images != null ? images : new ArrayList<>();
        this.version = version != null ? version : 0L;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public User getFarmer() { return farmer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }
    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public BigDecimal getQuantityQuintals() { return quantityQuintals; }
    public void setQuantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public ProduceGrade getGrade() { return grade; }
    public void setGrade(ProduceGrade grade) { this.grade = grade; }
    public BigDecimal getMoisturePercentage() { return moisturePercentage; }
    public void setMoisturePercentage(BigDecimal moisturePercentage) { this.moisturePercentage = moisturePercentage; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public BigDecimal getMinPricePerQuintal() { return minPricePerQuintal; }
    public void setMinPricePerQuintal(BigDecimal minPricePerQuintal) { this.minPricePerQuintal = minPricePerQuintal; }
    public LocalDate getExpectedDispatchDate() { return expectedDispatchDate; }
    public void setExpectedDispatchDate(LocalDate expectedDispatchDate) { this.expectedDispatchDate = expectedDispatchDate; }
    public LocalDate getAuctionClosingDate() { return auctionClosingDate; }
    public void setAuctionClosingDate(LocalDate auctionClosingDate) { this.auctionClosingDate = auctionClosingDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStorageRequirement() { return storageRequirement; }
    public void setStorageRequirement(String storageRequirement) { this.storageRequirement = storageRequirement; }
    public LotStatus getStatus() { return status; }
    public void setStatus(LotStatus status) { this.status = status; }
    public List<LotImage> getImages() { return images; }
    public void setImages(List<LotImage> images) { this.images = images; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public boolean isPoolingEnabled() { return poolingEnabled; }
    public void setPoolingEnabled(boolean poolingEnabled) { this.poolingEnabled = poolingEnabled; }
    public BigDecimal getMinOrderQuantity() { return minOrderQuantity; }
    public void setMinOrderQuantity(BigDecimal minOrderQuantity) { this.minOrderQuantity = minOrderQuantity; }
    public boolean isSampleApproved() { return sampleApproved; }
    public void setSampleApproved(boolean sampleApproved) { this.sampleApproved = sampleApproved; }
    public String getSampleStatus() { return sampleStatus; }
    public void setSampleStatus(String sampleStatus) { this.sampleStatus = sampleStatus; }
    public BigDecimal getForeignMatterPercent() { return foreignMatterPercent; }
    public void setForeignMatterPercent(BigDecimal foreignMatterPercent) { this.foreignMatterPercent = foreignMatterPercent; }
    public BigDecimal getBrokenGrainsPercent() { return brokenGrainsPercent; }
    public void setBrokenGrainsPercent(BigDecimal brokenGrainsPercent) { this.brokenGrainsPercent = brokenGrainsPercent; }
    public String getQualityCertificateNumber() { return qualityCertificateNumber; }
    public void setQualityCertificateNumber(String qualityCertificateNumber) { this.qualityCertificateNumber = qualityCertificateNumber; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String lotNumber;
        private User farmer;
        private CropType crop;
        private String variety;
        private BigDecimal quantityQuintals;
        private String unit = "QUINTAL";
        private ProduceGrade grade = ProduceGrade.GRADE_A;
        private BigDecimal moisturePercentage;
        private String state;
        private String district;
        private String village;
        private String pickupLocation;
        private BigDecimal minPricePerQuintal;
        private LocalDate expectedDispatchDate;
        private LocalDate auctionClosingDate;
        private String description;
        private String storageRequirement = "DRY_COVERED";
        private LotStatus status = LotStatus.OPEN;
        private boolean poolingEnabled = false;
        private BigDecimal minOrderQuantity;
        private boolean sampleApproved = true;
        private String sampleStatus = "QUALITY_VERIFIED_AND_APPROVED";
        private BigDecimal foreignMatterPercent = BigDecimal.valueOf(0.80);
        private BigDecimal brokenGrainsPercent = BigDecimal.valueOf(1.20);
        private String qualityCertificateNumber;
        private List<LotImage> images = new ArrayList<>();
        private Long version = 0L;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder lotNumber(String lotNumber) { this.lotNumber = lotNumber; return this; }
        public Builder farmer(User farmer) { this.farmer = farmer; return this; }
        public Builder crop(CropType crop) { this.crop = crop; return this; }
        public Builder variety(String variety) { this.variety = variety; return this; }
        public Builder quantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder grade(ProduceGrade grade) { this.grade = grade; return this; }
        public Builder moisturePercentage(BigDecimal moisturePercentage) { this.moisturePercentage = moisturePercentage; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder village(String village) { this.village = village; return this; }
        public Builder pickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; return this; }
        public Builder minPricePerQuintal(BigDecimal minPricePerQuintal) { this.minPricePerQuintal = minPricePerQuintal; return this; }
        public Builder expectedDispatchDate(LocalDate expectedDispatchDate) { this.expectedDispatchDate = expectedDispatchDate; return this; }
        public Builder auctionClosingDate(LocalDate auctionClosingDate) { this.auctionClosingDate = auctionClosingDate; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder storageRequirement(String storageRequirement) { this.storageRequirement = storageRequirement; return this; }
        public Builder status(LotStatus status) { this.status = status; return this; }
        public Builder poolingEnabled(boolean poolingEnabled) { this.poolingEnabled = poolingEnabled; return this; }
        public Builder minOrderQuantity(BigDecimal minOrderQuantity) { this.minOrderQuantity = minOrderQuantity; return this; }
        public Builder sampleApproved(boolean sampleApproved) { this.sampleApproved = sampleApproved; return this; }
        public Builder sampleStatus(String sampleStatus) { this.sampleStatus = sampleStatus; return this; }
        public Builder foreignMatterPercent(BigDecimal foreignMatterPercent) { this.foreignMatterPercent = foreignMatterPercent; return this; }
        public Builder brokenGrainsPercent(BigDecimal brokenGrainsPercent) { this.brokenGrainsPercent = brokenGrainsPercent; return this; }
        public Builder qualityCertificateNumber(String qualityCertificateNumber) { this.qualityCertificateNumber = qualityCertificateNumber; return this; }
        public Builder images(List<LotImage> images) { this.images = images; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public ProduceLot build() {
            ProduceLot lot = new ProduceLot();
            lot.setId(id);
            lot.setLotNumber(lotNumber);
            lot.setFarmer(farmer);
            lot.setCrop(crop);
            lot.setVariety(variety);
            lot.setQuantityQuintals(quantityQuintals);
            lot.setUnit(unit != null ? unit : "QUINTAL");
            lot.setGrade(grade != null ? grade : ProduceGrade.GRADE_A);
            lot.setMoisturePercentage(moisturePercentage);
            lot.setState(state);
            lot.setDistrict(district);
            lot.setVillage(village);
            lot.setPickupLocation(pickupLocation);
            lot.setMinPricePerQuintal(minPricePerQuintal);
            lot.setExpectedDispatchDate(expectedDispatchDate);
            lot.setAuctionClosingDate(auctionClosingDate);
            lot.setDescription(description);
            lot.setStorageRequirement(storageRequirement != null ? storageRequirement : "DRY_COVERED");
            lot.setStatus(status != null ? status : LotStatus.OPEN);
            lot.setPoolingEnabled(poolingEnabled);
            lot.setMinOrderQuantity(minOrderQuantity != null ? minOrderQuantity : quantityQuintals);
            lot.setSampleApproved(sampleApproved);
            lot.setSampleStatus(sampleStatus != null ? sampleStatus : "QUALITY_VERIFIED_AND_APPROVED");
            lot.setForeignMatterPercent(foreignMatterPercent != null ? foreignMatterPercent : BigDecimal.valueOf(0.80));
            lot.setBrokenGrainsPercent(brokenGrainsPercent != null ? brokenGrainsPercent : BigDecimal.valueOf(1.20));
            lot.setQualityCertificateNumber(qualityCertificateNumber != null ? qualityCertificateNumber : "QC-HAR-2026-" + (lotNumber != null ? lotNumber.replaceAll("\\D+", "") : "1001"));
            lot.setImages(images != null ? images : new ArrayList<>());
            lot.setVersion(version != null ? version : 0L);
            lot.setCreatedAt(createdAt);
            lot.setUpdatedAt(updatedAt);
            return lot;
        }
    }
}
