package com.lambda.services.impl;

import com.lambda.models.entities.Theme;
import com.lambda.repositories.ThemeRepository;
import com.lambda.services.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThemeServiceImpl implements ThemeService {
    @Autowired
    ThemeRepository themeRepository;

    @Override
    public Optional<Theme> findById(Integer id) {
        return themeRepository.findById(id);
    }

    @Override
    public Theme findByName(String name) {
        return themeRepository.findByName(name);
    }

    @Override
    public Page<Theme> findAll(Pageable pageable) {
        return themeRepository.findAll(pageable);
    }

    @Override
    public Page<Theme> findAllByNameContaining(String name, Pageable pageable) {
        return themeRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public void save(Theme theme) {
        themeRepository.saveAndFlush(theme);
    }

    @Override
    public void deleteById(Integer id) {
        themeRepository.deleteById(id);
    }
}
