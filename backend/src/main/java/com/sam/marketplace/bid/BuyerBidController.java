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
@RequestMapping("/api/buyer/bids")
@PreAuthorize("hasRole('BUYER')")
@Tag(name = "Buyer Bids", description = "Buyer specific bid history")
public class BuyerBidController {

    private final BidService bidService;

    public BuyerBidController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping
    @Operation(summary = "Get all bids placed by the currently authenticated buyer")
    public ResponseEntity<ApiResponse<PageResponse<BidResponseDto>>> getMyBids(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID buyerId = SecurityUtils.getCurrentUserId();
        PageResponse<BidResponseDto> bids = bidService.getBuyerBids(buyerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bids, "Buyer bids retrieved successfully"));
    }
}
