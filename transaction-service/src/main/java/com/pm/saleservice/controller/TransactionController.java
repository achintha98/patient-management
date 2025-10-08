package com.pm.saleservice.controller;

import com.pm.saleservice.dto.TransactionRequestDTO;
import com.pm.saleservice.dto.TransactionResponseDTO;
import com.pm.saleservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Achintha Kalunayaka
 * @since 9/4/2025
 */

@RestController(value = "sale")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createSale(@RequestBody TransactionRequestDTO transactionRequestDTO) {
        TransactionResponseDTO transactionResponseDTO = transactionService.createSale(transactionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTO);
    }
}
