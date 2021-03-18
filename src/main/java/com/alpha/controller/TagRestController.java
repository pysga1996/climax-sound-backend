package com.alpha.controller;

import com.alpha.model.dto.TagDTO;
import com.alpha.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})
@RestController
@RequestMapping("/api/tag")
public class TagRestController {

    private final TagService tagService;

    @Autowired
    public TagRestController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(params = "action=list")
    public ResponseEntity<Page<TagDTO>> tagList(Pageable pageable) {
        Page<TagDTO> tagList = this.tagService.findAll(pageable);
        boolean isEmpty = tagList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(tagList, HttpStatus.OK);
    }

    @GetMapping(params = "action=search")
    public ResponseEntity<Page<TagDTO>> tagSearch(@RequestParam String name, Pageable pageable) {
        Page<TagDTO> filteredTagList = this.tagService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredTagList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredTagList, HttpStatus.OK);
    }

    @PostMapping(params = "action=create")
    public ResponseEntity<String> createTag(@Valid @RequestBody TagDTO tag) {
        TagDTO checkedTag = this.tagService.findByName(tag.getName());
        if (checkedTag != null) {
            return new ResponseEntity<>("Tag title has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            this.tagService.save(tag);
            return new ResponseEntity<>("Tag title created in database!", HttpStatus.CREATED);
        }
    }

    @PutMapping(params = {"action=edit", "id"})
    public ResponseEntity<String> editTag(@Valid @RequestBody TagDTO tag, @RequestParam Long id) {
        TagDTO checkedTag = this.tagService.findByName(tag.getName());
        if (checkedTag != null) {
            return new ResponseEntity<>("Tag title has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            tag.setId(id);
            this.tagService.save(tag);
            return new ResponseEntity<>("Tag title updated in database!", HttpStatus.OK);
        }
    }

    @DeleteMapping(params = {"action=delete", "id"})
    public ResponseEntity<String> deleteTag(@RequestParam Long id) {
        this.tagService.deleteById(id);
        return new ResponseEntity<>("Tag title removed in database!", HttpStatus.OK);
    }


}
