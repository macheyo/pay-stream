package models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.math.BigDecimal;

/**
* Project: pay-stream
* Module: PACKAGE_NAME
* File: models.Money
*
* Created by: justice.m on 22/3/2025
* 
* © 2025 justice.m. All rights reserved
**/
@Embeddable
public class Money {
    @NotNull(message = "Currency is required")
    private String currency;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private BigDecimal exchangeRate;

    // Default constructor required by JPA
    public Money() {}

    public Money(String currency, BigDecimal amount, BigDecimal exchangeRate) {
        this.currency = currency;
        this.amount = amount;
        this.exchangeRate = exchangeRate;
    }

    // Getters and Setters
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
}
