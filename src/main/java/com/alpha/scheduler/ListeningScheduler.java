package com.alpha.scheduler;

import com.alpha.constant.EntityType;
import com.alpha.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author thanhvt
 * @created 13/09/2021 - 1:50 SA
 * @project vengeance
 * @since 1.0
 **/
@Component
public class ListeningScheduler {

    private final FavoritesService favoritesService;

    @Autowired
    public ListeningScheduler(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    public void updateSongListeningToDb() {
        this.favoritesService.updateListeningToDb();
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    public void updateSongLikesCountToDb() {
        this.favoritesService.updateLikesCountToDb(EntityType.SONG);
    }

    @Scheduled(fixedDelay = 300000)
    public void updateAlbumLikesCountToDb() {
        this.favoritesService.updateLikesCountToDb(EntityType.ALBUM);
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    public void updateSongListeningCountToDb() {
        this.favoritesService.updateListeningCountToDb(EntityType.SONG);
    }

    @Scheduled(fixedDelay = 300000)
    public void updateAlbumListeningCountToDb() {
        this.favoritesService.updateListeningCountToDb(EntityType.ALBUM);
    }
}
