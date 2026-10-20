package com.sam.marketplace.bid;

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
@RequestMapping("/api/farmer/bids")
@PreAuthorize("hasRole('FARMER')")
@Tag(name = "Farmer Bids", description = "Farmer incoming bids comparison and monitoring")
public class FarmerBidController {

    private final BidService bidService;

    public FarmerBidController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping
    @Operation(summary = "Get all bids placed on lots listed by the currently authenticated farmer")
    public ResponseEntity<ApiResponse<PageResponse<BidResponseDto>>> getFarmerBids(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        PageResponse<BidResponseDto> bids = bidService.getFarmerBids(farmerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bids, "Farmer incoming bids retrieved successfully"));
    }
}
