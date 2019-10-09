package com.lambda.service;

import com.lambda.model.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CountryService {
    Optional<Country> findById(Integer id);
    Country findByName(String name);
    Page<Country> findAll(Pageable pageable);
    Page<Country> findAllByNameContaining(String name, Pageable pageable);
    void save(Country country);
    void deleteById(Integer id);
}
