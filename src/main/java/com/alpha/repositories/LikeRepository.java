package com.alpha.repositories;

import com.alpha.constant.SchedulerConstants.LikeConfig;
import com.alpha.constant.SchedulerConstants.ListeningConfig;
import com.alpha.model.entity.UserFavoriteSong;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository {

    boolean getUserSongLikeFromCache(String username, Long id, AtomicLong noCachedId);

    boolean getUserSongLikeFromCache(String username, Long id, List<Long> noCachedIds);

    boolean setUserSongLikeToCache(String username, Long id, boolean isUnlike);

    void updateLikesInBatch(List<String> records, LikeConfig likeConfig);

    void updateListeningInBatch(List<String> records, ListeningConfig listeningConfig);

    void updateListeningCountInBatch(Map<String, String> listeningCountMap, ListeningConfig listeningConfig);

    UserFavoriteSong getUserSongLike(String username, Long id);

    List<UserFavoriteSong> getUserSongLikeMap(String username, List<Long> songIds);

    Long getSongListeningCount(Long id);

    Long getAlbumListeningCount(Long id);

    Long setSongListeningCountToCache(Long id, Long count);

    Long setAlbumListeningCountToCache(Long id, Long count);
}
