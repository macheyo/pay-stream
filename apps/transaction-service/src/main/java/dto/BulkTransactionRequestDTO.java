package dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Project: pay-stream
 * Module: dto
 * File: BulkTransactionRequestDTO
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public class BulkTransactionRequestDTO {

    @NotEmpty(message = "At least one transaction is required")
    @Valid
    private List<TransactionRequestDTO> transactions;

    // Getters and Setters
    public List<TransactionRequestDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionRequestDTO> transactions) {
        this.transactions = transactions;
    }
}
