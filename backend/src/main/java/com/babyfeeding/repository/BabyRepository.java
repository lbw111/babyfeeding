package com.babyfeeding.repository;

import com.babyfeeding.entity.Baby;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BabyRepository extends JpaRepository<Baby, String> {
}
