package com.alpha.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface LikeService {

    void like(Long id, LikeConfig likeConfig, boolean isLiked);

    void listen(Long id, ListeningConfig listeningConfig, String username);

    void writeLikesToQueue(String username, Long id,
        boolean isLiked, LikeConfig likeConfig);

    void writeListenToQueue(String username, Long id, ListeningConfig listeningConfig);

    void insertLikesToDb(LikeConfig likeConfig, int batchSize);

    void insertSongLikesToDb();

    void insertAlbumLikesToDb();

    void insertArtistLikesToDb();

    void updateListeningToDb(ListeningConfig listeningConfig, int batchSize);

    void updateSongListeningToDb();

    void updateAlbumListeningToDb();

    void updateSongListeningCountToDb();

    void updateAlbumListeningCountToDb();

    void updateListeningCountToDb(ListeningConfig listeningConfig, int batchSize);

    @Getter
    @AllArgsConstructor
    enum LikeConfig {
        SONG(QUEUE_BASE_DIR, "song_likes.txt", "user_favorite_songs", "song.upsertLikes.sql"),
        ALBUM(QUEUE_BASE_DIR, "album_likes.txt", "user_favorite_albums", "album.upsertLikes.sql"),
        ARTIST(QUEUE_BASE_DIR, "artist_likes.txt", "user_favorite_artists",
            "artist.upsertLikes.sql");
        private final String dir;

        private final String likesQueueFile;

        private final String table;

        private final String sqlFile;
    }

    @Getter
    @AllArgsConstructor
    enum ListeningConfig {
        SONG(QUEUE_BASE_DIR, "song_listening.txt", "user_favorite_songs",
            "song.insertListening.sql", "song.updateListeningCount.sql"),
        ALBUM(QUEUE_BASE_DIR, "album_listening.txt", "user_favorite_albums",
            "album.insertListening.sql", "album.updateListeningCount.sql");
        private final String dir;

        private final String likesQueueFile;

        private final String table;

        private final String listeningSqlFile;

        private final String listeningCountSqlFile;
    }
}
