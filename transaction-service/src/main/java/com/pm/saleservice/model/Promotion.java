package com.pm.saleservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Achintha Kalunayaka
 * @since 9/5/2025
 */

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transactionId", nullable = false)
    private Transaction transaction;  // FK to your sales_projection table

    @Column(length = 50)
    private String promotionCode;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;

    private LocalDateTime appliedAt;

    // getters and setters
}
