package service;

import dto.BankRequestDTO;
import models.Bank;

import java.util.List;

/**
 * Project: pay-stream
 * Module: service
 * File: IBankService
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
public interface IBankService {
    Bank createBank(BankRequestDTO bankRequestDTO, String userId);
    Bank updateBank(Long id, BankRequestDTO bankRequestDTO, String userId);
    void deleteBank(Long id, String userId);
    Bank getBank(Long id);
    Bank getBankByBranchCode(String branchCode);
    List<Bank> getAllBanks();
    List<Bank> getActiveBanks();
}
