INSERT INTO user_favorite_songs (username, song_id, liked)
VALUES (?, ?, ?)
ON CONFLICT ON CONSTRAINT user_favorite_songs_pk
    DO UPDATE SET liked = ?
WHERE user_favorite_songs.username = ?
  AND user_favorite_songs.song_id = ?