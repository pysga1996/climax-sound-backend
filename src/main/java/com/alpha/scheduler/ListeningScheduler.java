package com.alpha.scheduler;

import com.alpha.constant.SchedulerConstants.ListeningConfig;
import com.alpha.service.LikeService;
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

    private final LikeService likeService;

    @Autowired
    public ListeningScheduler(LikeService likeService) {
        this.likeService = likeService;
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    public void updateSongListeningToDb() {
        this.likeService.updateListeningToDb(ListeningConfig.SONG);
    }

    @Scheduled(fixedDelay = 300000)
    public void updateAlbumListeningToDb() {
        this.likeService.updateListeningToDb(ListeningConfig.ALBUM);
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    public void updateSongListeningCountToDb() {
        this.likeService.updateListeningCountToDb(ListeningConfig.SONG);
    }

    @Scheduled(fixedDelay = 300000)
    public void updateAlbumListeningCountToDb() {
        this.likeService.updateListeningCountToDb(ListeningConfig.ALBUM);
    }
}
