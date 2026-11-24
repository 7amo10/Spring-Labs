package com.spring.lab.security.jwt.service;

import com.spring.lab.security.jwt.entity.BankTransaction;
import com.spring.lab.security.jwt.repository.BankTransactionRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final BankTransactionRepository transactionRepository;

    public TransactionService(BankTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @PreFilter("filterObject.transactionAmt > 0 and (filterObject.senderEmail == authentication.name or hasRole('ADMIN'))")
    public List<BankTransaction> processBatchTransfers(List<BankTransaction> transfers) {
        List<BankTransaction> processed = new ArrayList<>();
        for (BankTransaction tx : transfers) {
            processed.add(transactionRepository.save(tx));
        }
        return processed;
    }

    @Transactional(readOnly = true)
    @PostFilter("filterObject.senderEmail == authentication.name or hasRole('ADMIN')")
    public List<BankTransaction> getTransactionHistory(String customerEmail) {
        return new ArrayList<>(transactionRepository.findBySenderEmailOrderByTransactionDtDesc(customerEmail));
    }
}
