package com.pm.billingservice.repository;

import com.pm.billingservice.model.ScheduledJobRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Achintha Kalunayaka
 * @since 10/8/2025
 */

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJobRun, String> {

}
