package com.alpha.service.impl;

import com.alpha.mapper.ThemeMapper;
import com.alpha.model.dto.ThemeDTO;
import com.alpha.model.entity.Theme;
import com.alpha.repositories.ThemeRepository;
import com.alpha.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ThemeServiceImpl implements ThemeService {

    private final ThemeRepository themeRepository;

    private final ThemeMapper themeMapper;

    @Autowired
    public ThemeServiceImpl(ThemeRepository themeRepository, ThemeMapper themeMapper) {
        this.themeRepository = themeRepository;
        this.themeMapper = themeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ThemeDTO> findById(Integer id) {
        return this.themeRepository.findById(id).map(this.themeMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ThemeDTO findByName(String name) {
        return this.themeMapper.entityToDto(themeRepository.findByName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ThemeDTO> findAll(Pageable pageable) {
        Page<Theme> themePage = this.themeRepository.findAll(pageable);
        return new PageImpl<>(themePage.getContent()
                .stream()
                .map(this.themeMapper::entityToDto)
                .collect(Collectors.toList()), pageable, themePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ThemeDTO> findAllByNameContaining(String name, Pageable pageable) {
        Page<Theme> themePage = this.themeRepository.findAllByNameContaining(name, pageable);
        return new PageImpl<>(themePage.getContent()
                .stream()
                .map(this.themeMapper::entityToDto)
                .collect(Collectors.toList()), pageable, themePage.getTotalElements());
    }

    @Override
    @Transactional
    public void save(ThemeDTO theme) {
        this.themeRepository.saveAndFlush(this.themeMapper.dtoToEntity(theme));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        this.themeRepository.deleteById(id);
    }
}
