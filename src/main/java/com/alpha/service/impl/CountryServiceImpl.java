package com.alpha.service.impl;

import com.alpha.model.entity.Country;
import com.alpha.repositories.CountryRepository;
import com.alpha.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

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
