package com.alpha.service;

import com.alpha.model.dto.CountryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CountryService {
    Optional<CountryDTO> findById(Integer id);

    CountryDTO findByName(String name);

    Page<CountryDTO> findAll(Pageable pageable);

    Page<CountryDTO> findAllByNameContaining(String name, Pageable pageable);

    void save(CountryDTO country);

    void deleteById(Integer id);
}
