package com.alpha.controller;

import com.alpha.model.dto.CountryDTO;
import com.alpha.service.CountryService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/country")
public class CountryRestController {

    private final CountryService countryService;

    @Autowired
    public CountryRestController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/list")
    public ResponseEntity<Page<CountryDTO>> songList(Pageable pageable) {
        return ResponseEntity.ok(this.countryService.findAll(pageable));
    }

    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<CountryDTO> songDetail(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.countryService.findById(id));
    }

    @PreAuthorize("hasAuthority(@Authority.COUNTRY_MANAGEMENT)")
    @PostMapping(value = "/create")
    public ResponseEntity<CountryDTO> createCountry(@Valid @RequestBody CountryDTO country) {
        CountryDTO createdCountryDTO = this.countryService.create(country);
        return ResponseEntity.ok(createdCountryDTO);
    }

    @PreAuthorize("hasAuthority(@Authority.COUNTRY_MANAGEMENT)")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<CountryDTO> editCountry(@PathVariable("id") Integer id, @Valid @RequestBody CountryDTO country) {
        CountryDTO updatedCountryDTO = this.countryService.update(id, country);
        return ResponseEntity.ok(updatedCountryDTO);
    }

    @PreAuthorize("hasAuthority(@Authority.COUNTRY_MANAGEMENT)")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable("id") Integer id) {
        try {
            this.countryService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
