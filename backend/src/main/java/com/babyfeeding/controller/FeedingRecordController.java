package com.babyfeeding.controller;

import com.babyfeeding.entity.FeedingRecord;
import com.babyfeeding.service.FeedingRecordService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/records")
public class FeedingRecordController {
    @Autowired
    private FeedingRecordService feedingRecordService;

    @GetMapping
    public List<FeedingRecord> getRecords(@RequestParam(required = false) Long since) {
        if (since != null) {
            return feedingRecordService.getRecordsSince(since);
        }
        return feedingRecordService.getAllRecords();
    }

    @GetMapping("/{id}")
    public Optional<FeedingRecord> getRecord(@PathVariable String id) {
        return feedingRecordService.getRecordById(id);
    }

    @PostMapping
    public Map<String, String> createRecord(@RequestBody FeedingRecord record) {
        FeedingRecord created = feedingRecordService.create(record);
        return Map.of("id", created.getId());
    }

    @PutMapping("/{id}")
    public Map<String, Boolean> updateRecord(@PathVariable String id, @RequestBody FeedingRecord record) {
        FeedingRecord updated = feedingRecordService.update(id, record);
        return Map.of("success", updated != null);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteRecord(@PathVariable String id) {
        feedingRecordService.delete(id);
        return Map.of("success", true);
    }
}
