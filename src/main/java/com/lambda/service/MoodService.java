package com.lambda.service;

import com.lambda.model.Mood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MoodService {
    Optional<Mood> findById(Integer id);
    Mood findByName(String name);
    Page<Mood> findAll(Pageable pageable);
    Page<Mood> findAllByNameContaining(String name, Pageable pageable);
    void save(Mood mood);
    void deleteById(Integer id);
}
