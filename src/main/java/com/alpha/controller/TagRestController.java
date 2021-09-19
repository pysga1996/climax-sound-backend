package com.alpha.controller;

import com.alpha.model.dto.TagDTO;
import com.alpha.service.TagService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tag")
public class TagRestController {

    private final TagService tagService;

    @Autowired
    public TagRestController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<Page<TagDTO>> tagList(Pageable pageable) {
        Page<TagDTO> tagList = this.tagService.findAll(pageable);
        return ResponseEntity.ok(tagList);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Page<TagDTO>> tagSearch(@RequestParam String name, Pageable pageable) {
        Page<TagDTO> filteredTagList = this.tagService.findAllByNameContaining(name, pageable);
        return ResponseEntity.ok(filteredTagList);
    }

    @PreAuthorize("hasAuthority(@Authority.TAG_MANAGEMENT)")
    @PostMapping(value = "/create")
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody TagDTO tagDTO) {
        TagDTO createTag = this.tagService.create(tagDTO);
        return ResponseEntity.ok(createTag);
    }

    @PreAuthorize("hasAuthority(@Authority.TAG_MANAGEMENT)")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<TagDTO> editTag(@Valid @RequestBody TagDTO tagDTO, @PathVariable("id") Long id) {
        TagDTO updatedTag = this.tagService.update(id, tagDTO);
        return ResponseEntity.ok(updatedTag);
    }

    @PreAuthorize("hasAuthority(@Authority.TAG_MANAGEMENT)")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        this.tagService.deleteById(id);
        return ResponseEntity.ok().build();
    }


}
