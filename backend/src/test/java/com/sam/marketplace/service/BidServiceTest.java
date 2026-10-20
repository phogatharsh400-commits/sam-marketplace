package com.sam.marketplace.service;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.bid.*;
import com.sam.marketplace.common.enums.*;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.contract.ContractResponseDto;
import com.sam.marketplace.contract.SaleContract;
import com.sam.marketplace.contract.SaleContractRepository;
import com.sam.marketplace.lot.LotRepository;
import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @Mock
    private BidRepository bidRepository;

    @Mock
    private LotRepository lotRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SaleContractRepository saleContractRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditService auditService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private BidService bidService;

    private User farmer;
    private User buyer;
    private ProduceLot openLot;

    @BeforeEach
    void setUp() {
        farmer = User.builder()
                .id(UUID.randomUUID())
                .fullName("Farmer Ramesh")
                .mobileNumber("9000000002")
                .role(RoleType.FARMER)
                .status(AccountStatus.ACTIVE)
                .build();

        buyer = User.builder()
                .id(UUID.randomUUID())
                .fullName("Buyer Vikram")
                .mobileNumber("9000000003")
                .role(RoleType.BUYER)
                .status(AccountStatus.ACTIVE)
                .build();

        openLot = ProduceLot.builder()
                .id(UUID.randomUUID())
                .lotNumber("LOT-20261001-1111")
                .farmer(farmer)
                .crop(CropType.WHEAT)
                .variety("PBW-502")
                .quantityQuintals(BigDecimal.valueOf(100.0))
                .minPricePerQuintal(BigDecimal.valueOf(2400.0))
                .auctionClosingDate(LocalDate.now().plusDays(5))
                .expectedDispatchDate(LocalDate.now().plusDays(7))
                .status(LotStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("Successfully place bid when price >= minPrice and quantity <= available")
    void testPlaceBid_Success() {
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(lotRepository.findById(openLot.getId())).thenReturn(Optional.of(openLot));
        when(bidRepository.save(any(Bid.class))).thenAnswer(inv -> {
            Bid b = inv.getArgument(0);
            b.setId(UUID.randomUUID());
            return b;
        });

        BidCreateDto dto = new BidCreateDto();
        dto.setBidPricePerQuintal(BigDecimal.valueOf(2450.0));
        dto.setQuantityQuintals(BigDecimal.valueOf(100.0));

        BidResponseDto result = bidService.placeBid(buyer.getId(), openLot.getId(), dto);

        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(2450.0).compareTo(result.getBidPricePerQuintal()));
        assertEquals(0, BigDecimal.valueOf(245000.0).compareTo(result.getTotalAmount()));
        assertEquals(BidStatus.ACTIVE, result.getStatus());
        verify(bidRepository, times(1)).save(any(Bid.class));
    }

    @Test
    @DisplayName("Reject bid if price is below farmer's minimum acceptable price")
    void testPlaceBid_FailsBelowMinPrice() {
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(lotRepository.findById(openLot.getId())).thenReturn(Optional.of(openLot));

        BidCreateDto dto = new BidCreateDto();
        dto.setBidPricePerQuintal(BigDecimal.valueOf(2300.0)); // Less than 2400
        dto.setQuantityQuintals(BigDecimal.valueOf(100.0));

        assertThrows(BadRequestException.class, () ->
                bidService.placeBid(buyer.getId(), openLot.getId(), dto));

        verify(bidRepository, never()).save(any());
    }

    @Test
    @DisplayName("Accept bid creates sale contract and marks lot as BID_ACCEPTED")
    void testAcceptBid_Success() {
        Bid bid = Bid.builder()
                .id(UUID.randomUUID())
                .lot(openLot)
                .buyer(buyer)
                .bidPricePerQuintal(BigDecimal.valueOf(2450.0))
                .quantityQuintals(BigDecimal.valueOf(100.0))
                .totalAmount(BigDecimal.valueOf(245000.0))
                .status(BidStatus.ACTIVE)
                .build();

        when(bidRepository.findById(bid.getId())).thenReturn(Optional.of(bid));
        when(bidRepository.findByLotIdAndStatus(openLot.getId(), BidStatus.ACTIVE)).thenReturn(List.of(bid));
        when(saleContractRepository.save(any(SaleContract.class))).thenAnswer(inv -> {
            SaleContract sc = inv.getArgument(0);
            sc.setId(UUID.randomUUID());
            return sc;
        });

        ContractResponseDto contract = bidService.acceptBid(farmer.getId(), bid.getId());

        assertNotNull(contract);
        assertEquals(ContractStatus.AWAITING_ESCROW, contract.getStatus());
        assertEquals(BigDecimal.valueOf(245000.0), contract.getTotalAmount());
        assertEquals(LotStatus.BID_ACCEPTED, openLot.getStatus());
        assertEquals(BidStatus.ACCEPTED, bid.getStatus());
    }
}
