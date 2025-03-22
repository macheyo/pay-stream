package models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Project: pay-stream
 * Module: models
 * File: Bank
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/

@Entity
@Table(name = "banks", uniqueConstraints = {
        @UniqueConstraint(name = "unique_branch_code_per_tenant", columnNames = {"branch_code", "tenant_id"})
})
public class Bank extends PanacheEntity {
    @Column(name = "tenant_id", nullable = false)
    public String tenantId;
    @NotBlank(message = "Bank name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Branch code is required")
    @Size(min = 3, max = 20, message = "Branch code must be between 3 and 20 characters")
    @Column(name = "branch_code", nullable = false, unique = true)
    private String branchCode;

    @Column(name = "address")
    private String address;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
