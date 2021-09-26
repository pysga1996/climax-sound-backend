package com.alpha.repositories;

import com.alpha.constant.EntityType;
import com.alpha.model.entity.UserFavorites;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritesRepository {

    boolean getUserLikeFromCache(String username, Long id, EntityType type, AtomicLong noCachedId);

    boolean getUserLikeFromCache(String username, Long id, EntityType type, List<Long> noCachedIds);

    boolean setUserLikeToCache(String username, Long id, EntityType type, boolean isUnlike);

    void updateLikesInBatch(List<String> records);

    void updateListeningInBatch(List<String> records);

    void updateLikesCountInBatch(Map<String, String> listeningCountMap, EntityType type);

    void updateListeningCountInBatch(Map<String, String> listeningCountMap, EntityType type);

    UserFavorites getUserFavorites(String username, Long id, EntityType type);

    List<UserFavorites> getUserLikeMap(String username, List<Long> songIds, EntityType type);

    Long getLikesCount(Long id, EntityType type);

    Long setLikesCountToCache(Long id, Long count, EntityType type);

    Long getListeningCount(Long id, EntityType type);

    Long setListeningCountToCache(Long id, Long count, EntityType type);
}
