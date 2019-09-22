package com.lambda.controller;

import com.lambda.model.Mood;
import com.lambda.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

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
            return new ResponseEntity<Page<Mood>>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<Page<Mood>>(moodList, HttpStatus.OK);
    }

    @GetMapping(params = "action=search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Mood>> moodSearch(@RequestParam String name, Pageable pageable) {
        Page<Mood> filteredMoodList = moodService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredMoodList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<Page<Mood>>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<Page<Mood>>(filteredMoodList, HttpStatus.OK);
    }

    @PostMapping(params = "action=create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createMood(@Valid @RequestBody Mood mood) {
        if (mood == null) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } else {
            moodService.save(mood);
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        }
    }

    @PutMapping(params = {"action=edit", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editMood(@Valid @RequestBody Mood mood, @RequestParam Integer id) {
        Optional<Mood> moodToEdit = moodService.findById(id);
        if (mood == null) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } else if (!moodToEdit.isPresent()) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        else {
            moodToEdit.get().setName(mood.getName());
            moodService.save(moodToEdit.get());
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
    }

    @DeleteMapping(params = {"action=delete", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteMood(@RequestParam Integer id) {
        boolean isExist = moodService.findById(id).isPresent();
        if (isExist) {
            moodService.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } else {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }
}
