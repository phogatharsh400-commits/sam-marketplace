package com.sam.marketplace.seeder;

import com.sam.marketplace.benchmark.BenchmarkRepository;
import com.sam.marketplace.benchmark.MandiPriceBenchmark;
import com.sam.marketplace.bid.Bid;
import com.sam.marketplace.bid.BidRepository;
import com.sam.marketplace.common.enums.*;
import com.sam.marketplace.contract.SaleContract;
import com.sam.marketplace.contract.SaleContractRepository;
import com.sam.marketplace.delivery.DeliveryRecord;
import com.sam.marketplace.delivery.DeliveryRepository;
import com.sam.marketplace.lot.LotImage;
import com.sam.marketplace.lot.LotImageRepository;
import com.sam.marketplace.lot.LotRepository;
import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.notification.Notification;
import com.sam.marketplace.notification.NotificationRepository;
import com.sam.marketplace.payment.PaymentRepository;
import com.sam.marketplace.payment.PaymentTransaction;
import com.sam.marketplace.user.*;
import com.sam.marketplace.warehouse.Warehouse;
import com.sam.marketplace.warehouse.WarehouseBooking;
import com.sam.marketplace.warehouse.WarehouseBookingRepository;
import com.sam.marketplace.warehouse.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeederService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeederService.class);

    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final WarehouseOperatorProfileRepository warehouseOperatorProfileRepository;
    private final LotRepository lotRepository;
    private final LotImageRepository lotImageRepository;
    private final BidRepository bidRepository;
    private final SaleContractRepository saleContractRepository;
    private final PaymentRepository paymentRepository;
    private final DeliveryRepository deliveryRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseBookingRepository bookingRepository;
    private final BenchmarkRepository benchmarkRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeederService(UserRepository userRepository,
                                 FarmerProfileRepository farmerProfileRepository,
                                 BuyerProfileRepository buyerProfileRepository,
                                 WarehouseOperatorProfileRepository warehouseOperatorProfileRepository,
                                 LotRepository lotRepository,
                                 LotImageRepository lotImageRepository,
                                 BidRepository bidRepository,
                                 SaleContractRepository saleContractRepository,
                                 PaymentRepository paymentRepository,
                                 DeliveryRepository deliveryRepository,
                                 WarehouseRepository warehouseRepository,
                                 WarehouseBookingRepository bookingRepository,
                                 BenchmarkRepository benchmarkRepository,
                                 NotificationRepository notificationRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.warehouseOperatorProfileRepository = warehouseOperatorProfileRepository;
        this.lotRepository = lotRepository;
        this.lotImageRepository = lotImageRepository;
        this.bidRepository = bidRepository;
        this.saleContractRepository = saleContractRepository;
        this.paymentRepository = paymentRepository;
        this.deliveryRepository = deliveryRepository;
        this.warehouseRepository = warehouseRepository;
        this.bookingRepository = bookingRepository;
        this.benchmarkRepository = benchmarkRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded with user records. Skipping full seeding.");
            return;
        }

        log.info("--- Seeding Smart Agricultural Marketplace Prototype Data ---");

        // 1. Users & Profiles
        User admin = User.builder()
                .mobileNumber("9000000001")
                .email("admin@sam-marketplace.gov.in")
                .passwordHash(passwordEncoder.encode("Admin@123"))
                .fullName("Admin Officer (Karnal HQ)")
                .role(RoleType.ADMIN)
                .status(AccountStatus.ACTIVE)
                .isMobileVerified(true)
                .build();
        admin = userRepository.save(admin);

        User farmer1 = User.builder()
                .mobileNumber("9000000002")
                .email("ramesh.farmer@sam.in")
                .passwordHash(passwordEncoder.encode("Farmer@123"))
                .fullName("Ramesh Singh")
                .role(RoleType.FARMER)
                .status(AccountStatus.ACTIVE)
                .isMobileVerified(true)
                .build();
        farmer1 = userRepository.save(farmer1);
        farmerProfileRepository.save(FarmerProfile.builder()
                .user(farmer1)
                .state("Haryana")
                .district("Karnal")
                .village("Nilokheri")
                .bankAccountNumber("SBI98765432101")
                .bankIfsc("SBIN0001234")
                .preferredLanguage("en")
                .landAreaAcres(BigDecimal.valueOf(14.5))
                .build());

        User farmer2 = User.builder()
                .mobileNumber("9000000005")
                .email("harpreet.farmer@sam.in")
                .passwordHash(passwordEncoder.encode("Farmer@123"))
                .fullName("Harpreet Kaur")
                .role(RoleType.FARMER)
                .status(AccountStatus.ACTIVE)
                .isMobileVerified(true)
                .build();
        farmer2 = userRepository.save(farmer2);
        farmerProfileRepository.save(FarmerProfile.builder()
                .user(farmer2)
                .state("Haryana")
                .district("Hisar")
                .village("Hansi")
                .bankAccountNumber("PND88765432102")
                .bankIfsc("PUNB0123400")
                .preferredLanguage("hi")
                .landAreaAcres(BigDecimal.valueOf(22.0))
                .build());

        User buyer1 = User.builder()
                .mobileNumber("9000000003")
                .email("vikram.traders@haryana-grains.com")
                .passwordHash(passwordEncoder.encode("Buyer@123"))
                .fullName("Vikram Aggarwal")
                .role(RoleType.BUYER)
                .status(AccountStatus.ACTIVE)
                .isMobileVerified(true)
                .build();
        buyer1 = userRepository.save(buyer1);
        buyerProfileRepository.save(BuyerProfile.builder()
                .user(buyer1)
                .businessName("Haryana Grain Traders Pvt Ltd")
                .gstin("06AAACH7409R1ZZ")
                .panNumber("AAACH7409R")
                .businessAddress("Plot 42, New Grain Market, GT Road, Ambala Cantt")
                .state("Haryana")
                .district("Ambala")
                .buyerType("COMMISSION_AGENT")
                .build());

        User buyer2 = User.builder()
                .mobileNumber("9000000006")
                .email("sunil.mittal@agro-millers.com")
                .passwordHash(passwordEncoder.encode("Buyer@123"))
                .fullName("Sunil Mittal")
                .role(RoleType.BUYER)
                .status(AccountStatus.ACTIVE)
                .isMobileVerified(true)
                .build();
        buyer2 = userRepository.save(buyer2);
        buyerProfileRepository.save(BuyerProfile.builder()
                .user(buyer2)
                .businessName("Kisan Agro Flour & Oil Mills")
                .gstin("06BBBCD8901S2ZX")
                .panNumber("BBBCD8901S")
                .businessAddress("Industrial Area Phase 2, Rohtak")
                .state("Haryana")
                .district("Rohtak")
                .buyerType("PROCESSOR")
                .build());

        User operator1 = User.builder()
                .mobileNumber("9000000004")
                .email("sukhvinder.mann@mann-warehousing.com")
                .passwordHash(passwordEncoder.encode("Warehouse@123"))
                .fullName("Sukhvinder Mann")
                .role(RoleType.WAREHOUSE_OPERATOR)
                .status(AccountStatus.ACTIVE)
                .isMobileVerified(true)
                .build();
        operator1 = userRepository.save(operator1);
        warehouseOperatorProfileRepository.save(WarehouseOperatorProfile.builder()
                .user(operator1)
                .businessName("Mann Agro Logistics & Warehousing Corp")
                .licenseNumber("WDRA/HAR/2023/8892")
                .state("Haryana")
                .district("Karnal")
                .build());

        // 2. Mandi Price Benchmarks (Seeded for Karnal, Hisar, Ambala, Rohtak, Sirsa, Panipat)
        String[] districts = {"Karnal", "Hisar", "Ambala", "Rohtak", "Sirsa", "Panipat"};
        LocalDate today = LocalDate.now();

        for (String dist : districts) {
            // Wheat benchmark
            benchmarkRepository.save(MandiPriceBenchmark.builder()
                    .crop(CropType.WHEAT)
                    .district(dist)
                    .state("Haryana")
                    .currentMandiPrice(BigDecimal.valueOf(2420.00))
                    .minPrice(BigDecimal.valueOf(2310.00))
                    .maxPrice(BigDecimal.valueOf(2520.00))
                    .modalPrice(BigDecimal.valueOf(2420.00))
                    .msp(BigDecimal.valueOf(2275.00))
                    .dataSource("Agmarknet Simulated Mandi API (Prototype Data)")
                    .priceDate(today)
                    .build());

            // Paddy benchmark
            benchmarkRepository.save(MandiPriceBenchmark.builder()
                    .crop(CropType.PADDY)
                    .district(dist)
                    .state("Haryana")
                    .currentMandiPrice(BigDecimal.valueOf(2380.00))
                    .minPrice(BigDecimal.valueOf(2240.00))
                    .maxPrice(BigDecimal.valueOf(2490.00))
                    .modalPrice(BigDecimal.valueOf(2380.00))
                    .msp(BigDecimal.valueOf(2183.00))
                    .dataSource("Agmarknet Simulated Mandi API (Prototype Data)")
                    .priceDate(today)
                    .build());

            // Mustard benchmark
            benchmarkRepository.save(MandiPriceBenchmark.builder()
                    .crop(CropType.MUSTARD)
                    .district(dist)
                    .state("Haryana")
                    .currentMandiPrice(BigDecimal.valueOf(5890.00))
                    .minPrice(BigDecimal.valueOf(5680.00))
                    .maxPrice(BigDecimal.valueOf(6150.00))
                    .modalPrice(BigDecimal.valueOf(5890.00))
                    .msp(BigDecimal.valueOf(5650.00))
                    .dataSource("Agmarknet Simulated Mandi API (Prototype Data)")
                    .priceDate(today)
                    .build());
        }

        // 3. Warehouses
        Warehouse wh1 = Warehouse.builder()
                .operator(operator1)
                .warehouseName("Karnal Central Agro Silos & Cold Storage")
                .ownerName("Sukhvinder Mann")
                .contactNumber("9812345678")
                .state("Haryana")
                .district("Karnal")
                .address("Near New Anaj Mandi, GT Road, Karnal")
                .totalCapacityQuintals(BigDecimal.valueOf(5000.00))
                .availableCapacityQuintals(BigDecimal.valueOf(4200.00))
                .supportedCrops("WHEAT, PADDY, MUSTARD")
                .storageChargePerQuintalMonth(BigDecimal.valueOf(18.50))
                .isAccredited(true)
                .description("WDRA accredited steel silos with computerized aeration, moisture monitoring and fire suppression.")
                .build();
        wh1 = warehouseRepository.save(wh1);

        Warehouse wh2 = Warehouse.builder()
                .operator(operator1)
                .warehouseName("Hisar Green Grain Warehouse Hub")
                .ownerName("Sukhvinder Mann")
                .contactNumber("9812345679")
                .state("Haryana")
                .district("Hisar")
                .address("Sector 14 Bypass Road, Hisar")
                .totalCapacityQuintals(BigDecimal.valueOf(3500.00))
                .availableCapacityQuintals(BigDecimal.valueOf(3000.00))
                .supportedCrops("MUSTARD, WHEAT")
                .storageChargePerQuintalMonth(BigDecimal.valueOf(16.00))
                .isAccredited(true)
                .description("Scientific warehouse facility suitable for long-term oilseeds and cereal storage.")
                .build();
        wh2 = warehouseRepository.save(wh2);

        Warehouse wh3 = Warehouse.builder()
                .operator(operator1)
                .warehouseName("Ambala Agri Logistic Park")
                .ownerName("Sukhvinder Mann")
                .contactNumber("9812345680")
                .state("Haryana")
                .district("Ambala")
                .address("NH-44 Junction, Ambala City")
                .totalCapacityQuintals(BigDecimal.valueOf(8000.00))
                .availableCapacityQuintals(BigDecimal.valueOf(6500.00))
                .supportedCrops("WHEAT, PADDY")
                .storageChargePerQuintalMonth(BigDecimal.valueOf(19.00))
                .isAccredited(true)
                .description("High capacity multi-chamber warehouse with dedicated truck loading bays.")
                .build();
        wh3 = warehouseRepository.save(wh3);

        Warehouse wh4 = Warehouse.builder()
                .operator(operator1)
                .warehouseName("Panipat Farmers Cooperative Storage")
                .ownerName("Sukhvinder Mann")
                .contactNumber("9812345681")
                .state("Haryana")
                .district("Panipat")
                .address("Old Subzi Mandi Road, Panipat")
                .totalCapacityQuintals(BigDecimal.valueOf(2500.00))
                .availableCapacityQuintals(BigDecimal.valueOf(2100.00))
                .supportedCrops("WHEAT, MUSTARD, PADDY")
                .storageChargePerQuintalMonth(BigDecimal.valueOf(15.00))
                .isAccredited(true)
                .description("Affordable storage facility with on-site weighbridge and grading lab.")
                .build();
        wh4 = warehouseRepository.save(wh4);

        // 4. Produce Lots (8+ Lots)
        List<ProduceLot> seedLots = new ArrayList<>();

        ProduceLot lot1 = ProduceLot.builder()
                .lotNumber("LOT-20261001-1001")
                .farmer(farmer1)
                .crop(CropType.WHEAT)
                .variety("PBW-502 (Sharbati Shrestha)")
                .quantityQuintals(BigDecimal.valueOf(150.00))
                .unit("QUINTAL")
                .grade(ProduceGrade.GRADE_A)
                .moisturePercentage(BigDecimal.valueOf(11.20))
                .state("Haryana")
                .district("Karnal")
                .village("Nilokheri")
                .pickupLocation("Farm Gate, Plot 12, Nilokheri-Taraori Link Road")
                .minPricePerQuintal(BigDecimal.valueOf(2350.00))
                .expectedDispatchDate(today.plusDays(7))
                .auctionClosingDate(today.plusDays(5))
                .description("Golden grain premium quality Sharbati wheat, freshly harvested and winnowed. Single-origin farm lot.")
                .storageRequirement("DRY_COVERED")
                .status(LotStatus.OPEN)
                .build();
        lot1 = lotRepository.save(lot1);
        lotImageRepository.save(LotImage.builder().lot(lot1).imageUrl("https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=600&auto=format&fit=crop&q=80").isPrimary(true).build());
        seedLots.add(lot1);

        ProduceLot lot2 = ProduceLot.builder()
                .lotNumber("LOT-20261001-1002")
                .farmer(farmer1)
                .crop(CropType.PADDY)
                .variety("Basmati 1121 Super")
                .quantityQuintals(BigDecimal.valueOf(200.00))
                .unit("QUINTAL")
                .grade(ProduceGrade.GRADE_A)
                .moisturePercentage(BigDecimal.valueOf(13.50))
                .state("Haryana")
                .district("Karnal")
                .village("Indri")
                .pickupLocation("Near Mandi Gate, Indri Road")
                .minPricePerQuintal(BigDecimal.valueOf(3800.00))
                .expectedDispatchDate(today.plusDays(10))
                .auctionClosingDate(today.plusDays(6))
                .description("Extra long grain 1121 traditional basmati paddy. Matured aroma and uniform grain length.")
                .storageRequirement("VENTILATED_DRY")
                .status(LotStatus.OPEN)
                .build();
        lot2 = lotRepository.save(lot2);
        lotImageRepository.save(LotImage.builder().lot(lot2).imageUrl("https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600&auto=format&fit=crop&q=80").isPrimary(true).build());
        seedLots.add(lot2);

        ProduceLot lot3 = ProduceLot.builder()
                .lotNumber("LOT-20261001-1003")
                .farmer(farmer2)
                .crop(CropType.MUSTARD)
                .variety("Pusa Bold 42% Oil")
                .quantityQuintals(BigDecimal.valueOf(80.00))
                .unit("QUINTAL")
                .grade(ProduceGrade.GRADE_A)
                .moisturePercentage(BigDecimal.valueOf(7.80))
                .state("Haryana")
                .district("Hisar")
                .village("Hansi")
                .pickupLocation("Main Hansi Mandi Warehouse yard")
                .minPricePerQuintal(BigDecimal.valueOf(5800.00))
                .expectedDispatchDate(today.plusDays(6))
                .auctionClosingDate(today.plusDays(4))
                .description("High oil content bold black mustard seeds. Lab tested 42.1% oil percentage.")
                .storageRequirement("COOL_DRY")
                .status(LotStatus.OPEN)
                .build();
        lot3 = lotRepository.save(lot3);
        lotImageRepository.save(LotImage.builder().lot(lot3).imageUrl("https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=600&auto=format&fit=crop&q=80").isPrimary(true).build());
        seedLots.add(lot3);

        ProduceLot lot4 = ProduceLot.builder()
                .lotNumber("LOT-20261001-1004")
                .farmer(farmer2)
                .crop(CropType.WHEAT)
                .variety("HD-2967 (Kalyan Sona Standard)")
                .quantityQuintals(BigDecimal.valueOf(320.00))
                .unit("QUINTAL")
                .grade(ProduceGrade.GRADE_B)
                .moisturePercentage(BigDecimal.valueOf(12.00))
                .state("Haryana")
                .district("Hisar")
                .village("Narnaund")
                .pickupLocation("Farm storage yard, Narnaund")
                .minPricePerQuintal(BigDecimal.valueOf(2300.00))
                .expectedDispatchDate(today.plusDays(8))
                .auctionClosingDate(today.plusDays(5))
                .description("High yield milling wheat lot, clean and bagged in 50kg jute sacks.")
                .storageRequirement("DRY_COVERED")
                .status(LotStatus.OPEN)
                .build();
        lot4 = lotRepository.save(lot4);
        seedLots.add(lot4);

        ProduceLot lot5 = ProduceLot.builder()
                .lotNumber("LOT-20261001-1005")
                .farmer(farmer1)
                .crop(CropType.PADDY)
                .variety("PR-126 Hybrid")
                .quantityQuintals(BigDecimal.valueOf(180.00))
                .unit("QUINTAL")
                .grade(ProduceGrade.GRADE_B)
                .moisturePercentage(BigDecimal.valueOf(14.00))
                .state("Haryana")
                .district("Ambala")
                .village("Saha")
                .pickupLocation("Saha Agro Center")
                .minPricePerQuintal(BigDecimal.valueOf(2250.00))
                .expectedDispatchDate(today.plusDays(9))
                .auctionClosingDate(today.plusDays(6))
                .description("Quick maturing high grain output non-basmati paddy.")
                .storageRequirement("VENTILATED_DRY")
                .status(LotStatus.OPEN)
                .build();
        lot5 = lotRepository.save(lot5);
        seedLots.add(lot5);

        ProduceLot lot6 = ProduceLot.builder()
                .lotNumber("LOT-20261001-1006")
                .farmer(farmer1)
                .crop(CropType.MUSTARD)
                .variety("Giriraj Black Mustard")
                .quantityQuintals(BigDecimal.valueOf(60.00))
                .unit("QUINTAL")
                .grade(ProduceGrade.GRADE_A)
                .moisturePercentage(BigDecimal.valueOf(8.00))
                .state("Haryana")
                .district("Rohtak")
                .village("Sampla")
                .pickupLocation("Sampla Mandi Link")
                .minPricePerQuintal(BigDecimal.valueOf(5750.00))
                .expectedDispatchDate(today.plusDays(12))
                .auctionClosingDate(today.plusDays(7))
                .description("Rich aroma mustard seeds for premium cold-pressed oil extraction.")
                .storageRequirement("COOL_DRY")
                .status(LotStatus.OPEN)
                .build();
        lot6 = lotRepository.save(lot6);
        seedLots.add(lot6);

        // Lot 7 with active Contract & Escrow
        ProduceLot lot7 = ProduceLot.builder()
                .lotNumber("LOT-20261001-1007")
                .farmer(farmer1)
                .crop(CropType.WHEAT)
                .variety("HD-3086 Premium")
                .quantityQuintals(BigDecimal.valueOf(100.00))
                .unit("QUINTAL")
                .grade(ProduceGrade.GRADE_A)
                .moisturePercentage(BigDecimal.valueOf(11.00))
                .state("Haryana")
                .district("Karnal")
                .village("Nilokheri")
                .pickupLocation("Nilokheri Farm Gate")
                .minPricePerQuintal(BigDecimal.valueOf(2400.00))
                .expectedDispatchDate(today.plusDays(3))
                .auctionClosingDate(today.minusDays(1))
                .description("Pre-booked contract lot with funded escrow.")
                .storageRequirement("DRY_COVERED")
                .status(LotStatus.READY_FOR_DISPATCH)
                .build();
        lot7 = lotRepository.save(lot7);
        seedLots.add(lot7);

        // Lot 8 with Delivered & Paid Contract
        ProduceLot lot8 = ProduceLot.builder()
                .lotNumber("LOT-20261001-1008")
                .farmer(farmer2)
                .crop(CropType.MUSTARD)
                .variety("Pusa Bold Special")
                .quantityQuintals(BigDecimal.valueOf(50.00))
                .unit("QUINTAL")
                .grade(ProduceGrade.GRADE_A)
                .moisturePercentage(BigDecimal.valueOf(7.50))
                .state("Haryana")
                .district("Hisar")
                .village("Hansi")
                .pickupLocation("Hansi Agro Yard")
                .minPricePerQuintal(BigDecimal.valueOf(5850.00))
                .expectedDispatchDate(today.minusDays(5))
                .auctionClosingDate(today.minusDays(8))
                .description("Completed historical transaction lot.")
                .storageRequirement("COOL_DRY")
                .status(LotStatus.PAID)
                .build();
        lot8 = lotRepository.save(lot8);
        seedLots.add(lot8);

        // 5. Seed Bids (15+ Bids)
        // Bids on Lot 1
        bidRepository.save(Bid.builder().lot(lot1).buyer(buyer1).bidPricePerQuintal(BigDecimal.valueOf(2440.00)).quantityQuintals(BigDecimal.valueOf(150.00)).totalAmount(BigDecimal.valueOf(366000.00)).status(BidStatus.ACTIVE).build());
        bidRepository.save(Bid.builder().lot(lot1).buyer(buyer2).bidPricePerQuintal(BigDecimal.valueOf(2410.00)).quantityQuintals(BigDecimal.valueOf(150.00)).totalAmount(BigDecimal.valueOf(361500.00)).status(BidStatus.ACTIVE).build());

        // Bids on Lot 2
        bidRepository.save(Bid.builder().lot(lot2).buyer(buyer1).bidPricePerQuintal(BigDecimal.valueOf(3900.00)).quantityQuintals(BigDecimal.valueOf(200.00)).totalAmount(BigDecimal.valueOf(780000.00)).status(BidStatus.ACTIVE).build());
        bidRepository.save(Bid.builder().lot(lot2).buyer(buyer2).bidPricePerQuintal(BigDecimal.valueOf(3850.00)).quantityQuintals(BigDecimal.valueOf(200.00)).totalAmount(BigDecimal.valueOf(770000.00)).status(BidStatus.ACTIVE).build());

        // Bids on Lot 3
        bidRepository.save(Bid.builder().lot(lot3).buyer(buyer2).bidPricePerQuintal(BigDecimal.valueOf(5920.00)).quantityQuintals(BigDecimal.valueOf(80.00)).totalAmount(BigDecimal.valueOf(473600.00)).status(BidStatus.ACTIVE).build());
        bidRepository.save(Bid.builder().lot(lot3).buyer(buyer1).bidPricePerQuintal(BigDecimal.valueOf(5850.00)).quantityQuintals(BigDecimal.valueOf(80.00)).totalAmount(BigDecimal.valueOf(468000.00)).status(BidStatus.ACTIVE).build());

        // Bids on Lot 4
        bidRepository.save(Bid.builder().lot(lot4).buyer(buyer1).bidPricePerQuintal(BigDecimal.valueOf(2340.00)).quantityQuintals(BigDecimal.valueOf(320.00)).totalAmount(BigDecimal.valueOf(748800.00)).status(BidStatus.ACTIVE).build());
        bidRepository.save(Bid.builder().lot(lot4).buyer(buyer2).bidPricePerQuintal(BigDecimal.valueOf(2310.00)).quantityQuintals(BigDecimal.valueOf(320.00)).totalAmount(BigDecimal.valueOf(739200.00)).status(BidStatus.ACTIVE).build());

        // Bids on Lot 5
        bidRepository.save(Bid.builder().lot(lot5).buyer(buyer1).bidPricePerQuintal(BigDecimal.valueOf(2280.00)).quantityQuintals(BigDecimal.valueOf(180.00)).totalAmount(BigDecimal.valueOf(410400.00)).status(BidStatus.ACTIVE).build());

        // Bids on Lot 6
        bidRepository.save(Bid.builder().lot(lot6).buyer(buyer2).bidPricePerQuintal(BigDecimal.valueOf(5800.00)).quantityQuintals(BigDecimal.valueOf(60.00)).totalAmount(BigDecimal.valueOf(348000.00)).status(BidStatus.ACTIVE).build());

        // Accepted Bids & Contracts
        // Contract 1: In Escrow Funded / Ready for Dispatch status
        Bid acceptedBid1 = Bid.builder()
                .lot(lot7)
                .buyer(buyer1)
                .bidPricePerQuintal(BigDecimal.valueOf(2460.00))
                .quantityQuintals(BigDecimal.valueOf(100.00))
                .totalAmount(BigDecimal.valueOf(246000.00))
                .status(BidStatus.ACCEPTED)
                .build();
        acceptedBid1 = bidRepository.save(acceptedBid1);

        SaleContract contract1 = SaleContract.builder()
                .contractNumber("SC-20261001-7001")
                .lot(lot7)
                .farmer(farmer1)
                .buyer(buyer1)
                .acceptedBid(acceptedBid1)
                .quantityQuintals(BigDecimal.valueOf(100.00))
                .pricePerQuintal(BigDecimal.valueOf(2460.00))
                .totalAmount(BigDecimal.valueOf(246000.00))
                .deliveryTerms("Standard Escrow Terms: Farmer dispatches upon escrow receipt.")
                .dispatchDeadline(today.plusDays(3))
                .deliveryDeadline(today.plusDays(8))
                .status(ContractStatus.ESCROW_FUNDED)
                .build();
        contract1 = saleContractRepository.save(contract1);

        PaymentTransaction payment1 = PaymentTransaction.builder()
                .contract(contract1)
                .payer(buyer1)
                .payee(farmer1)
                .amount(BigDecimal.valueOf(246000.00))
                .paymentMethod(PaymentMethod.UPI)
                .status(PaymentStatus.HELD_IN_ESCROW)
                .transactionReference("TXN-20261001-9001")
                .simulationMode(true)
                .notes("Simulated Escrow Payment - Funds held safely in platform escrow")
                .build();
        paymentRepository.save(payment1);

        // Contract 2: Delivered and Payment Released status
        Bid acceptedBid2 = Bid.builder()
                .lot(lot8)
                .buyer(buyer2)
                .bidPricePerQuintal(BigDecimal.valueOf(5900.00))
                .quantityQuintals(BigDecimal.valueOf(50.00))
                .totalAmount(BigDecimal.valueOf(295000.00))
                .status(BidStatus.ACCEPTED)
                .build();
        acceptedBid2 = bidRepository.save(acceptedBid2);

        SaleContract contract2 = SaleContract.builder()
                .contractNumber("SC-20260925-8002")
                .lot(lot8)
                .farmer(farmer2)
                .buyer(buyer2)
                .acceptedBid(acceptedBid2)
                .quantityQuintals(BigDecimal.valueOf(50.00))
                .pricePerQuintal(BigDecimal.valueOf(5900.00))
                .totalAmount(BigDecimal.valueOf(295000.00))
                .deliveryTerms("Standard Delivery Terms: Delivered and confirmed.")
                .dispatchDeadline(today.minusDays(4))
                .deliveryDeadline(today.minusDays(1))
                .status(ContractStatus.PAYMENT_RELEASED)
                .build();
        contract2 = saleContractRepository.save(contract2);

        PaymentTransaction payment2 = PaymentTransaction.builder()
                .contract(contract2)
                .payer(buyer2)
                .payee(farmer2)
                .amount(BigDecimal.valueOf(295000.00))
                .paymentMethod(PaymentMethod.NET_BANKING)
                .status(PaymentStatus.RELEASED)
                .transactionReference("TXN-20260925-9002")
                .simulationMode(true)
                .notes("Escrow released to farmer bank account upon successful buyer delivery inspection.")
                .build();
        paymentRepository.save(payment2);

        DeliveryRecord del2 = DeliveryRecord.builder()
                .contract(contract2)
                .quantityDeliveredQuintals(BigDecimal.valueOf(50.00))
                .deliveryDate(today.minusDays(2))
                .qualityRemarks("All 50 quintals inspected and found in Grade-A condition. Moisture 7.5% verified.")
                .buyerConfirmed(true)
                .farmerConfirmed(true)
                .status("CONFIRMED")
                .build();
        deliveryRepository.save(del2);

        // 6. Seed Warehouse Booking
        bookingRepository.save(WarehouseBooking.builder()
                .warehouse(wh1)
                .farmer(farmer1)
                .crop("WHEAT")
                .quantityQuintals(BigDecimal.valueOf(200.00))
                .startDate(today.plusDays(2))
                .endDate(today.plusDays(62))
                .totalEstimatedCost(BigDecimal.valueOf(7400.00))
                .status(WarehouseBookingStatus.APPROVED)
                .remarks("Approved for Silo Bay #4")
                .build());

        // 7. Seed Notifications
        notificationRepository.save(Notification.builder()
                .user(farmer1)
                .title("Escrow Funded for Contract #SC-20261001-7001")
                .message("Buyer Haryana Grain Traders has funded ₹246,000 in Escrow. Please arrange dispatch by " + today.plusDays(3))
                .notificationType(NotificationType.ESCROW_FUNDED)
                .isRead(false)
                .referenceId(contract1.getId().toString())
                .referenceType("CONTRACT")
                .build());

        notificationRepository.save(Notification.builder()
                .user(farmer1)
                .title("New Bid Received on Lot LOT-20261001-1001")
                .message("Buyer Vikram Aggarwal placed a bid of ₹2,440.00 / quintal.")
                .notificationType(NotificationType.NEW_BID)
                .isRead(false)
                .referenceId(lot1.getId().toString())
                .referenceType("LOT")
                .build());

        notificationRepository.save(Notification.builder()
                .user(buyer1)
                .title("Contract Created: SC-20261001-7001")
                .message("Your bid was accepted by Ramesh Singh! Please fund simulated escrow to initiate dispatch.")
                .notificationType(NotificationType.BID_ACCEPTED)
                .isRead(true)
                .referenceId(contract1.getId().toString())
                .referenceType("CONTRACT")
                .build());

        notificationRepository.save(Notification.builder()
                .user(farmer2)
                .title("Payment Credited: ₹295,000.00")
                .message("Escrow payment for Contract #SC-20260925-8002 has been released to your registered bank account.")
                .notificationType(NotificationType.PAYMENT_RELEASED)
                .isRead(true)
                .referenceId(contract2.getId().toString())
                .referenceType("CONTRACT")
                .build());

        log.info("--- Prototype Seed Data Loaded Successfully! ---");
    }
}
