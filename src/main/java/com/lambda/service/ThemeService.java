package com.lambda.service;

import com.lambda.model.entity.Theme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ThemeService {
    Optional<Theme> findById(Integer id);
    Theme findByName(String name);
    Page<Theme> findAll(Pageable pageable);
    Page<Theme> findAllByNameContaining(String name, Pageable pageable);
    void save(Theme mood);
    void deleteById(Integer id);
}
