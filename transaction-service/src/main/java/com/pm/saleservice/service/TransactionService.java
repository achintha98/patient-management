package com.pm.saleservice.service;

import com.pm.saleservice.dto.TransactionRequestDTO;
import com.pm.saleservice.dto.TransactionResponseDTO;
import com.pm.saleservice.mapper.TransactionMapper;
import com.pm.saleservice.model.Transaction;
import com.pm.saleservice.repository.TransactionOutBoxRepository;
import com.pm.saleservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Achintha Kalunayaka
 * @since 9/4/2025
 */

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionOutBoxRepository transactionOutBoxRepository;

    @Transactional
    public TransactionResponseDTO createSale(TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = transactionRepository.save(TransactionMapper.mapFromSaleRequestDTO(transactionRequestDTO));
        transactionOutBoxRepository.save(TransactionMapper.mapFromTransactionToTransactionOutBox(transaction));
        return TransactionMapper.mapToSaleResponseDTO(transaction);
    }
}
