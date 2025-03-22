package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.AuditLog;
import repository.AuditLogRepository;

import java.util.List;

/**
 * Project: pay-stream
 * Module: service
 * File: AuditService
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@ApplicationScoped
public class AuditService implements IAuditService{

    @Inject
    AuditLogRepository auditLogRepository;

    @Inject
    ObjectMapper objectMapper;

    @Override
    @Transactional
    public void logEvent(String entityType, Long entityId, String action, String userId, Object details) {
        AuditLog log = new AuditLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setUserId(userId);

        try {
            if (details != null) {
                log.setDetails(objectMapper.writeValueAsString(details));
            }
        } catch (Exception e) {
            log.setDetails("Failed to serialize details: " + e.getMessage());
        }

        auditLogRepository.persist(log);
    }

    @Override
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.listAll();
    }
}
