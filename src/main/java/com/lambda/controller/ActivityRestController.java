package com.lambda.controller;

import com.lambda.model.Activity;
import com.lambda.service.ActivityService;
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
@RequestMapping("/api/activity")
public class ActivityRestController {
    @Autowired
    ActivityService activityService;

    @GetMapping(params = "action=list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Activity>> activityList(Pageable pageable) {
        Page<Activity> activityList = activityService.findAll(pageable);
        boolean isEmpty = activityList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<Page<Activity>>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<Page<Activity>>(activityList, HttpStatus.OK);
    }

    @GetMapping(params = "action=search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Activity>> activitySearch(@RequestParam String name, Pageable pageable) {
        Page<Activity> filteredActivityList = activityService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredActivityList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<Page<Activity>>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<Page<Activity>>(filteredActivityList, HttpStatus.OK);
    }

    @PostMapping(params = "action=create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createActivity(@Valid @RequestBody Activity mood) {
        if (mood == null) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } else {
            activityService.save(mood);
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        }
    }

    @PutMapping(params = {"action=edit", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editActivity(@Valid @RequestBody Activity mood, @RequestParam Integer id) {
        Optional<Activity> activityToEdit = activityService.findById(id);
        if (mood == null) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } else if (!activityToEdit.isPresent()) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        else {
            activityToEdit.get().setName(mood.getName());
            activityService.save(activityToEdit.get());
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
    }

    @DeleteMapping(params = {"action=delete", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteActivity(@RequestParam Integer id) {
        boolean isExist = activityService.findById(id).isPresent();
        if (isExist) {
            activityService.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } else {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }
}
