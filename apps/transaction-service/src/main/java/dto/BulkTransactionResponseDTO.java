package dto;

import models.Transaction;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: pay-stream
 * Module: dto
 * File: BulkTransactionResponseDTO
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public class BulkTransactionResponseDTO {

    private String batchId;
    private int transactionCount;
    private List<TransactionResponseDTO> transactions;

    // Constructors
    public BulkTransactionResponseDTO() {}

    public BulkTransactionResponseDTO(String batchId, List<Transaction> transactions) {
        this.batchId = batchId;
        this.transactionCount = transactions.size();
        this.transactions = transactions.stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public List<TransactionResponseDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionResponseDTO> transactions) {
        this.transactions = transactions;
    }
}
