INSERT INTO user_favorite_albums (username, album_id, liked)
VALUES (?, ?, ?)
ON CONFLICT ON CONSTRAINT user_favorite_albums_pk
    DO UPDATE SET liked = ?
WHERE user_favorite_albums.username = ?
  AND user_favorite_albums.album_id = ?