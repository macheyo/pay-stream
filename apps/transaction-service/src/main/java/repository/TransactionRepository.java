package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import models.Transaction;
import models.TransactionStatus;
import util.SecurityContext;

import java.util.List;
import java.util.Optional;

/**
 * Project: pay-stream
 * Module: repository
 * File: TransactionRepository
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transaction>, ITransactionRepository, TenantAwareRepository<Transaction> {

    @Inject
    SecurityContext securityContext;

    @Inject
    EntityManager entityManager;

    @Override
    public void setTenantFilter(Transaction transaction) {
        transaction.setTenantId(securityContext.getTenantId());
    }

    @Override
    public void persist(Transaction transaction) {
        transaction.setTenantId(securityContext.getTenantId());
        entityManager.persist(transaction);
    }

    @Override
    public Optional<Transaction> findByIdOptional(Long id) {
        return find("id = ?1 and tenantId = ?2", id, securityContext.getTenantId()).firstResultOptional();
    }
    @Override
    public List<Transaction> findByBatchId(String batchId) {
        return list("tenantId = ?1 and batchId = ?2", securityContext.getTenantId(), batchId);
    }
    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        return list("status", status);
    }
    @Override
    public List<Transaction> listAll() {
        return list("tenantId", securityContext.getTenantId());
    }
}
