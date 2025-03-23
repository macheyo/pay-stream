package service;

import dto.ApprovalRequestDTO;
import dto.BulkTransactionRequestDTO;
import dto.RejectionRequestDTO;
import dto.TransactionRequestDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import models.Bank;
import models.Transaction;
import models.TransactionStatus;
import repository.IBankRepository;
import repository.ITransactionRepository;
import repository.TransactionRepository;
import util.TenantContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Project: pay-stream
 * Module: service
 * File: TransactionService
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@ApplicationScoped
public class TransactionService implements ITransactionService {

    @Inject
    ITransactionRepository transactionRepository;

    @Inject
    IBankRepository bankRepository;

    @Inject
    IAuditService auditService;

    @Override
    @Transactional
    public Transaction createTransaction(TransactionRequestDTO requestDTO, String userId) {
        // Find the bank by branch code
        Bank bank = bankRepository.findByBranchCode(requestDTO.getBankBranchCode())
                .orElseThrow(() -> new BadRequestException("Bank not found with branch code: " + requestDTO.getBankBranchCode()));

        if (!bank.isActive()) {
            throw new BadRequestException("Bank with branch code " + requestDTO.getBankBranchCode() + " is inactive");
        }

        Transaction transaction = requestDTO.toEntity();
        transaction.setBank(bank);
        transaction.setCreatedBy(userId);
        transactionRepository.persist(transaction);

        // Create audit log
        auditService.logEvent(
                "Transaction",
                transaction.id,
                "CREATE",
                userId,
                requestDTO
        );

        return transaction;
    }

    @Override
    @Transactional
    public List<Transaction> createBulkTransactions(BulkTransactionRequestDTO requestDTO, String userId) {
        String batchId = UUID.randomUUID().toString();
        List<Transaction> transactions = new ArrayList<>();
        for (TransactionRequestDTO dto : requestDTO.getTransactions()) {
            // Find the bank by branch code
            Bank bank = bankRepository.findByBranchCode(dto.getBankBranchCode())
                    .orElseThrow(() -> new BadRequestException("Bank not found with branch code: " + dto.getBankBranchCode()));

            if (!bank.isActive()) {
                throw new BadRequestException("Bank with branch code " + dto.getBankBranchCode() + " is inactive");
            }

            Transaction transaction = dto.toEntity();
            transaction.setBank(bank);
            transaction.setBatchId(batchId);
            transaction.setCreatedBy(userId);
            transactionRepository.persist(transaction);
            transactions.add(transaction);

            // Create audit log for each transaction
            auditService.logEvent(
                    "Transaction",
                    transaction.id,
                    "CREATE",
                    userId,
                    dto
            );
        }

        // Create audit log for the batch
        auditService.logEvent(
                "TransactionBatch",
                null,
                "CREATE",
                userId,
                new Object() {
                    public final String batchIdentifier = batchId;
                    public final int count = transactions.size();
                }
        );

        return transactions;
    }

    @Override
    public Transaction getTransaction(Long id) {
        return transactionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found with id: " + id));
    }

    @Override
    public List<Transaction> getTransactionsByBatchId(String batchId) {
        return transactionRepository.findByBatchId(batchId);
    }

    @Override
    @Transactional
    public Transaction approveTransaction(Long id, ApprovalRequestDTO approvalDTO, String approverId) {
        Transaction transaction = getTransaction(id);

        if (transaction.getStatus() != TransactionStatus.PENDING_APPROVAL) {
            throw new BadRequestException("Only pending transactions can be approved");
        }

        // Can't approve your own transactions
        if (approverId.equals(transaction.getCreatedBy())) {
            throw new BadRequestException("Cannot approve a transaction you created");
        }

        transaction.setStatus(TransactionStatus.APPROVED);
        transaction.setApprovedBy(approverId);
        transaction.setApprovedAt(LocalDateTime.now());
        transaction.setApprovalNotes(approvalDTO.getNotes());

        // Create audit log
        auditService.logEvent(
                "Transaction",
                transaction.id,
                "APPROVE",
                approverId,
                approvalDTO
        );

        return transaction;
    }

    @Override
    @Transactional
    public Transaction rejectTransaction(Long id, RejectionRequestDTO rejectionDTO, String rejecterId) {
        Transaction transaction = getTransaction(id);

        if (transaction.getStatus() != TransactionStatus.PENDING_APPROVAL) {
            throw new BadRequestException("Only pending transactions can be rejected");
        }

        transaction.setStatus(TransactionStatus.REJECTED);
        transaction.setRejectedBy(rejecterId);
        transaction.setRejectedAt(LocalDateTime.now());
        transaction.setRejectionReason(rejectionDTO.getReason());

        // Create audit log
        auditService.logEvent(
                "Transaction",
                transaction.id,
                "REJECT",
                rejecterId,
                rejectionDTO
        );

        return transaction;
    }

    @Override
    public List<Transaction> getTransactionsByStatus(String status) {
        try {
            TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
            return transactionRepository.findByStatus(transactionStatus);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }
    }
}