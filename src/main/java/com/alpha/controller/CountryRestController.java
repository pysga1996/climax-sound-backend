package com.alpha.controller;

import com.alpha.constant.CrossOriginConfig;
import com.alpha.model.dto.CountryDTO;
import com.alpha.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin(origins = {CrossOriginConfig.Origins.ALPHA_SOUND, CrossOriginConfig.Origins.LOCAL_HOST},
        allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})@RestController
@RequestMapping("/api/country")
public class CountryRestController {

    private final CountryService countryService;

    @Autowired
    public CountryRestController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PreAuthorize("hasAuthority('VIEW_COUNTRY_LIST')")
    @GetMapping("/list")
    public ResponseEntity<Page<CountryDTO>> songList(Pageable pageable) {
        Page<CountryDTO> songList = countryService.findAll(pageable);
        if (songList.getTotalElements() > 0) {
            return new ResponseEntity<>(songList, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('VIEW_COUNTRY_DETAIL')")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<CountryDTO> songDetail(Integer id) {
        Optional<CountryDTO> country = countryService.findById(id);
        return country.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('CREATE_COUNTRY')")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createCountry(@Valid @RequestBody CountryDTO country) {
        try {
            countryService.save(country);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PreAuthorize("hasRole('UPDATE_COUNTRY')")
    @PutMapping(value = "/edit")
    public ResponseEntity<Void> editCountry(@Valid @RequestBody CountryDTO country) {
        try {
            countryService.save(country);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('DELETE_COUNTRY')")
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
