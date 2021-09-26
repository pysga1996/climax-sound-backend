package com.alpha.controller;

import com.alpha.constant.CommentType;
import com.alpha.model.dto.CommentDTO;
import com.alpha.service.CommentService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author thanhvt
 * @created 9/26/2021 - 2:24 PM
 * @project vengeance
 * @since 1.0
 **/
@RestController
@RequestMapping("/api/comment")
public class CommentRestController {

    private final CommentService commentService;

    @Autowired
    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping(value = "/{type}")
    public ResponseEntity<Page<CommentDTO>> commentList(@PathVariable("type") CommentType type,
        @RequestParam("id") Long entityId,
        Pageable pageable) {
        Page<CommentDTO> commentDTOPage = this.commentService.commentList(type, entityId, pageable);
        return new ResponseEntity<>(commentDTOPage, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentDTO comment) {
        CommentDTO songDTO = this.commentService.create(comment);
        return new ResponseEntity<>(songDTO, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping
    public ResponseEntity<CommentDTO> updateComment(@Valid @RequestBody CommentDTO comment) {
        CommentDTO songDTO = this.commentService.update(comment);
        return new ResponseEntity<>(songDTO, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ResponseEntity<Void> deleteCommentOnSong(@RequestParam("comment-id") Long id,
        @RequestParam("type") CommentType type) {
        this.commentService.deleteById(id, type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
