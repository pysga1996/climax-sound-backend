package com.alpha.scheduler;

import com.alpha.constant.SchedulerConstants.LikeConfig;
import com.alpha.service.LikeService;
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

    private final LikeService likeService;

    @Autowired
    public LikeScheduler(LikeService likeService) {
        this.likeService = likeService;
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    public void insertSongLikesToDb() {
        this.likeService.insertLikesToDb(LikeConfig.SONG);
    }

    @Scheduled(fixedDelay = 300000)
    public void insertAlbumLikesToDb() {
        this.likeService.insertLikesToDb(LikeConfig.ALBUM);
    }

    @Scheduled(fixedDelay = 300000)
    public void insertArtistLikesToDb() {
        this.likeService.insertLikesToDb(LikeConfig.ARTIST);
    }
}
