package com.alpha.service.impl;

import com.alpha.mapper.ThemeMapper;
import com.alpha.model.dto.ThemeDTO;
import com.alpha.model.entity.Theme;
import com.alpha.repositories.ThemeRepository;
import com.alpha.service.ThemeService;
import java.util.Date;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return this.themeRepository.findByName(name).map(this.themeMapper::entityToDto)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ThemeDTO> findAll(Pageable pageable) {
        return this.themeRepository.findAllByOrderByUpdateTimeDescCreateTimeDesc(pageable)
            .map(this.themeMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ThemeDTO> findAllByNameContaining(String name, Pageable pageable) {
        return this.themeRepository.findAllByNameContaining(name, pageable)
            .map(this.themeMapper::entityToDto);
    }

    @Override
    @Transactional
    public ThemeDTO create(ThemeDTO themeDTO) {
        Optional<Theme> existedThemeOptional = this.themeRepository.findByName(themeDTO.getName());
        if (existedThemeOptional.isPresent()) {
            throw new EntityExistsException("Theme existed!");
        }
        Theme theme = this.themeMapper.dtoToEntity(themeDTO);
        theme.setCreateTime(new Date());
        theme.setStatus(1);
        this.themeRepository.saveAndFlush(theme);
        return this.themeMapper.entityToDto(theme);
    }

    @Override
    @Transactional
    public ThemeDTO update(Integer id, ThemeDTO themeDTO) {
        Optional<Theme> existedThemeOptional = this.themeRepository.findByName(themeDTO.getName());
        Theme theme;
        if (existedThemeOptional.isPresent()) {
            theme = existedThemeOptional.get();
            if (!theme.getId().equals(id)) {
                throw new EntityExistsException("Theme existed!");
            }
            theme.setName(themeDTO.getName());
        } else {
            theme = this.themeMapper.dtoToEntity(themeDTO);
        }
        theme.setUpdateTime(new Date());
        this.themeRepository.saveAndFlush(theme);
        return this.themeMapper.entityToDto(theme);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        this.themeRepository.deleteById(id);
    }
}
