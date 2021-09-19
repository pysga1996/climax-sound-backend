package com.alpha.controller;

import com.alpha.model.dto.ThemeDTO;
import com.alpha.service.ThemeService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/theme")
public class ThemeRestController {

    private ThemeService themeService;

    @Autowired
    public void setThemeService(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ThemeDTO>> themeList(Pageable pageable) {
        return ResponseEntity.ok(this.themeService.findAll(pageable));
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ThemeDTO>> searchTheme(@RequestParam String name,
        Pageable pageable) {
        return ResponseEntity.ok(themeService.findAllByNameContaining(name, pageable));
    }

    @PreAuthorize("hasAuthority(@Authority.THEME_MANAGEMENT)")
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTheme(@Valid @RequestBody ThemeDTO themeDTO) {
        this.themeService.create(themeDTO) ;
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority(@Authority.THEME_MANAGEMENT)")
    @PutMapping(value = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ThemeDTO> updateTheme(@Valid @RequestBody ThemeDTO themeDTO,
        @PathVariable("id") Integer id) {
        ThemeDTO updatedThemeDTO = this.themeService.update(id, themeDTO);
        return ResponseEntity.ok(updatedThemeDTO);
    }

    @PreAuthorize("hasAuthority(@Authority.THEME_MANAGEMENT)")
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteActivity(@PathVariable("id") Integer id) {
        this.themeService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
