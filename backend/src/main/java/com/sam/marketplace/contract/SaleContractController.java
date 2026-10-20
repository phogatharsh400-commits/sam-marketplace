package com.sam.marketplace.contract;

import com.sam.marketplace.common.enums.ContractStatus;
import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/contracts")
@Tag(name = "Sale Contracts", description = "Contract Lifecycle and Monitoring")
public class SaleContractController {

    private final SaleContractService saleContractService;

    public SaleContractController(SaleContractService saleContractService) {
        this.saleContractService = saleContractService;
    }

    @GetMapping
    @Operation(summary = "Get all contracts (Admin / Filtered)")
    public ResponseEntity<ApiResponse<PageResponse<ContractResponseDto>>> getAllContracts(
            @RequestParam(required = false) ContractStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<ContractResponseDto> contracts = saleContractService.getAllContracts(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(contracts, "Sale contracts retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale contract details by ID")
    public ResponseEntity<ApiResponse<ContractResponseDto>> getContractById(@PathVariable UUID id) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        ContractResponseDto contract = saleContractService.getContractById(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(contract, "Sale contract details retrieved"));
    }

    @PostMapping("/{id}/dispatch")
    @Operation(summary = "Confirm produce dispatch for a sale contract")
    public ResponseEntity<ApiResponse<ContractResponseDto>> confirmDispatch(@PathVariable UUID id) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        ContractResponseDto contract = saleContractService.confirmDispatch(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(contract, "Produce dispatch confirmed successfully"));
    }
}
