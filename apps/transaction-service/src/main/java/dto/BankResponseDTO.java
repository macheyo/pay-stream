package dto;

import models.Bank;

/**
 * Project: pay-stream
 * Module: dto
 * File: BankResponseDTO
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public class BankResponseDTO {

    private Long id;
    private String name;
    private String branchCode;
    private String address;
    private String contactPhone;
    private String contactEmail;
    private boolean active;

    // Constructors
    public BankResponseDTO() {}

    public BankResponseDTO(Bank bank) {
        this.id = bank.id;
        this.name = bank.getName();
        this.branchCode = bank.getBranchCode();
        this.address = bank.getAddress();
        this.contactPhone = bank.getContactPhone();
        this.contactEmail = bank.getContactEmail();
        this.active = bank.isActive();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
