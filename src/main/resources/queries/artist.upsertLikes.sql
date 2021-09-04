INSERT INTO user_favorite_artists (username, artist_id, liked)
VALUES (?, ?, ?)
ON CONFLICT ON CONSTRAINT user_favorite_artists_pk
    DO UPDATE SET liked = ?
WHERE user_favorite_artists.username = ?
  AND user_favorite_artists.artist_id = ?