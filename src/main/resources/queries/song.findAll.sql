SELECT TOTAL,
       ROW_NUMBER() OVER (
           ORDER BY s.unaccent_title, s.title, a.unaccent_name, a.name
           )            AS RN,
       s.id             AS song_id,
       s.title          AS song_title,
       s.unaccent_title AS song_unaccent_title,
       s.duration,
       s.listening_frequency,
       s.like_count,
       s.release_date,
       a.id             AS artist_id,
       a.name           AS artist_name,
       a.unaccent_name  AS artist_unaccent_name,
       s.url
FROM (SELECT *,
             COUNT(*) OVER () AS TOTAL
      FROM song
               LEFT JOIN
           (SELECT DISTINCT media_id,
                            :baseUrl || (FIRST_VALUE(uri) OVER (PARTITION BY media_id)) AS url
            FROM resource_info
            WHERE media_type = :mediaType
              AND media_ref = :mediaRef) ri ON ri.media_id = song.id
      LIMIT :limit OFFSET :offset) s
         LEFT JOIN
     song_artist sa
     ON s.id = sa.song_id
         LEFT JOIN
     artist a
     ON sa.artist_id = a.id

