package com.alpha.service;

import com.alpha.constant.EntityType;
import com.alpha.constant.SchedulerConstants.CacheQueue;
import com.alpha.model.dto.MediaDTO;
import com.alpha.model.entity.UserFavorites;
import com.alpha.repositories.FavoritesRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public interface FavoritesService {

    void like(Long id, boolean isLiked, EntityType type);

    void listen(Long id, EntityType type);

    void writeLikesToQueue(String username, Long id,
        boolean isLiked, EntityType type);

    void writeListenToQueue(String username, Long id, EntityType type);

    void insertLikesToDb();

    void updateListeningToDb();

    void updateLikesCountToDb(EntityType type);

    void updateListeningCountToDb(EntityType type);

    UserService getUserService();

    FavoritesRepository getFavoritesRepository();

    default void setLike(MediaDTO mediaDTO, EntityType type) {
        String currentUsername = this.getUserService().getCurrentUsername();
        AtomicLong atomicLong = new AtomicLong(0);
        boolean isLiked = this.getFavoritesRepository()
            .getUserLikeFromCache(currentUsername, mediaDTO.getId(), type, atomicLong);
        if (atomicLong.get() != 0L) {
            UserFavorites userFavorites = this.getFavoritesRepository()
                .getUserFavorites(currentUsername, mediaDTO.getId(), type);
            this.getFavoritesRepository()
                .setUserLikeToCache(currentUsername, mediaDTO.getId(), type,
                    userFavorites.isLiked());
        }
        mediaDTO.setLiked(isLiked);
    }

    default void setLikes(Page<? extends MediaDTO> songDTOPage, EntityType type) {
        Map<Long, MediaDTO> songDTOMap = songDTOPage
            .stream()
            .collect(Collectors.toMap(MediaDTO::getId, e -> e));
        String currentUsername = this.getUserService().getCurrentUsername();
        List<Long> noCachedEntityIds = new ArrayList<>();
        for (Entry<Long, MediaDTO> entry : songDTOMap.entrySet()) {
            boolean isLiked = this.getFavoritesRepository()
                .getUserLikeFromCache(currentUsername, entry.getKey(), entry.getValue().getType(),
                    noCachedEntityIds);
            entry.getValue().setLiked(isLiked);
        }
        if (noCachedEntityIds.size() > 0) {
            boolean isLiked;
            Long entityId;
            List<UserFavorites> userFavoriteEntityList = this.getFavoritesRepository()
                .getUserLikeMap(currentUsername, noCachedEntityIds, type);
            for (UserFavorites userFavoriteEntity : userFavoriteEntityList) {
                entityId = userFavoriteEntity.getUserFavoritesId().getEntityId();
                isLiked = this.getFavoritesRepository()
                    .setUserLikeToCache(currentUsername, entityId, type,
                        userFavoriteEntity.isLiked());
                songDTOMap.get(entityId).setLiked(isLiked);
            }
        }
    }

    default Map<Long, Boolean> getUserLikeMap(Map<Long, Boolean> entityIdMap, EntityType type) {
        String currentUsername = this.getUserService().getCurrentUsername();
        List<Long> entityIds = new ArrayList<>(entityIdMap.keySet());
        List<Long> noCachedSongIds = new ArrayList<>();
        for (Long entityId : entityIds) {
            boolean isLiked = this.getFavoritesRepository()
                .getUserLikeFromCache(currentUsername, entityId, type, noCachedSongIds);
            entityIdMap.replace(entityId, isLiked);
        }
        List<UserFavorites> userFavoriteSongList = this.getFavoritesRepository()
            .getUserLikeMap(currentUsername, noCachedSongIds, type);
        userFavoriteSongList
            .forEach(e -> entityIdMap.replace(e.getUserFavoritesId().getEntityId(), e.isLiked()));
        return entityIdMap;
    }

    default String getLikesCacheQueue(EntityType type) {
        switch (type) {
            case SONG:
                return CacheQueue.SONG_LIKES_CACHE_QUEUE;
            case ALBUM:
                return CacheQueue.ALBUM_LIKES_CACHE_QUEUE;
            default:
                return null;
        }
    }

    default String getListeningCacheQueue(EntityType type) {
        switch (type) {
            case SONG:
                return CacheQueue.SONG_LISTENING_CACHE_QUEUE;
            case ALBUM:
                return CacheQueue.ALBUM_LISTENING_CACHE_QUEUE;
            default:
                return null;
        }
    }
}
