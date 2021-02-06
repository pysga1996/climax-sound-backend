package com.alpha.service.impl;

import com.alpha.model.entity.Theme;
import com.alpha.repositories.ThemeRepository;
import com.alpha.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ThemeServiceImpl implements ThemeService {

    private final ThemeRepository themeRepository;

    @Autowired
    public ThemeServiceImpl(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

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
