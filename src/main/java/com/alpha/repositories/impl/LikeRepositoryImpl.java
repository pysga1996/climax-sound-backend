package com.alpha.repositories.impl;

import com.alpha.model.entity.UserFavoriteSong;
import com.alpha.repositories.BaseRepository;
import com.alpha.repositories.LikeRepository;
import com.alpha.service.LikeService.LikeConfig;
import com.alpha.service.LikeService.ListeningConfig;
import com.alpha.util.helper.SqlUtilService;
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
import org.springframework.beans.factory.annotation.Autowired;
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
public class LikeRepositoryImpl extends BaseRepository implements LikeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final SqlUtilService sqlUtilService;

    @Autowired
    public LikeRepositoryImpl(SqlUtilService sqlUtilService) {
        this.sqlUtilService = sqlUtilService;
    }

    @Override
    @Cacheable(cacheNames = "songLikes", key = "#username + '_' + #id")
    public boolean getUserSongLikeFromCache(String username, Long id, AtomicLong noCachedId) {
        noCachedId.set(id);
        return false;
    }

    @Override
    @Cacheable(cacheNames = "songLikes", key = "#username + '_' + #id")
    public boolean getUserSongLikeFromCache(String username, Long id, List<Long> noCachedIds) {
        noCachedIds.add(id);
        return false;
    }

    @Override
    @CachePut(cacheNames = "songLikes", key = "#username + '_' + #id")
    public boolean setUserSongLikeToCache(String username, Long id, boolean isLiked) {
        return isLiked;
    }

    public void updateLikesInBatch(List<String> records, LikeConfig likeConfig) {
        String sql = this.sqlUtilService.readSqlResourceFile(likeConfig.getSqlFile());
        int upsertCount = this.batchInsertUpdateDelete(sql, records, (statement, record, i) -> {
            String[] lineArr = record.split("_");
            log.debug("Line {}: {}", i, record);
            statement.setString(1, lineArr[0]);
            statement.setLong(2, Long.parseLong(lineArr[1]));
            statement.setBoolean(3, Boolean.parseBoolean(lineArr[2]));
            statement.setBoolean(4, Boolean.parseBoolean(lineArr[2]));
            statement.setString(5, lineArr[0]);
            statement.setLong(6, Long.parseLong(lineArr[1]));
        });
        log.info("Upsert likes of {} count: {}", likeConfig.getTable(), upsertCount);
    }

    @Override
    public void updateListeningInBatch(List<String> records, ListeningConfig listeningConfig) {
        String sql = this.sqlUtilService.readSqlResourceFile(listeningConfig.getListeningSqlFile());
        int upsertCount = this.batchInsertUpdateDelete(sql, records, (statement, record, i) -> {
            String[] lineArr = record.split("_");
            log.debug("Line {}: {}", i, record);
            statement.setString(1, lineArr[0]);
            statement.setLong(2, Long.parseLong(lineArr[1]));
        });
        log.info("Upsert listening of {} count: {}", listeningConfig.getTable(), upsertCount);
    }

    @Override
    public void updateListeningCountInBatch(Map<String, String> listeningCountMap,
        ListeningConfig listeningConfig) {
        String sql = this.sqlUtilService.readSqlResourceFile(listeningConfig.getListeningCountSqlFile());
        int updateCount = this.batchInsertUpdateDelete(sql, listeningCountMap.entrySet(), (statement, record, i) -> {
            log.debug("Listening update line {}: id {} - count {}", i, record.getKey(), record.getValue());
            statement.setLong(1, Long.parseLong(record.getValue()));
            statement.setLong(2, Long.parseLong(record.getKey()));
        });
        log.info("Update listening count of {} count: {}", listeningConfig.getTable(), updateCount);
    }

    @Override
    public UserFavoriteSong getUserSongLike(String username, Long id) {
        TypedQuery<UserFavoriteSong> query = this.entityManager.createQuery(
            "SELECT us FROM UserFavoriteSong us WHERE us.userFavoriteSongId.username = :username AND us.userFavoriteSongId.songId = :songId AND us.liked = true",
            UserFavoriteSong.class);
        query.setParameter("username", username);
        query.setParameter("songId", id);
        List<UserFavoriteSong> userFavoriteSongList = query.getResultList();
        if (userFavoriteSongList.size() > 0) {
            return userFavoriteSongList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<UserFavoriteSong> getUserSongLikeMap(String username, List<Long> songIds) {
        TypedQuery<UserFavoriteSong> query = this.entityManager.createQuery(
            "SELECT us FROM UserFavoriteSong us WHERE us.userFavoriteSongId.username = :username AND us.userFavoriteSongId.songId IN (:songIds) AND us.liked = true",
            UserFavoriteSong.class);
        query.setParameter("username", username);
        query.setParameter("songIds", songIds);
        return query.getResultList();
    }

    @Override
    @Cacheable(cacheNames = "songListeningCount", key = "#id", cacheManager = "pysgaRedisCacheManager")
    public Long getSongListeningCount(Long id) {
        Query query = this.entityManager.createNativeQuery(
            "SELECT listening_frequency FROM song WHERE id = :id");
        query.setParameter("id", id);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @Override
    @Cacheable(cacheNames = "songListeningCount", key = "#id", cacheManager = "pysgaRedisCacheManager")
    public Long getAlbumListeningCount(Long id) {
        Query query = this.entityManager.createNativeQuery(
            "SELECT listening_frequency FROM album WHERE id = :id");
        query.setParameter("id", id);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @Override
    @CachePut(cacheNames = "songListeningCount", key = "#id", cacheManager = "pysgaRedisCacheManager")
    public Long setSongListeningCountToCache(Long id, Long count) {
        return count;
    }

    @Override
    @CachePut(cacheNames = "songListeningCount", key = "#id", cacheManager = "pysgaRedisCacheManager")
    public Long setAlbumListeningCountToCache(Long id, Long count) {
        return count;
    }
}
