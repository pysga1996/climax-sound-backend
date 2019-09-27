package com.lambda.controller;

import com.lambda.model.entity.Mood;
import com.lambda.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/mood")
public class MoodRestController {
    @Autowired
    MoodService moodService;

    @GetMapping(params = "action=list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Mood>> moodList(Pageable pageable) {
        Page<Mood> moodList = moodService.findAll(pageable);
        boolean isEmpty = moodList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(moodList, HttpStatus.OK);
    }

    @GetMapping(params = "action=search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Mood>> moodSearch(@RequestParam String name, Pageable pageable) {
        Page<Mood> filteredMoodList = moodService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredMoodList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredMoodList, HttpStatus.OK);
    }

    @PostMapping(params = "action=create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createMood(@Valid @RequestBody Mood mood) {
        Mood checkedMood = moodService.findByName(mood.getName());
        if (checkedMood != null) {
            return new ResponseEntity<>("Mood name has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            moodService.save(mood);
            return new ResponseEntity<>("Mood name created successfully!", HttpStatus.CREATED);
        }
    }

    @PutMapping(params = {"action=edit", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editMood(@Valid @RequestBody Mood mood, @RequestParam Integer id) {
        Mood checkedMood = moodService.findByName(mood.getName());
        if (checkedMood != null) {
            return new ResponseEntity<>("Mood name has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        else {
            mood.setId(id);
            moodService.save(mood);
            return new ResponseEntity<>("Mood name updated successfully!", HttpStatus.OK);
        }
    }

    @DeleteMapping(params = {"action=delete", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteMood(@RequestParam Integer id) {
        moodService.deleteById(id);
        return new ResponseEntity<>("Mood removed successfully", HttpStatus.OK);
    }
}
