package com.sam.marketplace.wallet;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@Tag(name = "Wallet & Token Vault", description = "User deposit token vault, balances, bank withdrawal, and pledge liquidity")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    @Operation(summary = "Get currently authenticated user's wallet, token balances, and stats")
    public ResponseEntity<ApiResponse<WalletDto>> getWallet() {
        UUID userId = SecurityUtils.getCurrentUserId();
        WalletDto dto = walletService.getWalletDto(userId);
        return ResponseEntity.ok(ApiResponse.success(dto, "Wallet retrieved successfully"));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')")
    @Operation(summary = "Deposit money into SAM Security Vault and generate Escrow Bidding Tokens (Buyers & Admin)")
    public ResponseEntity<ApiResponse<WalletTransactionDto>> deposit(@Valid @RequestBody WalletDepositDto depositDto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        WalletTransactionDto tx = walletService.deposit(userId, depositDto);
        return ResponseEntity.ok(ApiResponse.success(tx, "Funds deposited successfully and security tokens generated"));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw / Return money back to bank account or UPI (Redeem tokens or cash out earnings)")
    public ResponseEntity<ApiResponse<WalletTransactionDto>> withdraw(@Valid @RequestBody WalletWithdrawDto withdrawDto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        WalletTransactionDto tx = walletService.withdraw(userId, withdrawDto);
        return ResponseEntity.ok(ApiResponse.success(tx, "Withdrawal processed successfully. Funds returned to your account."));
    }

    @PostMapping("/enwr-advance")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Unique Agricultural Finance: Request 70% instant pledge liquidity advance against stored e-NWR receipt")
    public ResponseEntity<ApiResponse<WalletTransactionDto>> requestENWRAdvance(
            @RequestParam UUID bookingId,
            @RequestParam BigDecimal amount) {
        UUID farmerId = SecurityUtils.getCurrentUserId();
        WalletTransactionDto tx = walletService.requestENWRPledgeAdvance(farmerId, bookingId, amount);
        return ResponseEntity.ok(ApiResponse.success(tx, "e-NWR pledge liquidity advance credited to your vault"));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get wallet passbook transaction history with pagination")
    public ResponseEntity<ApiResponse<Page<WalletTransactionDto>>> getTransactions(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Page<WalletTransactionDto> txs = walletService.getTransactions(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(txs, "Wallet transactions retrieved successfully"));
    }

    @GetMapping("/recent-transactions")
    @Operation(summary = "Get top 20 recent wallet transactions for passbook view")
    public ResponseEntity<ApiResponse<List<WalletTransactionDto>>> getRecentTransactions() {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<WalletTransactionDto> txs = walletService.getRecentTransactions(userId);
        return ResponseEntity.ok(ApiResponse.success(txs, "Recent transactions retrieved successfully"));
    }
}
