package com.alpha.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author thanhvt
 * @created 13/09/2021 - 2:03 SA
 * @project vengeance
 * @since 1.0
 **/
public class SchedulerConstants {

    private static final String QUEUE_BASE_DIR = "./queues";

    private static final String ALBUM_INSERT_LISTENING =
        "INSERT INTO user_favorite_albums (username, album_id, liked)\n"
            + "VALUES (?, ?, false)\n"
            + "    ON CONFLICT ON CONSTRAINT user_favorite_albums_pk\n"
            + "    DO NOTHING";

    private static final String ALBUM_UPDATE_LISTENING_COUNT = "UPDATE album SET listening_frequency = ? WHERE id = ?";

    private static final String ALBUM_UPSERT_LIKES =
        "INSERT INTO user_favorite_albums (username, album_id, liked)\n"
            + "VALUES (?, ?, ?)\n"
            + "ON CONFLICT ON CONSTRAINT user_favorite_albums_pk\n"
            + "    DO UPDATE SET liked = ?\n"
            + "WHERE user_favorite_albums.username = ?\n"
            + "  AND user_favorite_albums.album_id = ?";

    private static final String ARTIST_UPSERT_LIKES =
        "INSERT INTO user_favorite_artists (username, artist_id, liked)\n"
            + "VALUES (?, ?, ?)\n"
            + "ON CONFLICT ON CONSTRAINT user_favorite_artists_pk\n"
            + "    DO UPDATE SET liked = ?\n"
            + "WHERE user_favorite_artists.username = ?\n"
            + "  AND user_favorite_artists.artist_id = ?";

    private static final String SONG_INSERT_LISTENING =
        "INSERT INTO user_favorite_songs (username, song_id, liked)\n"
            + "VALUES (?, ?, false)\n"
            + "ON CONFLICT ON CONSTRAINT user_favorite_songs_pk\n"
            + "    DO NOTHING";

    private static final String SONG_UPDATE_LISTENING_COUNT = "UPDATE song SET listening_frequency = ? WHERE id = ?";

    private static final String SONG_UPSERT_LIKES =
        "INSERT INTO user_favorite_songs (username, song_id, liked)\n"
            + "VALUES (?, ?, ?)\n"
            + "ON CONFLICT ON CONSTRAINT user_favorite_songs_pk\n"
            + "    DO UPDATE SET liked = ?\n"
            + "WHERE user_favorite_songs.username = ?\n"
            + "  AND user_favorite_songs.song_id = ?";

    @Getter
    @AllArgsConstructor
    public enum LikeConfig {
        SONG(QUEUE_BASE_DIR, "song_likes.txt", "user_favorite_songs", SONG_UPSERT_LIKES),
        ALBUM(QUEUE_BASE_DIR, "album_likes.txt", "user_favorite_albums", ALBUM_UPSERT_LIKES),
        ARTIST(QUEUE_BASE_DIR, "artist_likes.txt", "user_favorite_artists", ARTIST_UPSERT_LIKES);
        private final String dir;

        private final String likesQueueFile;

        private final String table;

        private final String sql;
    }

    @Getter
    @AllArgsConstructor
    public enum ListeningConfig {
        SONG(QUEUE_BASE_DIR, "song_listening.txt", "user_favorite_songs",
            SONG_INSERT_LISTENING, SONG_UPDATE_LISTENING_COUNT),
        ALBUM(QUEUE_BASE_DIR, "album_listening.txt", "user_favorite_albums",
            ALBUM_INSERT_LISTENING, ALBUM_UPDATE_LISTENING_COUNT);
        private final String dir;

        private final String listeningQueueFile;

        private final String table;

        private final String listeningSql;

        private final String listeningCountSql;
    }
}
