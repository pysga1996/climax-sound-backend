package com.alpha.controller;

import com.alpha.model.dto.ThemeDTO;
import com.alpha.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})
@RestController
@RequestMapping("/api/activity")
public class ThemeRestController {
    private ThemeService themeService;

    @Autowired
    public void setThemeService(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping(params = "action=list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ThemeDTO>> activityList(Pageable pageable) {
        Page<ThemeDTO> activityList = themeService.findAll(pageable);
        boolean isEmpty = activityList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(activityList, HttpStatus.OK);
    }

    @GetMapping(params = "action=search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ThemeDTO>> activitySearch(@RequestParam String name, Pageable pageable) {
        Page<ThemeDTO> filteredActivityList = themeService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredActivityList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredActivityList, HttpStatus.OK);
    }

    @PostMapping(params = "action=create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createActivity(@Valid @RequestBody ThemeDTO mood) {
        if (mood == null) {
            return new ResponseEntity<>("Theme title has already existed in database!", HttpStatus.BAD_REQUEST);
        } else {
            themeService.save(mood);
            return new ResponseEntity<>("Theme created successfully!", HttpStatus.CREATED);
        }
    }

    @PutMapping(params = {"action=edit", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editActivity(@Valid @RequestBody ThemeDTO theme, @RequestParam Integer id) {
        ThemeDTO checkedTheme = themeService.findByName(theme.getName());
        if (checkedTheme != null) {
            return new ResponseEntity<>("Theme title has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            theme.setId(id);
            themeService.save(theme);
            return new ResponseEntity<>("Theme updated successfully!", HttpStatus.OK);
        }
    }

    @DeleteMapping(params = {"action=delete", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteActivity(@RequestParam Integer id) {
        themeService.deleteById(id);
        return new ResponseEntity<>("Theme removed successfully!", HttpStatus.OK);
    }
}
