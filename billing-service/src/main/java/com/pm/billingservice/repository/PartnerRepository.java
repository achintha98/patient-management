package com.pm.billingservice.repository;

import com.pm.billingservice.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Achintha Kalunayaka
 * @since 9/19/2025
 */

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
}
