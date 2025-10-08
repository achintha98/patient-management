package com.pm.saleservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Achintha Kalunayaka
 * @since 9/4/2025
 */

@Builder
public class TransactionResponseDTO {

    private Long transactionId;

    private Long partnerId;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime saleDate;

}
