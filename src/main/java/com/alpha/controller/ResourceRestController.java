package com.alpha.controller;

import com.alpha.service.StorageService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/resource")
public class ResourceRestController {

    private final StorageService storageService;

    @Autowired
    public ResourceRestController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/download/{folder}/{fileName:.+}")
    public ResponseEntity<Resource> downloadAudio(@PathVariable("folder") String folder,
        @PathVariable("fileName") String fileName,
        HttpServletRequest request) {
        // Load file as Resource
        Resource resource = this.storageService.loadFileAsResource(fileName, folder);
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext()
                .getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
}
