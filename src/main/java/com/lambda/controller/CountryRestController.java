package com.lambda.controller;

import com.lambda.model.entity.Country;
import com.lambda.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/mood")
public class CountryRestController {
    @Autowired
    CountryService countryService;

    @GetMapping(params = "action=list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Country>> moodList(Pageable pageable) {
        Page<Country> moodList = countryService.findAll(pageable);
        boolean isEmpty = moodList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(moodList, HttpStatus.OK);
    }

    @GetMapping(params = "action=search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Country>> moodSearch(@RequestParam String name, Pageable pageable) {
        Page<Country> filteredMoodList = countryService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredMoodList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredMoodList, HttpStatus.OK);
    }

    @PostMapping(params = "action=create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createMood(@Valid @RequestBody Country country) {
        Country checkedCountry = countryService.findByName(country.getName());
        if (checkedCountry != null) {
            return new ResponseEntity<>("Country name has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            countryService.save(country);
            return new ResponseEntity<>("Country name created successfully!", HttpStatus.CREATED);
        }
    }

    @PutMapping(params = {"action=edit", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editMood(@Valid @RequestBody Country country, @RequestParam Integer id) {
        Country checkedCountry = countryService.findByName(country.getName());
        if (checkedCountry != null) {
            return new ResponseEntity<>("Country name has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        else {
            country.setId(id);
            countryService.save(country);
            return new ResponseEntity<>("Country name updated successfully!", HttpStatus.OK);
        }
    }

    @DeleteMapping(params = {"action=delete", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteMood(@RequestParam Integer id) {
        countryService.deleteById(id);
        return new ResponseEntity<>("Country removed successfully", HttpStatus.OK);
    }
}
