package com.pm.billingservice.repository;

import com.pm.billingservice.model.PartnerCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Achintha Kalunayaka
 * @since 9/19/2025
 */

@Repository
public interface PartnerCommissionRepository extends JpaRepository<PartnerCommission, Long> {
}
