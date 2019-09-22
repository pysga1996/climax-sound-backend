package com.lambda.controller;

import com.lambda.model.Tag;
import com.lambda.service.TagService;
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
@RequestMapping("/api/tag")
public class TagRestController {
    @Autowired
    TagService tagService;

    @GetMapping(value = "", params = "action=list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Tag>> tagList(Pageable pageable) {
        Page<Tag> tagList = tagService.findAll(pageable);
        boolean isEmpty = tagList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<Page<Tag>>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<Page<Tag>>(tagList, HttpStatus.OK);
    }

    @GetMapping(value = "", params = "action=search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Tag>> tagSearch(@RequestParam String name, Pageable pageable) {
        Page<Tag> filteredTagList = tagService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredTagList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<Page<Tag>>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<Page<Tag>>(filteredTagList, HttpStatus.OK);
    }

    @PostMapping(value = "", params = "action=create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTag(@Valid @RequestBody Tag tag) {
        if (tag == null) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } else {
            tagService.save(tag);
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        }
    }

    @PutMapping(value = "", params = {"action=edit", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editTag(@Valid @RequestBody Tag tag, @RequestParam Long id) {
        Optional<Tag> tagToEdit =tagService.findById(id);
        if (tag == null) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } else if (!tagToEdit.isPresent()) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        } else {
            tagToEdit.get().setName(tag.getName());
            tagService.save(tagToEdit.get());
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "", params = {"action=delete", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTag(@RequestParam Long id) {
        boolean isExist = tagService.findById(id).isPresent();
        if (isExist) {
            tagService.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } else {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }


}
