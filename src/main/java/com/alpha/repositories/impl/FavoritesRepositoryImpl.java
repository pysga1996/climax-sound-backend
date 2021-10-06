package com.alpha.repositories.impl;

import com.alpha.constant.EntityType;
import com.alpha.constant.SchedulerConstants;
import com.alpha.model.entity.UserFavorites;
import com.alpha.repositories.BaseRepository;
import com.alpha.repositories.FavoritesRepository;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 23/07/2021 - 11:57 CH
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Getter
@Repository
public class FavoritesRepositoryImpl extends BaseRepository implements FavoritesRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Cacheable(cacheNames = "likes", key = "#type.name() + '_' + #username + '_' + #id")
    public boolean getUserLikeFromCache(String username, Long id, EntityType type, AtomicLong noCachedId) {
        noCachedId.set(id);
        return false;
    }

    @Override
    @Cacheable(cacheNames = "likes", key = "#type.name() + '_' + #username + '_' + #id")
    public boolean getUserLikeFromCache(String username, Long id, EntityType type, List<Long> noCachedIds) {
        noCachedIds.add(id);
        return false;
    }

    @Override
    @CachePut(cacheNames = "likes", key = "#type.name() + '_' + #username + '_' + #id")
    public boolean setUserLikeToCache(String username, Long id, EntityType type,
        boolean isLiked) {
        return isLiked;
    }

    @Override
    public void updateLikesInBatch(List<String> records) {
        int upsertCount = this
            .executeInsertUpdateDeleteInBatch(SchedulerConstants.UPSERT_LIKES_SQL, records, (statement, record, i) -> {
                String[] lineArr = record.split("_");
                log.debug("Line {}: {}", i, record);
                statement.setString(1, lineArr[1]);
                statement.setLong(2, Long.parseLong(lineArr[2]));
                statement.setBoolean(3, Boolean.parseBoolean(lineArr[3]));
                statement.setString(4, lineArr[0]);
                statement.setBoolean(5, Boolean.parseBoolean(lineArr[3]));
                statement.setString(6, lineArr[1]);
                statement.setLong(7, Long.parseLong(lineArr[2]));
                statement.setString(8, lineArr[0]);
            });
        log.info("Upsert likes count: {}", upsertCount);
    }

    @Override
    public void updateListeningInBatch(List<String> records) {
        int upsertCount = this.executeInsertUpdateDeleteInBatch(SchedulerConstants.INSERT_LISTENING_SQL, records,
            (statement, record, i) -> {
                String[] lineArr = record.split("_");
                log.debug("Line {}: {}", i, record);
                statement.setString(1, lineArr[1]);
                statement.setLong(2, Long.parseLong(lineArr[2]));
                statement.setString(3, lineArr[0]);
            });
        log.info("Upsert listening count: {}", upsertCount);
    }

    @Override
    public void updateLikesCountInBatch(Map<String, String> listeningCountMap,
        EntityType type) {
        String sql = null;
        switch (type) {
            case SONG:
                sql = SchedulerConstants.SONG_UPDATE_LIKES_COUNT;
                break;
            case ALBUM:
                sql = SchedulerConstants.ALBUM_UPDATE_LIKES_COUNT;
                break;
        }
        if (sql == null) return;
        int updateCount = this
            .executeInsertUpdateDeleteInBatch(sql,
                listeningCountMap.entrySet(), (statement, record, i) -> {
                    log.debug("Likes update line {}: id {} - count {}", i, record.getKey(),
                        record.getValue());
                    statement.setLong(1, Long.parseLong(record.getValue()));
                    statement.setLong(2, Long.parseLong(record.getKey()));
                });
        log.info("Update likes count of {} count: {}", type.name(), updateCount);
    }

    @Override
    public void updateListeningCountInBatch(Map<String, String> listeningCountMap,
        EntityType type) {
        String sql = null;
        switch (type) {
            case SONG:
                sql = SchedulerConstants.SONG_UPDATE_LISTENING_COUNT;
                break;
            case ALBUM:
                sql = SchedulerConstants.ALBUM_UPDATE_LISTENING_COUNT;
                break;
        }
        if (sql == null) return;
        int updateCount = this
            .executeInsertUpdateDeleteInBatch(sql,
                listeningCountMap.entrySet(), (statement, record, i) -> {
                    log.debug("Listening update line {}: id {} - count {}", i, record.getKey(),
                        record.getValue());
                    statement.setLong(1, Long.parseLong(record.getValue()));
                    statement.setLong(2, Long.parseLong(record.getKey()));
                });
        log.info("Update listening count of {} count: {}", type.name(), updateCount);
    }

    @Override
    public UserFavorites getUserFavorites(String username, Long id, EntityType type) {
        TypedQuery<UserFavorites> query = this.entityManager.createQuery(
            "SELECT us FROM UserFavorites us WHERE us.userFavoritesId.username = :username AND us.userFavoritesId.entityId = :entityId AND us.userFavoritesId.type = :type AND us.liked = true",
            UserFavorites.class);
        query.setParameter("username", username);
        query.setParameter("entityId", id);
        query.setParameter("type", type);
        List<UserFavorites> userFavoritesList = query.getResultList();
        if (userFavoritesList.size() > 0) {
            return userFavoritesList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<UserFavorites> getUserLikeMap(String username, List<Long> songIds,
        EntityType type) {
        TypedQuery<UserFavorites> query = this.entityManager.createQuery(
            "SELECT us FROM UserFavorites us WHERE us.userFavoritesId.username = :username AND us.userFavoritesId.type = :type AND us.userFavoritesId.entityId  IN (:entityIds) AND us.liked = true",
            UserFavorites.class);
        query.setParameter("username", username);
        query.setParameter("entityIds", songIds);
        query.setParameter("type", type);
        return query.getResultList();
    }

    @Override
    @Cacheable(cacheNames = SchedulerConstants.LIKES_CACHE, key = "#type.name() + '_' + #id", cacheManager = "pysgaRedisCacheManager")
    public Long getLikesCount(Long id, EntityType type) {
        String sql = null;
        switch (type) {
            case SONG:
                sql = "SELECT like_count FROM song WHERE id = :id";
                break;
            case ALBUM:
                sql = "SELECT like_count FROM album WHERE id = :id";
                break;
            case ARTIST:
                sql = "SELECT like_count FROM artist WHERE id = :id";
                break;
        }
        if (sql == null) {
            throw new RuntimeException("Invalid cache type!");
        }
        Query query = this.entityManager.createNativeQuery(
            sql);
        query.setParameter("id", id);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @Override
    @CachePut(cacheNames = SchedulerConstants.LIKES_CACHE, key = "#type.name() + '_' + #id", cacheManager = "pysgaRedisCacheManager")
    public Long setLikesCountToCache(Long id, Long count, EntityType type) {
        return count;
    }

    @Override
    @Cacheable(cacheNames = SchedulerConstants.LISTENING_CACHE, key = "#type.name() + '_' + #id", cacheManager = "pysgaRedisCacheManager")
    public Long getListeningCount(Long id, EntityType type) {
        String sql = null;
        switch (type) {
            case SONG:
                sql = "SELECT listening_frequency FROM song WHERE id = :id";
                break;
            case ALBUM:
                sql = "SELECT listening_frequency FROM album WHERE id = :id";
                break;
        }
        if (sql == null) {
            throw new RuntimeException("Invalid cache type!");
        }
        Query query = this.entityManager.createNativeQuery(
            sql);
        query.setParameter("id", id);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @Override
    @CachePut(cacheNames = SchedulerConstants.LISTENING_CACHE, key = "#type.name() + '_' + #id", cacheManager = "pysgaRedisCacheManager")
    public Long setListeningCountToCache(Long id, Long count, EntityType type) {
        return count;
    }
}
