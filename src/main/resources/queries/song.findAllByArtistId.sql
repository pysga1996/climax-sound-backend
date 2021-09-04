SELECT TOTAL,
       ROW_NUMBER() OVER (
           ORDER BY s.unaccent_title, s.title, a.unaccent_name, a.name
           )  AS RN,
       s.id AS song_id,
       s.title AS song_title,
       s.unaccent_title AS song_unaccent_title,
       s.duration,
       s.listening_frequency,
       s.like_count,
       s.release_date,
       a.id   AS artist_id,
       a.name AS artist_name,
       a.unaccent_name AS artist_unaccent_name
FROM (SELECT song.*,
             COUNT(*) OVER () AS TOTAL
      FROM song JOIN
           song_artist sa ON song.id = sa.song_id
      WHERE sa.artist_id = :artistId
      LIMIT :limit OFFSET :offset) s
         LEFT JOIN
     song_artist sa
     ON s.id = sa.song_id
         LEFT JOIN
     artist a
     ON sa.artist_id = a.id