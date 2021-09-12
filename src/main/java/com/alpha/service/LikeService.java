package com.alpha.service;

import com.alpha.constant.SchedulerConstants.LikeConfig;
import com.alpha.constant.SchedulerConstants.ListeningConfig;

public interface LikeService {

    void like(Long id, LikeConfig likeConfig, boolean isLiked);

    void listen(Long id, ListeningConfig listeningConfig, String username);

    void writeLikesToQueue(String username, Long id,
        boolean isLiked, LikeConfig likeConfig);

    void writeListenToQueue(String username, Long id, ListeningConfig listeningConfig);

    void insertLikesToDb(LikeConfig likeConfig);

    void updateListeningToDb(ListeningConfig listeningConfig);

    void updateListeningCountToDb(ListeningConfig listeningConfig);
}
