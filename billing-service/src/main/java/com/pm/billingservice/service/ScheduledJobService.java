package com.pm.billingservice.service;

import com.pm.billingservice.model.ScheduledJobRun;
import com.pm.billingservice.repository.ScheduledJobRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Achintha Kalunayaka
 * @since 10/7/2025
 */

@Service
public class ScheduledJobService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ScheduledJobRepository scheduledJobRepository;

    public boolean hasRunThisMonth(String jobName, LocalDateTime now) {
        return scheduledJobRepository.findById(jobName).map(jobRun ->
                {
                    LocalDateTime lastRunDate = jobRun.getLastRun();
                    LocalDateTime firstDayOfMonth = now.withDayOfMonth(1);
                    return !lastRunDate.isBefore(firstDayOfMonth);
                }
                ).orElse( false);
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
