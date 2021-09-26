package com.alpha.controller;

import com.alpha.constant.EntityType;
import com.alpha.model.dto.LikeDTO;
import com.alpha.service.FavoritesService;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author thanhvt
 * @created 9/26/2021 - 10:00 PM
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@RestController
@RequestMapping("/api/favorites")
public class FavoritesRestController {

    private final FavoritesService favoritesService;

    public FavoritesRestController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/like")
    public ResponseEntity<Void> likeSong(@Validated @RequestBody LikeDTO likeDTO) {
        this.favoritesService.like(likeDTO.getId(), likeDTO.getIsLiked(), likeDTO.getType());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/like-map")
    public ResponseEntity<Map<Long, Boolean>> songLikeMap(
        @RequestParam("type") EntityType type,
        @RequestBody Map<Long, Boolean> songLikeMap) {
        Map<Long, Boolean> patchedSongLikeMap = this.favoritesService
            .getUserLikeMap(songLikeMap, type);
        return new ResponseEntity<>(patchedSongLikeMap, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @PatchMapping("/listen")
    public ResponseEntity<Void> listenToSong(@Validated @RequestBody LikeDTO likeDTO) {
        this.favoritesService.listen(likeDTO.getId(), likeDTO.getType());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
