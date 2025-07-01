package com.example.service.Impl;

import com.example.entity.SequenceNumber;
import com.example.repository.SequenceRepository;
import com.example.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SequenceServiceImpl implements SequenceService {

    private static final String PRODUCT_SEQUENCE_NAME = "product_code";
    private static final String PRODUCT_CODE_PREFIX = "SP";

    @Autowired
    private SequenceRepository sequenceRepository;

    @Override
    @Transactional
    public String getNextProductCode() {
        SequenceNumber sequence = sequenceRepository.findBySequenceName(PRODUCT_SEQUENCE_NAME)
                .orElseGet(() -> {
                    // Start from 1 if no sequence exists
                    SequenceNumber newSequence = new SequenceNumber(PRODUCT_SEQUENCE_NAME, 1L);
                    return sequenceRepository.save(newSequence);
                });

        long nextValue = sequence.getCurrentValue();
        sequence.setCurrentValue(nextValue + 1);
        sequenceRepository.save(sequence);

        return PRODUCT_CODE_PREFIX + String.format("%06d", nextValue);
    }
} 