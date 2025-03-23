package service;

import dto.BankRequestDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import models.Bank;
import repository.IBankRepository;

import java.util.List;

/**
 * Project: pay-stream
 * Module: service
 * File: BankService
 * <p>
 * Created by: justice.m on 22/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@ApplicationScoped
public class BankService implements IBankService {

    @Inject
    IBankRepository bankRepository;

    @Inject
    IAuditService auditService;


    @Override
    @Transactional
    public Bank createBank(BankRequestDTO bankDTO, String userId) {
        Bank bank = new Bank();
        bank.setName(bankDTO.getName());
        bank.setBranchCode(bankDTO.getBranchCode());
        bank.setAddress(bankDTO.getAddress());
        bank.setContactPhone(bankDTO.getContactPhone());
        bank.setContactEmail(bankDTO.getContactEmail());
        bank.setActive(bankDTO.isActive());

        bankRepository.persist(bank);

        // Create audit log
        auditService.logEvent(
                "Bank",
                bank.id,
                "CREATE",
                userId,
                bankDTO
        );

        return bank;
    }

    @Override
    @Transactional
    public Bank updateBank(Long id, BankRequestDTO bankDTO, String userId) {
        Bank bank = getBank(id);

        bank.setName(bankDTO.getName());
        bank.setBranchCode(bankDTO.getBranchCode());
        bank.setAddress(bankDTO.getAddress());
        bank.setContactPhone(bankDTO.getContactPhone());
        bank.setContactEmail(bankDTO.getContactEmail());
        bank.setActive(bankDTO.isActive());

        // Create audit log
        auditService.logEvent(
                "Bank",
                bank.id,
                "UPDATE",
                userId,
                bankDTO
        );

        return bank;
    }

    @Override
    @Transactional
    public void deleteBank(Long id, String userId) {
        Bank bank = getBank(id);
        bank.setActive(false);

        // Create audit log
        auditService.logEvent(
                "Bank",
                bank.id,
                "DELETE",
                userId,
                null
        );
    }

    @Override
    public Bank getBank(Long id) {
        return bankRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Bank not found with id: " + id));
    }

    @Override
    public Bank getBankByBranchCode(String branchCode) {
        return bankRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new NotFoundException("Bank not found with branch code: " + branchCode));
    }

    @Override
    public List<Bank> getAllBanks() {
        return bankRepository.listAll();
    }

    @Override
    public List<Bank> getActiveBanks() {
        return bankRepository.findActiveBank();
    }
}
