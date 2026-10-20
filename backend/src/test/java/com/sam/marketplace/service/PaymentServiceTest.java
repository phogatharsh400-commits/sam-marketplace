package com.sam.marketplace.service;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.*;
import com.sam.marketplace.contract.SaleContract;
import com.sam.marketplace.contract.SaleContractRepository;
import com.sam.marketplace.lot.LotRepository;
import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.payment.*;
import com.sam.marketplace.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SaleContractRepository saleContractRepository;

    @Mock
    private LotRepository lotRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PaymentService paymentService;

    private User farmer;
    private User buyer;
    private ProduceLot lot;
    private SaleContract contract;

    @BeforeEach
    void setUp() {
        farmer = User.builder()
                .id(UUID.randomUUID())
                .fullName("Farmer Ramesh")
                .role(RoleType.FARMER)
                .status(AccountStatus.ACTIVE)
                .build();

        buyer = User.builder()
                .id(UUID.randomUUID())
                .fullName("Buyer Vikram")
                .role(RoleType.BUYER)
                .status(AccountStatus.ACTIVE)
                .build();

        lot = ProduceLot.builder()
                .id(UUID.randomUUID())
                .lotNumber("LOT-20261001-9999")
                .farmer(farmer)
                .crop(CropType.WHEAT)
                .status(LotStatus.BID_ACCEPTED)
                .build();

        contract = SaleContract.builder()
                .id(UUID.randomUUID())
                .contractNumber("SC-20261001-8888")
                .lot(lot)
                .farmer(farmer)
                .buyer(buyer)
                .quantityQuintals(BigDecimal.valueOf(100.0))
                .pricePerQuintal(BigDecimal.valueOf(2400.0))
                .totalAmount(BigDecimal.valueOf(240000.0))
                .status(ContractStatus.AWAITING_ESCROW)
                .build();
    }

    @Test
    @DisplayName("Fund escrow updates payment to HELD_IN_ESCROW, contract to ESCROW_FUNDED, and lot to READY_FOR_DISPATCH")
    void testFundEscrow_AutoSuccess() {
        when(saleContractRepository.findById(contract.getId())).thenReturn(Optional.of(contract));
        when(paymentRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> {
            PaymentTransaction p = inv.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        PaymentInitiateDto dto = new PaymentInitiateDto();
        dto.setPaymentMethod(PaymentMethod.UPI);
        dto.setAutoSimulateSuccess(true);
        dto.setIdempotencyKey("IDEMP-KEY-12345");

        PaymentResponseDto response = paymentService.fundEscrow(buyer.getId(), contract.getId(), dto);

        assertNotNull(response);
        assertEquals(PaymentStatus.HELD_IN_ESCROW, response.getStatus());
        assertEquals(ContractStatus.ESCROW_FUNDED, contract.getStatus());
        assertEquals(LotStatus.READY_FOR_DISPATCH, lot.getStatus());
        verify(paymentRepository, times(1)).save(any(PaymentTransaction.class));
    }

    @Test
    @DisplayName("Release payment updates payment to RELEASED, contract to PAYMENT_RELEASED, and lot to PAID")
    void testReleasePayment_Success() {
        PaymentTransaction payment = PaymentTransaction.builder()
                .id(UUID.randomUUID())
                .contract(contract)
                .payer(buyer)
                .payee(farmer)
                .amount(BigDecimal.valueOf(240000.0))
                .status(PaymentStatus.HELD_IN_ESCROW)
                .transactionReference("TXN-123456")
                .build();

        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponseDto response = paymentService.releasePayment(payment.getId(), buyer.getId());

        assertNotNull(response);
        assertEquals(PaymentStatus.RELEASED, response.getStatus());
        assertEquals(ContractStatus.PAYMENT_RELEASED, contract.getStatus());
        assertEquals(LotStatus.PAID, lot.getStatus());
    }
}
