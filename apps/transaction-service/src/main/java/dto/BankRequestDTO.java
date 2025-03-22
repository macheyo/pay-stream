package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import models.Bank;

/**
 * Project: pay-stream
 * Module: dto
 * File: BankDTO
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public class BankRequestDTO {

    @NotBlank(message = "Bank name is required")
    private String name;

    @NotBlank(message = "Branch code is required")
    @Size(min = 3, max = 20, message = "Branch code must be between 3 and 20 characters")
    private String branchCode;

    private String address;

    private String contactPhone;

    private String contactEmail;

    private boolean active = true;

    public static BankRequestDTO toggleStatus(Bank bank) {
        BankRequestDTO dto = new BankRequestDTO();
        dto.setName(bank.getName());
        dto.setBranchCode(bank.getBranchCode());
        dto.setAddress(bank.getAddress());
        dto.setContactEmail(bank.getContactEmail());
        dto.setContactPhone(bank.getContactPhone());
        dto.setActive(!bank.isActive()); // Toggle the active state
        return dto;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

