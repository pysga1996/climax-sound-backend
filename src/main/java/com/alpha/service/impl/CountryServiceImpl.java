package com.alpha.service.impl;

import com.alpha.mapper.CountryMapper;
import com.alpha.model.dto.CountryDTO;
import com.alpha.model.entity.Country;
import com.alpha.repositories.CountryRepository;
import com.alpha.service.CountryService;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    private final CountryMapper countryMapper;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CountryDTO> findById(Integer id) {
        return this.countryRepository.findById(id)
            .map(this.countryMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CountryDTO findByName(String name) {
        return this.countryMapper.entityToDto(this.countryRepository.findByName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CountryDTO> findAll(Pageable pageable) {
        Page<Country> countryPage = this.countryRepository.findAll(pageable);
        return new PageImpl<>(countryPage.getContent()
            .stream()
            .map(this.countryMapper::entityToDto)
            .collect(Collectors.toList()), pageable, countryPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CountryDTO> findAllByNameContaining(String name, Pageable pageable) {
        Page<Country> countryPage = this.countryRepository.findAllByNameContaining(name, pageable);
        return new PageImpl<>(countryPage.getContent()
            .stream()
            .map(this.countryMapper::entityToDto)
            .collect(Collectors.toList()), pageable, countryPage.getTotalElements());
    }

    @Override
    @Transactional
    public void save(CountryDTO country) {
        countryRepository.saveAndFlush(this.countryMapper.dtoToEntity(country));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        countryRepository.deleteById(id);
    }
}
