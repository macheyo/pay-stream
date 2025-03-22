package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import models.AuditLog;

/**
 * Project: pay-stream
 * Module: repository
 * File: IAuditRepository
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public interface IAuditLogRepository extends PanacheRepository<AuditLog> {

}
