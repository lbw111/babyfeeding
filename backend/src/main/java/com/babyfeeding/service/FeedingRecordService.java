package com.babyfeeding.service;

import com.babyfeeding.entity.FeedingRecord;
import com.babyfeeding.repository.FeedingRecordRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedingRecordService {
    @Autowired
    private FeedingRecordRepository feedingRecordRepository;

    public List<FeedingRecord> getAllRecords() {
        return feedingRecordRepository.findAll();
    }

    public List<FeedingRecord> getRecordsByBaby(String babyId) {
        return feedingRecordRepository.findByBabyIdAndNotDeleted(babyId);
    }

    public List<FeedingRecord> getRecordsSince(Long timestamp) {
        return feedingRecordRepository.findUpdatedSince(timestamp);
    }

    public Optional<FeedingRecord> getRecordById(String id) {
        return feedingRecordRepository.findById(id);
    }

    public FeedingRecord create(FeedingRecord record) {
        long now = System.currentTimeMillis();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        if (record.getDeleted() == null) {
            record.setDeleted(false);
        }
        return feedingRecordRepository.save(record);
    }

    public FeedingRecord update(String id, FeedingRecord record) {
        FeedingRecord existing = feedingRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feeding record not found: " + id));

        existing.setBabyId(record.getBabyId());
        existing.setType(record.getType());
        existing.setTimestamp(record.getTimestamp());
        existing.setAmountMl(record.getAmountMl());
        existing.setMilkType(record.getMilkType());
        existing.setFoodType(record.getFoodType());
        existing.setFoodAmount(record.getFoodAmount());
        existing.setAcceptance(record.getAcceptance());
        existing.setNote(record.getNote());
        existing.setCreatedBy(record.getCreatedBy());
        existing.setCreatedAt(record.getCreatedAt());
        existing.setDeleted(record.getDeleted());
        existing.setUpdatedAt(System.currentTimeMillis());

        return feedingRecordRepository.save(existing);
    }

    public void delete(String id) {
        feedingRecordRepository.findById(id).ifPresent(record -> {
            record.setDeleted(true);
            record.setUpdatedAt(System.currentTimeMillis());
            feedingRecordRepository.save(record);
        });
    }
}
