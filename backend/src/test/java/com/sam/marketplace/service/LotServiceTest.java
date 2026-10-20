package com.sam.marketplace.service;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.LotStatus;
import com.sam.marketplace.common.enums.ProduceGrade;
import com.sam.marketplace.common.enums.RoleType;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.lot.*;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotServiceTest {

    @Mock
    private LotRepository lotRepository;

    @Mock
    private LotImageRepository lotImageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private LotService lotService;

    private User activeFarmer;
    private User pendingFarmer;
    private LotCreateDto validDto;

    @BeforeEach
    void setUp() {
        activeFarmer = User.builder()
                .id(UUID.randomUUID())
                .mobileNumber("9876543210")
                .fullName("Suresh Patel")
                .role(RoleType.FARMER)
                .status(AccountStatus.ACTIVE)
                .build();

        pendingFarmer = User.builder()
                .id(UUID.randomUUID())
                .mobileNumber("9876543211")
                .fullName("Ravi Kumar")
                .role(RoleType.FARMER)
                .status(AccountStatus.PENDING_VERIFICATION)
                .build();

        validDto = new LotCreateDto();
        validDto.setCrop(CropType.WHEAT);
        validDto.setVariety("Sharbati");
        validDto.setQuantityQuintals(BigDecimal.valueOf(100.0));
        validDto.setGrade(ProduceGrade.GRADE_A);
        validDto.setMoisturePercentage(BigDecimal.valueOf(11.5));
        validDto.setState("Haryana");
        validDto.setDistrict("Karnal");
        validDto.setVillage("Nilokheri");
        validDto.setPickupLocation("Farm Gate Nilokheri");
        validDto.setMinPricePerQuintal(BigDecimal.valueOf(2400.0));
        validDto.setAuctionClosingDate(LocalDate.now().plusDays(5));
        validDto.setExpectedDispatchDate(LocalDate.now().plusDays(7));
        validDto.setPublishImmediately(true);
    }

    @Test
    @DisplayName("Successfully create produce lot by active farmer")
    void testCreateLot_Success() {
        when(userRepository.findById(activeFarmer.getId())).thenReturn(Optional.of(activeFarmer));
        when(lotRepository.save(any(ProduceLot.class))).thenAnswer(inv -> {
            ProduceLot lot = inv.getArgument(0);
            lot.setId(UUID.randomUUID());
            return lot;
        });

        LotResponseDto result = lotService.createLot(activeFarmer.getId(), validDto);

        assertNotNull(result);
        assertEquals(CropType.WHEAT, result.getCrop());
        assertEquals(BigDecimal.valueOf(100.0), result.getQuantityQuintals());
        assertEquals(LotStatus.OPEN, result.getStatus());
        verify(lotRepository, times(1)).save(any(ProduceLot.class));
    }

    @Test
    @DisplayName("Fail creating lot if farmer is not ACTIVE (PENDING_VERIFICATION)")
    void testCreateLot_FailsWhenNotActive() {
        when(userRepository.findById(pendingFarmer.getId())).thenReturn(Optional.of(pendingFarmer));

        assertThrows(BusinessRuleException.class, () ->
                lotService.createLot(pendingFarmer.getId(), validDto));

        verify(lotRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fail creating lot if auction closing date is in the past")
    void testCreateLot_FailsWhenClosingDateInPast() {
        when(userRepository.findById(activeFarmer.getId())).thenReturn(Optional.of(activeFarmer));
        validDto.setAuctionClosingDate(LocalDate.now().minusDays(1));

        assertThrows(BadRequestException.class, () ->
                lotService.createLot(activeFarmer.getId(), validDto));

        verify(lotRepository, never()).save(any());
    }
}
