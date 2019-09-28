package com.lambda.controller;

import com.lambda.model.entity.Tag;
import com.lambda.service.TagService;
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
@RequestMapping("/api/tag")
public class TagRestController {
    @Autowired
    TagService tagService;

    @GetMapping(params = "action=list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Tag>> tagList(Pageable pageable) {
        Page<Tag> tagList = tagService.findAll(pageable);
        boolean isEmpty = tagList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(tagList, HttpStatus.OK);
    }

    @GetMapping(params = "action=search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Tag>> tagSearch(@RequestParam String name, Pageable pageable) {
        Page<Tag> filteredTagList = tagService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredTagList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredTagList, HttpStatus.OK);
    }

    @PostMapping(params = "action=create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createTag(@Valid @RequestBody Tag tag) {
        Tag checkedTag = tagService.findByName(tag.getName());
        if (checkedTag != null) {
            return new ResponseEntity<>("Tag name has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            tagService.save(tag);
            return new ResponseEntity<>("Tag name created in database!", HttpStatus.CREATED);
        }
    }

    @PutMapping(params = {"action=edit", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editTag(@Valid @RequestBody Tag tag, @RequestParam Long id) {
        Tag checkedTag = tagService.findByName(tag.getName());
        if (checkedTag != null) {
            return new ResponseEntity<>("Tag name has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            tag.setId(id);
            tagService.save(tag);
            return new ResponseEntity<>("Tag name updated in database!", HttpStatus.OK);
        }
    }

    @DeleteMapping(params = {"action=delete", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteTag(@RequestParam Long id) {
        tagService.deleteById(id);
        return new ResponseEntity<>("Tag name removed in database!", HttpStatus.OK);
    }


}
