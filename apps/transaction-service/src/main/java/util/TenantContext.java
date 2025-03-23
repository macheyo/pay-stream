package util;


import jakarta.enterprise.context.RequestScoped;

/**
 * Project: pay-stream
 * Module: util
 * File: TenantContext
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/

@RequestScoped
public class TenantContext {

    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}

