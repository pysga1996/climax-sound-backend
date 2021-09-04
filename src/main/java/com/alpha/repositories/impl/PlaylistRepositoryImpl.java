package com.alpha.repositories.impl;

import com.alpha.repositories.BaseRepository;
import com.alpha.repositories.PlaylistRepositoryCustom;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 24/08/2021 - 10:54 CH
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Getter
@Repository
public class PlaylistRepositoryImpl extends BaseRepository implements PlaylistRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addToPlayList(String username, Long playlistId, List<Long> songIds) {
        String sql = "INSERT INTO playlist_song (playlist_id, song_id)\n"
            + "VALUES(?, ?) \n"
            + "ON CONFLICT ON CONSTRAINT playlist_song_pk\n"
            + "DO \n"
            + "UPDATE SET song_id = ? WHERE playlist_song.playlist_id = ?";
        int addedCount = this.batchInsertUpdateDelete(sql, songIds, (statement, songId, i) -> {
            log.debug("Insert song #{} - {} to playlist {}", i, songId, playlistId);
            statement.setLong(1, playlistId);
            statement.setLong(2, songId);
            statement.setLong(3, songId);
            statement.setLong(4, playlistId);
        });
        log.info("{} songs were added to playlist {}", addedCount, playlistId);
    }

    @Override
    public void removeFromPlaylist(String username, Long playlistId, List<Long> songIds) {
        String sql = "DELETE FROM playlist_song WHERE playlist_id = :id and song_id = :songId";
        int removedCount = this.batchInsertUpdateDelete(sql, songIds, (statement, songId, i) -> {
            log.debug("Delete song #{} - {} from playlist {}", i, songId, playlistId);
            statement.setLong(1, playlistId);
            statement.setLong(2, songId);
        });
        log.debug("{} songs were removed from playlist {}", removedCount, playlistId);
    }
}
