package com.babyfeeding.controller;

import com.babyfeeding.entity.Baby;
import com.babyfeeding.service.BabyService;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/babies")
public class BabyController {
    @Autowired
    private BabyService babyService;

    @GetMapping
    public Iterable<Baby> getAllBabies() {
        return babyService.getAllBabies();
    }

    @GetMapping("/{id}")
    public Optional<Baby> getBaby(@PathVariable String id) {
        return babyService.getBabyById(id);
    }

    @PostMapping
    public Map<String, String> createBaby(@RequestBody Baby baby) {
        Baby created = babyService.create(baby);
        return Map.of("id", created.getId());
    }

    @PutMapping("/{id}")
    public Map<String, Boolean> updateBaby(@PathVariable String id, @RequestBody Baby baby) {
        Baby updated = babyService.update(id, baby);
        return Map.of("success", updated != null);
    }
}
