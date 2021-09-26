package com.alpha.scheduler;

import com.alpha.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author thanhvt
 * @created 13/09/2021 - 1:48 SA
 * @project vengeance
 * @since 1.0
 **/
@Component
public class LikeScheduler {

    private final FavoritesService favoritesService;

    @Autowired
    public LikeScheduler(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    public void insertSongLikesToDb() {
        this.favoritesService.insertLikesToDb();
    }
}
