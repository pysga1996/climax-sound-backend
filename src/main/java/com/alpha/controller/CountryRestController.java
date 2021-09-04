package com.alpha.controller;

import com.alpha.model.dto.CountryDTO;
import com.alpha.service.CountryService;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/country")
public class CountryRestController {

    private final CountryService countryService;

    @Autowired
    public CountryRestController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PreAuthorize("hasAuthority(@Authority.COUNTRY_MANAGEMENT)")
    @GetMapping("/list")
    public ResponseEntity<Page<CountryDTO>> songList(Pageable pageable) {
        Page<CountryDTO> songList = countryService.findAll(pageable);
        if (songList.getTotalElements() > 0) {
            return new ResponseEntity<>(songList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("hasAuthority(@Authority.COUNTRY_MANAGEMENT)")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<CountryDTO> songDetail(Integer id) {
        Optional<CountryDTO> country = countryService.findById(id);
        return country.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority(@Authority.COUNTRY_MANAGEMENT)")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createCountry(@Valid @RequestBody CountryDTO country) {
        try {
            countryService.save(country);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PreAuthorize("hasAuthority(@Authority.COUNTRY_MANAGEMENT)")
    @PutMapping(value = "/edit")
    public ResponseEntity<Void> editCountry(@Valid @RequestBody CountryDTO country) {
        try {
            countryService.save(country);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority(@Authority.COUNTRY_MANAGEMENT)")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deleteCountry(@Valid @RequestParam Integer id) {
        try {
            countryService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
