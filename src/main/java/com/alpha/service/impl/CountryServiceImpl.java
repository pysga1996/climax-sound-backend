package com.alpha.service.impl;

import com.alpha.mapper.CountryMapper;
import com.alpha.model.dto.CountryDTO;
import com.alpha.model.entity.Country;
import com.alpha.repositories.CountryRepository;
import com.alpha.service.CountryService;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
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
    public CountryDTO findById(Integer id) {
        return this.countryRepository.findById(id)
            .map(this.countryMapper::entityToDto)
            .orElseThrow(() -> new EntityNotFoundException("Country not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public CountryDTO findByName(String name) {
        return this.countryRepository.findByName(name).map(this.countryMapper::entityToDto)
            .orElseThrow(() -> new EntityNotFoundException("Country not found"));
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
    public CountryDTO create(CountryDTO countryDTO) {
        Optional<Country> exitedCountryOptional = this.countryRepository
            .findByName(countryDTO.getName());
        if (exitedCountryOptional.isPresent()) {
            throw new EntityExistsException("Country with the a name existed");
        }
        Country createCountry = this.countryMapper.dtoToEntity(countryDTO);
        createCountry.setCreateTime(new Date());
        createCountry.setStatus(1);
        createCountry = this.countryRepository.saveAndFlush(createCountry);
        return this.countryMapper.entityToDto(createCountry);
    }

    @Override
    @Transactional
    public CountryDTO update(Integer id, CountryDTO countryDTO) {
        boolean existed = this.countryRepository.existsByIdAndName(id, countryDTO.getName());
        if (existed) {
            throw new EntityExistsException("Country with the a name existed");
        }
        Optional<Country> existedCountryOptional = this.countryRepository.findById(id);
        if (existedCountryOptional.isPresent()) {
            existedCountryOptional.get().setName(countryDTO.getName());
            existedCountryOptional.get().setUpdateTime(new Date());
            this.countryRepository.save(existedCountryOptional.get());
            return this.countryMapper.entityToDto(existedCountryOptional.get());
        } else {
            throw new EntityNotFoundException("Country not found");
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        this.countryRepository.deleteById(id);
    }
}
