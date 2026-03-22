package com.babyfeeding.repository;

import com.babyfeeding.entity.FeedingRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedingRecordRepository extends JpaRepository<FeedingRecord, String> {
    @Query("SELECT fr FROM FeedingRecord fr WHERE fr.babyId = :babyId AND fr.deleted = false")
    List<FeedingRecord> findByBabyIdAndNotDeleted(@Param("babyId") String babyId);

    @Query("SELECT fr FROM FeedingRecord fr WHERE fr.updatedAt > :since")
    List<FeedingRecord> findUpdatedSince(@Param("since") Long since);

    @Query("SELECT fr FROM FeedingRecord fr WHERE fr.babyId = :babyId AND fr.timestamp >= :startOfDay AND fr.timestamp <= :endOfDay")
    List<FeedingRecord> findByBabyIdAndDay(
            @Param("babyId") String babyId,
            @Param("startOfDay") Long startOfDay,
            @Param("endOfDay") Long endOfDay);
}
