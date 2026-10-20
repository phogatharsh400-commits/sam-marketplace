package com.sam.marketplace.bid;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.contract.ContractResponseDto;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Bids", description = "Bidding operations on produce lots")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping("/lots/{lotId}/bids")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Place a bid on an open produce lot (Buyer only)")
    public ResponseEntity<ApiResponse<BidResponseDto>> placeBid(
            @PathVariable UUID lotId,
            @Valid @RequestBody BidCreateDto dto) {
        UUID buyerId = SecurityUtils.getCurrentUserId();
        BidResponseDto response = bidService.placeBid(buyerId, lotId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Bid placed successfully"));
    }

    @GetMapping("/lots/{lotId}/bids")
    @Operation(summary = "Get all bids for a lot ranked by price descending")
    public ResponseEntity<ApiResponse<List<BidResponseDto>>> getBidsForLot(@PathVariable UUID lotId) {
        List<BidResponseDto> bids = bidService.getBidsForLot(lotId);
        return ResponseEntity.ok(ApiResponse.success(bids, "Bids retrieved for lot"));
    }

    @RequestMapping(value = {
            "/bids/{bidId}/accept",
            "/farmer/lots/{lotId}/bids/{bidId}/accept",
            "/lots/{lotId}/bids/{bidId}/accept"
    }, method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Accept a bid and generate sale contract (Farmer only)")
    public ResponseEntity<ApiResponse<ContractResponseDto>> acceptBid(
            @PathVariable(required = false) UUID lotId,
            @PathVariable UUID bidId,
            @RequestBody(required = false) AcceptBidRequestDto requestDto) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        ContractResponseDto contract = bidService.acceptBid(farmerId, bidId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(contract, "Bid accepted and sale contract generated successfully"));
    }

    @RequestMapping(value = {
            "/bids/{bidId}/reject",
            "/farmer/lots/{lotId}/bids/{bidId}/reject",
            "/lots/{lotId}/bids/{bidId}/reject"
    }, method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Reject a bid (Farmer only)")
    public ResponseEntity<ApiResponse<BidResponseDto>> rejectBid(
            @PathVariable(required = false) UUID lotId,
            @PathVariable UUID bidId) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        BidResponseDto response = bidService.rejectBid(farmerId, bidId);
        return ResponseEntity.ok(ApiResponse.success(response, "Bid rejected successfully"));
    }

    @RequestMapping(value = {
            "/bids/{bidId}/withdraw",
            "/buyer/bids/{bidId}/withdraw"
    }, method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Withdraw an active bid (Buyer only)")
    public ResponseEntity<ApiResponse<BidResponseDto>> withdrawBid(@PathVariable UUID bidId) {
        UUID buyerId = SecurityUtils.getCurrentUserId();
        BidResponseDto response = bidService.withdrawBid(buyerId, bidId);
        return ResponseEntity.ok(ApiResponse.success(response, "Bid withdrawn successfully"));
    }
}
