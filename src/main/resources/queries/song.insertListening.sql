INSERT INTO user_favorite_songs (username, song_id, liked)
VALUES (?, ?, false)
ON CONFLICT ON CONSTRAINT user_favorite_songs_pk
    DO NOTHING