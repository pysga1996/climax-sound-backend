package com.alpha.service;

import com.alpha.model.dto.CountryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CountryService {

    CountryDTO findById(Integer id);

    CountryDTO findByName(String name);

    Page<CountryDTO> findAll(Pageable pageable);

    Page<CountryDTO> findAllByNameContaining(String name, Pageable pageable);

    CountryDTO create(CountryDTO countryDTO);

    CountryDTO update(Integer id, CountryDTO countryDTO);

    void deleteById(Integer id);
}
