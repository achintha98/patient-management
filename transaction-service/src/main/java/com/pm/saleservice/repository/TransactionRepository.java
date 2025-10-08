package com.pm.saleservice.repository;

import com.pm.saleservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Achintha Kalunayaka
 * @since 9/4/2025
 */

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {


}
