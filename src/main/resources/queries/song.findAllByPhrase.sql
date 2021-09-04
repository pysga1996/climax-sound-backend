SELECT TOTAL,
       ROW_NUMBER() OVER (
           ORDER BY s.unaccent_title, s.title, a.unaccent_name, a.name
           )  AS RN,
       s.id,
       s.title,
       s.unaccent_title,
       s.duration,
       s.listening_frequency,
       s.like_count,
       s.release_date,
       a.id   AS artist_id,
       a.name AS artist_name
FROM (SELECT *,
             COUNT(*) OVER () AS TOTAL
      FROM song
      LIMIT :limit OFFSET :offset) s
         LEFT JOIN
     song_artist sa
     ON s.id = sa.song_id
         LEFT JOIN
     artist a
     ON sa.artist_id = a.id
WHERE LOWER(title) LIKE CONCAT('%', :phrase, '%')
   OR LOWER(unaccent_title) LIKE CONCAT('%', :phrase, '%')
   OR LOWER(a.name) LIKE CONCAT('%', :phrase, '%')
