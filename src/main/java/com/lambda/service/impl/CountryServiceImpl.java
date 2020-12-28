package com.lambda.service.impl;

import com.lambda.model.entities.Country;
import com.lambda.repositories.CountryRepository;
import com.lambda.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {
    @Autowired
    CountryRepository countryRepository;

    @Override
    public Optional<Country> findById(Integer id) {
        return countryRepository.findById(id);
    }

    @Override
    public Country findByName(String name) {
        return countryRepository.findByName(name);
    }

    @Override
    public Page<Country> findAll(Pageable pageable) {
        return countryRepository.findAll(pageable);
    }

    @Override
    public Page<Country> findAllByNameContaining(String name, Pageable pageable) {
        return countryRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public void save(Country country) {
        countryRepository.saveAndFlush(country);
    }

    @Override
    public void deleteById(Integer id) {
        countryRepository.deleteById(id);
    }
}
