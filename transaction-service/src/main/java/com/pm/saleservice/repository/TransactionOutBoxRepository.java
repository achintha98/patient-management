package com.pm.saleservice.repository;

import com.pm.saleservice.model.TransactionOutBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Achintha Kalunayaka
 * @since 9/10/2025
 */

@Repository
public interface TransactionOutBoxRepository extends JpaRepository<TransactionOutBox, UUID> {
}
