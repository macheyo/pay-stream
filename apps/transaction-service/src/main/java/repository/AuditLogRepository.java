package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import models.AuditLog;
import util.SecurityContext;

import java.util.List;

/**
 * Project: pay-stream
 * Module: repository
 * File: AuditLogRepository
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@ApplicationScoped
public class AuditLogRepository implements PanacheRepository<AuditLog>, IAuditLogRepository,  TenantAwareRepository<AuditLog> {
    @Inject
    SecurityContext securityContext;

    @Inject
    EntityManager entityManager;

    @Override
    public void setTenantFilter(AuditLog auditLog) {
        auditLog.setTenantId(securityContext.getTenantId());
    }

    @Override
    public void persist(AuditLog auditLog) {
        auditLog.setTenantId(securityContext.getTenantId());
        entityManager.persist(auditLog);
    }
    @Override
    public List<AuditLog> listAll() {
        return list("tenantId", securityContext.getTenantId());
    }
}
