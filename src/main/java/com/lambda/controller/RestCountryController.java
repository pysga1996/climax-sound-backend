package com.lambda.controller;

import com.lambda.model.entity.Country;
import com.lambda.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/country")
public class RestCountryController {
    @Autowired
    CountryService countryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<Page<Country>> songList(Pageable pageable) {
        Page<Country> songList = countryService.findAll(pageable);
        if (songList.getTotalElements() > 0) {
            return new ResponseEntity<>(songList, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<Country> songDetail(Integer id) {
        Optional<Country> country = countryService.findById(id);
        return country.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createCountry(@Valid @RequestBody Country country) {
        countryService.save(country);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/edit")
    public ResponseEntity<Void> editCountry(@Valid @RequestBody Country country) {
        try {
            countryService.save(country);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
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
