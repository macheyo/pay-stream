package util;

import jakarta.enterprise.context.RequestScoped;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: pay-stream
 * Module: util
 * File: SecurityContext
 * <p>
 * Created by: justice.m on 23/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@RequestScoped
public class SecurityContext {
    private String tenantId;
    private String userId;
    private String userEmail;
    private Set<String> roles = new HashSet<>();

    // Getters and setters

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {return userId;}

    public void  setUserId(String userId) {this.userId = userId;}

    public String getUserEmail(){return userEmail;}

    public void setUserEmail(String userEmail){this.userEmail = userEmail;}

    public Set<String> getRoles(){return roles;}

    public void setRoles(Set<String> roles){this.roles = roles;}


    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
