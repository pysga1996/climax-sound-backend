package com.alpha.service;

import com.alpha.model.dto.ThemeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ThemeService {

    Optional<ThemeDTO> findById(Integer id);

    ThemeDTO findByName(String name);

    Page<ThemeDTO> findAll(Pageable pageable);

    Page<ThemeDTO> findAllByNameContaining(String name, Pageable pageable);

    void save(ThemeDTO mood);

    void deleteById(Integer id);
}
