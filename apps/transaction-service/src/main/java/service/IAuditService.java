package service;

import jakarta.transaction.Transactional;
import models.AuditLog;
import models.Bank;

import java.util.List;

/**
 * Project: pay-stream
 * Module: service
 * File: IAuditService
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public interface IAuditService {
    @Transactional
    void logEvent(String entityType, Long entityId, String action, String userId, Object details);

    List<AuditLog> getAllAuditLogs();
}
