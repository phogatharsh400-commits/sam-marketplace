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
@RequestMapping("/api/farmer/contracts")
@PreAuthorize("hasRole('FARMER')")
@Tag(name = "Farmer Contracts", description = "Farmer specific sale contracts")
public class FarmerContractController {

    private final SaleContractService saleContractService;

    public FarmerContractController(SaleContractService saleContractService) {
        this.saleContractService = saleContractService;
    }

    @GetMapping
    @Operation(summary = "Get all contracts for the currently authenticated farmer")
    public ResponseEntity<ApiResponse<PageResponse<ContractResponseDto>>> getMyContracts(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        PageResponse<ContractResponseDto> contracts = saleContractService.getContractsByFarmer(farmerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(contracts, "Farmer contracts retrieved successfully"));
    }

    @org.springframework.web.bind.annotation.PostMapping("/{id}/dispatch")
    @Operation(summary = "Confirm produce dispatch for a sale contract")
    public ResponseEntity<ApiResponse<ContractResponseDto>> confirmDispatch(
            @org.springframework.web.bind.annotation.PathVariable UUID id) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        ContractResponseDto contract = saleContractService.confirmDispatch(id, farmerId);
        return ResponseEntity.ok(ApiResponse.success(contract, "Produce dispatch confirmed successfully"));
    }
}
