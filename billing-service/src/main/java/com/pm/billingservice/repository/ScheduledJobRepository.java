package com.pm.billingservice.repository;

import com.pm.billingservice.model.ScheduledJobRun;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Achintha Kalunayaka
 * @since 10/7/2025
 */

@Repository
public class ScheduledJobRepository {

    @PersistenceContext
    private EntityManager em;

    public boolean hasRunThisMonth(String jobName, LocalDateTime now) {
        ScheduledJobRun jobRun = em.find(ScheduledJobRun.class, jobName);
        if (jobRun == null) return false;

        LocalDateTime lastRunDate = jobRun.getLastRun();
        LocalDateTime firstDayOfMonth = now.withDayOfMonth(1);
        return !lastRunDate.isBefore(firstDayOfMonth);
    }

    @Transactional
    public void updateLastRun(String jobName, LocalDateTime now) {
        ScheduledJobRun jobRun = em.find(ScheduledJobRun.class, jobName);
        if (jobRun == null) {
            em.persist(new ScheduledJobRun(jobName, now));
        } else {
            jobRun.setLastRun(now);
            em.merge(jobRun);
        }
    }

}
