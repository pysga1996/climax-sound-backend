package com.alpha.repositories.impl;

import com.alpha.constant.ModelStatus;
import com.alpha.repositories.TagRepositoryCustom;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;

/**
 * @author thanhvt
 * @created 10/9/2021 - 11:13 AM
 * @project vengeance
 * @since 1.0
 **/
public class TagRepositoryImpl implements TagRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Set<String>> retrieveTagsOfAlbumIds(List<Long> albumIds) {
        String sql = "SELECT at.album_id, t.name FROM album_tag at INNER JOIN tag t ON t.id = at.tag_id WHERE at.album_id IN :albumIds AND t.status = :status";
        Query query = this.entityManager.createNativeQuery(sql, Tuple.class);
        query.setParameter("albumIds", albumIds);
        query.setParameter("status", ModelStatus.ACTIVE.getValue());
        Map<Long, Set<String>> tagMap = new HashMap<>();
        Stream<Tuple> rsStream =  query.getResultStream();
        rsStream.forEach(e -> {
            long albumId = e.get("album_id", BigInteger.class).longValue();
            String tagName = e.get("name", String.class);
            if (tagMap.containsKey(albumId)) {
                Set<String> tagSet = tagMap.get(albumId);
                tagSet.add(tagName);
            } else {
                Set<String> tagSet = new HashSet<>();
                tagSet.add(tagName);
                tagMap.put(albumId, tagSet);
            }
        });
        return tagMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Set<String>> retrieveTagsOfSongIds(List<Long> songIds) {
        String sql = "SELECT st.song_id, t.name FROM song_tag st INNER JOIN tag t ON t.id = st.tag_id WHERE st.song_id IN :songIds AND t.status = :status";
        Query query = this.entityManager.createNativeQuery(sql, Tuple.class);
        query.setParameter("songIds", songIds);
        query.setParameter("status", ModelStatus.ACTIVE.getValue());
        Map<Long, Set<String>> tagMap = new HashMap<>();
        Stream<Tuple> rsStream =  query.getResultStream();
        rsStream.forEach(e -> {
            long songId = e.get("song_id", BigInteger.class).longValue();
            String tagName = e.get("name", String.class);
            if (tagMap.containsKey(songId)) {
                Set<String> tagSet = tagMap.get(songId);
                tagSet.add(tagName);
            } else {
                Set<String> tagSet = new HashSet<>();
                tagSet.add(tagName);
                tagMap.put(songId, tagSet);
            }
        });
        return tagMap;
    }
}
