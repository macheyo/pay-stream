package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import models.Bank;

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
public interface IBankRepository extends PanacheRepository<Bank> {
    Optional<Bank> findByBranchCode(String branchCode);
    List<Bank> findActiveBank();
}
