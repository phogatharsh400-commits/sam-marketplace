package com.sam.marketplace.contract;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/buyer/contracts")
@PreAuthorize("hasRole('BUYER')")
@Tag(name = "Buyer Contracts", description = "Buyer specific sale contracts")
public class BuyerContractController {

    private final SaleContractService saleContractService;

    public BuyerContractController(SaleContractService saleContractService) {
        this.saleContractService = saleContractService;
    }

    @GetMapping
    @Operation(summary = "Get all contracts for the currently authenticated buyer")
    public ResponseEntity<ApiResponse<PageResponse<ContractResponseDto>>> getMyContracts(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID buyerId = SecurityUtils.getCurrentUserId();
        PageResponse<ContractResponseDto> contracts = saleContractService.getContractsByBuyer(buyerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(contracts, "Buyer contracts retrieved successfully"));
    }
}
