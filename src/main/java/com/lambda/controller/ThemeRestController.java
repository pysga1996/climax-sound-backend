package com.lambda.controller;

import com.lambda.model.entity.Theme;
import com.lambda.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "https://climax-sound.netlify.com, http://localhost:4200*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/activity")
public class ThemeRestController {
    @Autowired
    ThemeService themeService;

    @GetMapping(params = "action=list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Theme>> activityList(Pageable pageable) {
        Page<Theme> activityList = themeService.findAll(pageable);
        boolean isEmpty = activityList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(activityList, HttpStatus.OK);
    }

    @GetMapping(params = "action=search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Theme>> activitySearch(@RequestParam String name, Pageable pageable) {
        Page<Theme> filteredActivityList = themeService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredActivityList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredActivityList, HttpStatus.OK);
    }

    @PostMapping(params = "action=create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createActivity(@Valid @RequestBody Theme mood) {
        if (mood == null) {
            return new ResponseEntity<>("Theme title has already existed in database!", HttpStatus.BAD_REQUEST);
        } else {
            themeService.save(mood);
            return new ResponseEntity<>("Theme created successfully!", HttpStatus.CREATED);
        }
    }

    @PutMapping(params = {"action=edit", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editActivity(@Valid @RequestBody Theme theme, @RequestParam Integer id) {
        Theme checkedTheme = themeService.findByName(theme.getName());
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
