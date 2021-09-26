package com.alpha.constant;

/**
 * @author thanhvt
 * @created 13/09/2021 - 2:03 SA
 * @project vengeance
 * @since 1.0
 **/
public class SchedulerConstants {

    public static final String LIKES_TOPIC = "likes";

    public static final String LISTENING_TOPIC = "listening";

    public static final String LIKES_CACHE = "likes_cache";

    public static final String LISTENING_CACHE = "listening_cache";

    public static final String QUEUE_BASE_DIR = "./queues";

    public static final String LIKES_QUEUE_FILE = "likes.txt";

    public static final String LISTENING_QUEUE_FILE = "listening.txt";

    public static final String UPSERT_LIKES_SQL =
        "INSERT INTO user_favorites (username, entity_id, liked, type)\n"
            + "VALUES (?, ?, ?, ?)\n"
            + "ON CONFLICT ON CONSTRAINT favorites_pk\n"
            + "    DO UPDATE SET liked = ?\n"
            + "WHERE user_favorites.username = ?\n"
            + "  AND user_favorites.entity_id = ?\n"
            + "  AND user_favorites.type = ?\n";

    public static final String INSERT_LISTENING_SQL =
        "INSERT INTO user_favorites (username, entity_id, liked, type)\n"
            + "VALUES (?, ?, false, ?)\n"
            + "    ON CONFLICT ON CONSTRAINT favorites_pk\n"
            + "    DO NOTHING";

    public static final String ALBUM_UPDATE_LISTENING_COUNT = "UPDATE album SET listening_frequency = ? WHERE id = ?";

    public static final String SONG_UPDATE_LISTENING_COUNT = "UPDATE song SET listening_frequency = ? WHERE id = ?";

    public static final String ALBUM_UPDATE_LIKES_COUNT = "UPDATE album SET like_count = ? WHERE id = ?";

    public static final String SONG_UPDATE_LIKES_COUNT = "UPDATE song SET like_count = ? WHERE id = ?";

    public static class CacheQueue {
        public static final String SONG_LISTENING_CACHE_QUEUE = "song_listening_cache_queue";
        public static final String ALBUM_LISTENING_CACHE_QUEUE = "album_listening_cache_queue";
        public static final String SONG_LIKES_CACHE_QUEUE = "song_likes_cache_queue";
        public static final String ALBUM_LIKES_CACHE_QUEUE = "album_likes_cache_queue";
    }
}
