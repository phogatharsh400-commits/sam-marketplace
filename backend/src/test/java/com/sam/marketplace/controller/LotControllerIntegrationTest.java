package com.sam.marketplace.controller;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.ProduceGrade;
import com.sam.marketplace.lot.LotCreateDto;
import com.sam.marketplace.lot.LotResponseDto;
import com.sam.marketplace.lot.LotService;
import com.sam.marketplace.security.CustomUserDetailsService;
import com.sam.marketplace.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LotControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Public user can browse open lots in marketplace")
    void testGetOpenLots_PublicAccess() throws Exception {
        mockMvc.perform(get("/api/lots")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Public user can fetch benchmark prices")
    void testGetBenchmarks_PublicAccess() throws Exception {
        mockMvc.perform(get("/api/benchmarks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Public user can fetch MSP list")
    void testGetMsp_PublicAccess() throws Exception {
        mockMvc.perform(get("/api/msp")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
