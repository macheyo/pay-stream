package repository;


import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import models.Bank;
import util.TenantContext;

import java.util.List;
import java.util.Optional;

/**
 * Project: pay-stream
 * Module: repository
 * File: BankRepository
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@ApplicationScoped
public class BankRepository implements PanacheRepository<Bank>, IBankRepository, TenantAwareRepository<Bank> {

    @Inject
    TenantContext tenantContext;

    @Inject
    EntityManager entityManager;

    @Override
    public void setTenantFilter(Bank bank) {
        bank.setTenantId(tenantContext.getTenantId());
    }

    @Override
    public void persist(Bank bank) {
        bank.setTenantId(tenantContext.getTenantId());
        entityManager.persist(bank);
    }

    @Override
    public Optional<Bank> findByIdOptional(Long id) {
        return find("id = ?1 and tenantId = ?2", id, tenantContext.getTenantId()).firstResultOptional();
    }

    @Override
    public Optional<Bank> findByBranchCode(String branchCode) {
        return find("branchCode = ?1 and tenantId = ?2", branchCode, tenantContext.getTenantId()).firstResultOptional();
    }

    @Override
    public List<Bank> findActiveBank() {
        return list("active = true and tenantId = ?1", tenantContext.getTenantId());
    }

    @Override
    public List<Bank> listAll() {
        return list("tenantId", tenantContext.getTenantId());
    }

}
