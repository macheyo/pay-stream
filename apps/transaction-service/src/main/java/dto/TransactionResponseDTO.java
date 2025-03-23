package dto;

import models.Bank;
import models.Transaction;
import models.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Project: pay-stream
 * Module: dto
 * File: TransactionResponseDTO
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public class TransactionResponseDTO {

    private Long id;
    private String accountName;
    private String accountNumber;
    private Bank bank;
    private String currency;
    private BigDecimal amount;
    private BigDecimal exchangeRate;
    private TransactionStatus status;
    private String batchId;
    private String createdBy;
    private String approvedBy;
    private String rejectedBy;
    private String approvalNotes;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime updatedAt;

    // Constructors
    public TransactionResponseDTO() {}

    public TransactionResponseDTO(Transaction transaction) {
        this.id = transaction.id;
        this.accountName = transaction.getAccountName();
        this.accountNumber = transaction.getAccountNumber();
        this.bank = transaction.getBank();
        this.currency = transaction.getMoney().getCurrency();
        this.amount = transaction.getMoney().getAmount();
        this.exchangeRate = transaction.getMoney().getExchangeRate();
        this.status = transaction.getStatus();
        this.batchId = transaction.getBatchId();
        this.createdBy = transaction.getCreatedBy();
        this.approvedBy = transaction.getApprovedBy();
        this.rejectedBy = transaction.getRejectedBy();
        this.approvalNotes = transaction.getApprovalNotes();
        this.rejectionReason = transaction.getRejectionReason();
        this.createdAt = transaction.getCreatedAt();
        this.approvedAt = transaction.getApprovedAt();
        this.rejectedAt = transaction.getRejectedAt();
        this.updatedAt = transaction.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public String getApprovalNotes() {
        return approvalNotes;
    }

    public void setApprovalNotes(String approvalNotes) {
        this.approvalNotes = approvalNotes;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
