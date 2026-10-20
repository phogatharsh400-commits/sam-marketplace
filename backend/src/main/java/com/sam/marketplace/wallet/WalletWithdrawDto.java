package com.sam.marketplace.wallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class WalletWithdrawDto {

    @NotNull(message = "Withdrawal amount is required")
    @DecimalMin(value = "10.00", message = "Minimum withdrawal amount is ₹10.00")
    private BigDecimal amount;

    private String withdrawMethod = "BANK_ACCOUNT"; // BANK_ACCOUNT, UPI

    private String bankAccountNumber;

    private String ifscCode;

    private String accountHolderName;

    private String upiId;

    public WalletWithdrawDto() {}

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getWithdrawMethod() { return withdrawMethod; }
    public void setWithdrawMethod(String withdrawMethod) { this.withdrawMethod = withdrawMethod; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
}
