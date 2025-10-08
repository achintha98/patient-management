package com.pm.saleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Achintha Kalunayaka
 * @since 9/4/2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    private Long partnerId;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime saleDate;

}
