package service;

import dto.ApprovalRequestDTO;
import dto.BulkTransactionRequestDTO;
import dto.RejectionRequestDTO;
import dto.TransactionRequestDTO;
import models.Transaction;

import java.util.List;

/**
 * Project: pay-stream
 * Module: service
 * File: ITransactionService
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public interface ITransactionService {
    Transaction createTransaction(TransactionRequestDTO requestDTO, String userId);
    List<Transaction> createBulkTransactions(BulkTransactionRequestDTO requestDTO, String userId);
    Transaction getTransaction(Long id);
    List<Transaction> getTransactionsByBatchId(String batchId);
    Transaction approveTransaction(Long id, ApprovalRequestDTO approvalDTO, String approverId);
    Transaction rejectTransaction(Long id, RejectionRequestDTO rejectionDTO, String rejecterId);
    List<Transaction> batchApproveTransactions(String batchId, ApprovalRequestDTO approvalDTO, String approverId);
    List<Transaction> batchRejectTransactions(String batchId, RejectionRequestDTO rejectionDTO, String rejecterId);
    List<Transaction> getTransactionsByStatus(String status);
}
