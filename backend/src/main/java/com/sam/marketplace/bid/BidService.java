package com.sam.marketplace.bid;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.*;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.exception.UnauthorizedException;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.contract.ContractResponseDto;
import com.sam.marketplace.contract.SaleContract;
import com.sam.marketplace.contract.SaleContractRepository;
import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.lot.LotRepository;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import com.sam.marketplace.wallet.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BidService {

    private static final Logger log = LoggerFactory.getLogger(BidService.class);

    private final BidRepository bidRepository;
    private final LotRepository lotRepository;
    private final UserRepository userRepository;
    private final SaleContractRepository saleContractRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WalletService walletService;

    public BidService(BidRepository bidRepository,
                      LotRepository lotRepository,
                      UserRepository userRepository,
                      SaleContractRepository saleContractRepository,
                      NotificationService notificationService,
                      AuditService auditService,
                      SimpMessagingTemplate messagingTemplate,
                      WalletService walletService) {
        this.bidRepository = bidRepository;
        this.lotRepository = lotRepository;
        this.userRepository = userRepository;
        this.saleContractRepository = saleContractRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.messagingTemplate = messagingTemplate;
        this.walletService = walletService;
    }

    @Transactional
    public BidResponseDto placeBid(UUID buyerId, UUID lotId, BidCreateDto dto) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with id: " + buyerId));

        if (buyer.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE and verified buyers can place bids. Current status: " + buyer.getStatus());
        }

        ProduceLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Produce lot not found with id: " + lotId));

        if (lot.getStatus() != LotStatus.OPEN) {
            throw new BusinessRuleException("Cannot place bid on lot in status: " + lot.getStatus());
        }

        if (lot.getAuctionClosingDate() != null && lot.getAuctionClosingDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Auction closing date has passed for this produce lot");
        }

        if (dto.getBidPricePerQuintal().compareTo(lot.getMinPricePerQuintal()) < 0) {
            throw new BadRequestException("Bid price (" + dto.getBidPricePerQuintal() + " /qtl) cannot be below the farmer's minimum acceptable price (" + lot.getMinPricePerQuintal() + " /qtl)");
        }

        if (dto.getQuantityQuintals().compareTo(lot.getQuantityQuintals()) > 0) {
            throw new BadRequestException("Bid quantity (" + dto.getQuantityQuintals() + " qtl) cannot exceed available lot quantity (" + lot.getQuantityQuintals() + " qtl)");
        }

        BigDecimal totalAmount = dto.getBidPricePerQuintal().multiply(dto.getQuantityQuintals());

        Bid bid = Bid.builder()
                .lot(lot)
                .buyer(buyer)
                .bidPricePerQuintal(dto.getBidPricePerQuintal())
                .quantityQuintals(dto.getQuantityQuintals())
                .totalAmount(totalAmount)
                .status(BidStatus.ACTIVE)
                .build();

        Bid savedBid = bidRepository.save(bid);

        // Lock Security Deposit Tokens in Buyer's Wallet
        try {
            walletService.lockFundsForBid(buyerId, totalAmount, savedBid.getId().toString());
        } catch (Exception e) {
            log.warn("Wallet locking handled gracefully: {}", e.getMessage());
        }

        BidResponseDto responseDto = BidResponseDto.from(savedBid);

        try {
            messagingTemplate.convertAndSend("/topic/bids/" + lotId, responseDto);
        } catch (Exception e) {
            log.warn("Failed to broadcast bid via websocket: {}", e.getMessage());
        }

        notificationService.createNotification(
                lot.getFarmer().getId(),
                "New Bid Placed",
                "Buyer " + buyer.getFullName() + " placed a bid of ₹" + dto.getBidPricePerQuintal() + "/qtl on lot " + lot.getLotNumber(),
                NotificationType.NEW_BID,
                savedBid.getId().toString(),
                "BID"
        );

        auditService.logAction(buyerId, AuditAction.BID_PLACED, "BID",
                savedBid.getId().toString(), "Bid placed: ₹" + dto.getBidPricePerQuintal() + "/qtl on lot " + lot.getLotNumber(), null);

        return responseDto;
    }

    @Transactional(readOnly = true)
    public List<BidResponseDto> getBidsForLot(UUID lotId) {
        return bidRepository.findByLotIdOrderByBidPricePerQuintalDesc(lotId).stream()
                .map(BidResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<BidResponseDto> getBuyerBids(UUID buyerId, Pageable pageable) {
        Page<Bid> page = bidRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId, pageable);
        return PageResponse.from(page.map(BidResponseDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<BidResponseDto> getFarmerBids(UUID farmerId, Pageable pageable) {
        Page<Bid> page = bidRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId, pageable);
        return PageResponse.from(page.map(BidResponseDto::from));
    }

    @Transactional
    public ContractResponseDto acceptBid(UUID farmerId, UUID bidId) {
        return acceptBid(farmerId, bidId, null);
    }

    @Transactional
    public ContractResponseDto acceptBid(UUID farmerId, UUID bidId, AcceptBidRequestDto requestDto) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with id: " + bidId));

        ProduceLot lot = bid.getLot();

        if (!lot.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedException("You do not own this produce lot and cannot accept bids on it");
        }

        if (lot.getStatus() != LotStatus.OPEN) {
            throw new BusinessRuleException("Cannot accept bid: Lot is no longer OPEN. Current status: " + lot.getStatus());
        }

        if (bid.getStatus() != BidStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE bids can be accepted. Current bid status: " + bid.getStatus());
        }

        bid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);

        // Reject other competing bids and unlock their deposit tokens back to buyers
        List<Bid> otherBids = bidRepository.findByLotIdAndStatus(lot.getId(), BidStatus.ACTIVE);
        for (Bid other : otherBids) {
            if (!other.getId().equals(bid.getId())) {
                other.setStatus(BidStatus.REJECTED);
                bidRepository.save(other);

                // Refund / Unlock other buyer's tokens
                try {
                    walletService.unlockFundsForBid(other.getBuyer().getId(), other.getTotalAmount(), other.getId().toString(), "Outbid / Lot Finalized");
                } catch (Exception e) {
                    log.warn("Wallet unlock for other bid handled gracefully: {}", e.getMessage());
                }

                notificationService.createNotification(
                        other.getBuyer().getId(),
                        "Bid Status Update",
                        "Your bid on lot " + lot.getLotNumber() + " was released. Tokens refunded to your wallet.",
                        NotificationType.BID_REJECTED,
                        other.getId().toString(),
                        "BID"
                );
            }
        }

        lot.setStatus(LotStatus.BID_ACCEPTED);
        lotRepository.save(lot);

        String contractNumber = generateContractNumber();

        // Calculate deadlines safely without NPE
        LocalDate dispatchDeadline = (lot.getExpectedDispatchDate() != null)
                ? lot.getExpectedDispatchDate()
                : LocalDate.now().plusDays(5);

        if (requestDto != null && requestDto.getDispatchDeadline() != null && !requestDto.getDispatchDeadline().isBlank()) {
            try {
                dispatchDeadline = LocalDate.parse(requestDto.getDispatchDeadline().substring(0, 10));
            } catch (Exception ignored) {}
        }

        LocalDate deliveryDeadline = dispatchDeadline.plusDays(5);
        if (requestDto != null && requestDto.getDeliveryDeadline() != null && !requestDto.getDeliveryDeadline().isBlank()) {
            try {
                deliveryDeadline = LocalDate.parse(requestDto.getDeliveryDeadline().substring(0, 10));
            } catch (Exception ignored) {}
        }

        String deliveryTerms = (requestDto != null && requestDto.getDeliveryTerms() != null && !requestDto.getDeliveryTerms().isBlank())
                ? requestDto.getDeliveryTerms()
                : "Standard Agmarknet Terms: Farmer dispatches upon escrow funding; Buyer inspects upon delivery.";

        SaleContract contract = SaleContract.builder()
                .contractNumber(contractNumber)
                .lot(lot)
                .farmer(lot.getFarmer())
                .buyer(bid.getBuyer())
                .acceptedBid(bid)
                .quantityQuintals(bid.getQuantityQuintals())
                .pricePerQuintal(bid.getBidPricePerQuintal())
                .totalAmount(bid.getTotalAmount())
                .deliveryTerms(deliveryTerms)
                .dispatchDeadline(dispatchDeadline)
                .deliveryDeadline(deliveryDeadline)
                .status(ContractStatus.AWAITING_ESCROW)
                .build();

        SaleContract savedContract = saleContractRepository.save(contract);

        notificationService.createNotification(
                bid.getBuyer().getId(),
                "Bid Accepted - Contract Created!",
                "Your bid on lot " + lot.getLotNumber() + " was accepted! Contract #" + contractNumber + " generated. Please fund escrow to begin dispatch.",
                NotificationType.BID_ACCEPTED,
                savedContract.getId().toString(),
                "CONTRACT"
        );

        notificationService.createNotification(
                farmerId,
                "Bid Accepted - Contract Created",
                "You accepted bid from " + bid.getBuyer().getFullName() + ". Contract #" + contractNumber + " created.",
                NotificationType.BID_ACCEPTED,
                savedContract.getId().toString(),
                "CONTRACT"
        );

        auditService.logAction(farmerId, AuditAction.BID_ACCEPTED, "BID",
                bid.getId().toString(), "Farmer accepted bid #" + bid.getId() + " on lot " + lot.getLotNumber(), null);

        auditService.logAction(farmerId, AuditAction.CONTRACT_CREATED, "CONTRACT",
                savedContract.getId().toString(), "Sale contract created: " + contractNumber, null);

        return ContractResponseDto.from(savedContract);
    }

    @Transactional
    public BidResponseDto rejectBid(UUID farmerId, UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with id: " + bidId));

        if (!bid.getLot().getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedException("You do not own this produce lot");
        }

        if (bid.getStatus() != BidStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE bids can be rejected");
        }

        bid.setStatus(BidStatus.REJECTED);
        Bid saved = bidRepository.save(bid);

        // Refund / Unlock security deposit tokens back to buyer
        try {
            walletService.unlockFundsForBid(bid.getBuyer().getId(), bid.getTotalAmount(), bid.getId().toString(), "Farmer Rejected Bid");
        } catch (Exception e) {
            log.warn("Wallet unlock handled gracefully: {}", e.getMessage());
        }

        notificationService.createNotification(
                bid.getBuyer().getId(),
                "Bid Rejected",
                "Your bid on lot " + bid.getLot().getLotNumber() + " was declined by the farmer. Tokens unlocked.",
                NotificationType.BID_REJECTED,
                bid.getId().toString(),
                "BID"
        );

        auditService.logAction(farmerId, AuditAction.BID_REJECTED, "BID",
                bid.getId().toString(), "Farmer rejected bid #" + bid.getId(), null);

        return BidResponseDto.from(saved);
    }

    @Transactional
    public BidResponseDto withdrawBid(UUID buyerId, UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with id: " + bidId));

        if (!bid.getBuyer().getId().equals(buyerId)) {
            throw new UnauthorizedException("You do not own this bid");
        }

        if (bid.getStatus() != BidStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE bids can be withdrawn");
        }

        bid.setStatus(BidStatus.WITHDRAWN);
        Bid saved = bidRepository.save(bid);

        // Refund / Unlock security deposit tokens back to buyer's available wallet balance
        try {
            walletService.unlockFundsForBid(buyerId, bid.getTotalAmount(), bid.getId().toString(), "Buyer Withdrew Bid");
        } catch (Exception e) {
            log.warn("Wallet unlock handled gracefully: {}", e.getMessage());
        }

        auditService.logAction(buyerId, AuditAction.BID_WITHDRAWN, "BID",
                bid.getId().toString(), "Buyer withdrew bid #" + bid.getId(), null);

        return BidResponseDto.from(saved);
    }

    private String generateContractNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SC-" + dateStr + "-" + rand;
    }
}
