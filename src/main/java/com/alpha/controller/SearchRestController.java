package com.alpha.controller;

import com.alpha.elastic.model.MediaEs;
import com.alpha.model.dto.UpdateSyncOption;
import com.alpha.service.SearchService;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author thanhvt
 * @created 10/8/2021 - 6:58 PM
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@RestController
@RequestMapping("/api/es")
public class SearchRestController {

    private final SearchService searchService;

    @Autowired
    public SearchRestController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Page<? extends MediaEs>>> searchMedia(
        @RequestParam("q") String q) {
        Map<String, Page<? extends MediaEs>> searchResultMap = this.searchService.search(q);
        return ResponseEntity.ok(searchResultMap);
    }

    @PreAuthorize("hasAuthority(@Authority.ELASTIC_SEARCH_MANAGEMENT)")
    @PutMapping("/reload-mapping")
    public ResponseEntity<Void> reloadMapping(@RequestParam(value = "index-name") String indexName) {
        this.searchService.reloadMapping(indexName);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority(@Authority.ELASTIC_SEARCH_MANAGEMENT)")
    @PatchMapping("/mark-for-sync")
    public ResponseEntity<Void> markForSync(@RequestParam(value = "index-name") String indexName,
        @RequestBody UpdateSyncOption option) {
        this.searchService.markForSync(indexName, option);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority(@Authority.ELASTIC_SEARCH_MANAGEMENT)")
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearIndex(@RequestParam(value = "index-name") String indexName, @RequestParam(value = "id") Long id) {
        this.searchService.clearIndex(indexName, id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority(@Authority.ELASTIC_SEARCH_MANAGEMENT)")
    @DeleteMapping("/reset")
    public ResponseEntity<Void> resetIndex(@RequestParam(value = "index-name") String indexName) {
        this.searchService.resetIndex(indexName);
        return ResponseEntity.ok().build();
    }
}
