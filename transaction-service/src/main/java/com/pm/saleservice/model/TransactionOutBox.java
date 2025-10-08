package com.pm.saleservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Achintha Kalunayaka
 * @since 9/10/2025
 */

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionOutBox {

        @Id
        private Long transactionId;

        @Column(nullable = false)
        private Long partnerId; // FK → partners table (tour guide / translator)

        @Column(nullable = false, precision = 12, scale = 2)
        private BigDecimal amount;

        @Column(nullable = false, length = 10)
        private String currency;

        @Column(nullable = false)
        private LocalDateTime saleDate;
}
