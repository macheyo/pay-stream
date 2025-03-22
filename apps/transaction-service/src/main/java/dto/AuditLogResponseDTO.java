package dto;

import jakarta.persistence.Column;
import models.AuditLog;
import models.Bank;

import java.time.LocalDateTime;

/**
 * Project: pay-stream
 * Module: dto
 * File: AuditLogResponseDTO
 * <p>
 * Created by: justice.m on 23/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public class AuditLogResponseDTO {

    private Long id;
    private String entityType;
    private Long entityId;
    private String action;
    private String userId;
    private LocalDateTime timestamp;
    private String details;

    // Constructors
    public AuditLogResponseDTO() {}

    public AuditLogResponseDTO(AuditLog auditLog) {
        this.id = auditLog.id;
        this.entityType = auditLog.getEntityType();
        this.entityId = auditLog.getEntityId();
        this.action = auditLog.getAction();
        this.userId = auditLog.getUserId();
        this.timestamp = auditLog.getTimestamp();
        this.details = auditLog.getDetails();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimeStamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

