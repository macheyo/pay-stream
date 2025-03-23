package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import models.Transaction;
import models.TransactionStatus;

import java.util.List;
import java.util.Optional;

/**
 * Project: pay-stream
 * Module: repository
 * File: ITransactionRepository
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public interface ITransactionRepository extends PanacheRepository<Transaction> {
    List<Transaction> findByBatchId(String batchId);
    List<Transaction> findByStatus(TransactionStatus status);

}
