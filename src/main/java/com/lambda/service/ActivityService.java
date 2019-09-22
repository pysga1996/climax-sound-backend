package com.lambda.service;

import com.lambda.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ActivityService {
    Optional<Activity> findById(Integer id);
    Activity findByName(String name);
    Page<Activity> findAll(Pageable pageable);
    Page<Activity> findAllByNameContaining(String name, Pageable pageable);
    void save(Activity mood);
    void deleteById(Integer id);
}
