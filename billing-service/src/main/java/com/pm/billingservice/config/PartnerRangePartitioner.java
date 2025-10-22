package com.pm.billingservice.config;

import com.pm.billingservice.repository.PartnerRepository;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Achintha Kalunayaka
 * @since 10/20/2025
 */

@Component
public class PartnerRangePartitioner implements Partitioner {

    @Autowired
    private PartnerRepository partnerRepository;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        Long min = partnerRepository.findMin();
        Long max = partnerRepository.findMax();
        long targetSize = (max - min) / gridSize + 1;

        long start = min;
        long end = start + targetSize - 1;

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putLong("minId", start);
            context.putLong("maxId", Math.min(end, max));
            partitions.put("partition" + i, context);
            start += targetSize;
            end += targetSize;
        }

        return partitions;
    }
}
