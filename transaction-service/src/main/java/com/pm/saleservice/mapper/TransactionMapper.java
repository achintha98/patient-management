package com.pm.saleservice.mapper;

import com.pm.saleservice.dto.TransactionRequestDTO;
import com.pm.saleservice.dto.TransactionResponseDTO;
import com.pm.saleservice.model.Transaction;
import com.pm.saleservice.model.TransactionOutBox;

/**
 * @author Achintha Kalunayaka
 * @since 9/4/2025
 */


public class TransactionMapper {

    public static TransactionResponseDTO mapToSaleResponseDTO(Transaction transaction) {
        return TransactionResponseDTO.builder().transactionId(transaction.getTransactionId()).
                saleDate(transaction.getSaleDate()).partnerId(transaction.getPartnerId()).amount(transaction.getAmount()).
                currency(transaction.getCurrency()).build();
    }

    public static Transaction mapFromSaleRequestDTO(TransactionRequestDTO transactionRequestDTO) {
        return Transaction.builder().partnerId(transactionRequestDTO.getPartnerId()).
                saleDate(transactionRequestDTO.getSaleDate()).currency(transactionRequestDTO.getCurrency()).
                amount(transactionRequestDTO.getAmount()).
                build();
    }

    public static TransactionOutBox mapFromTransactionToTransactionOutBox(Transaction transaction) {
        return TransactionOutBox.builder().partnerId(transaction.getPartnerId()).
                saleDate(transaction.getSaleDate()).currency(transaction.getCurrency()).
                amount(transaction.getAmount()).
                build();
    }

}
