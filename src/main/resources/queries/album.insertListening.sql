INSERT INTO user_favorite_albums (username, album_id, liked)
VALUES (?, ?, false)
    ON CONFLICT ON CONSTRAINT user_favorite_albums_pk
    DO NOTHING