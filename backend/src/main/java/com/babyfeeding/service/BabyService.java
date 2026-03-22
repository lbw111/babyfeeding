package com.babyfeeding.service;

import com.babyfeeding.entity.Baby;
import com.babyfeeding.repository.BabyRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BabyService {
    @Autowired
    private BabyRepository babyRepository;

    public Iterable<Baby> getAllBabies() {
        return babyRepository.findAll();
    }

    public Optional<Baby> getBabyById(String id) {
        return babyRepository.findById(id);
    }

    public Baby create(Baby baby) {
        return babyRepository.save(baby);
    }

    public Baby update(String id, Baby baby) {
        Baby existing = babyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Baby not found: " + id));

        existing.setName(baby.getName());
        existing.setBirthDate(baby.getBirthDate());
        existing.setAvatarPath(baby.getAvatarPath());

        return babyRepository.save(existing);
    }
}
