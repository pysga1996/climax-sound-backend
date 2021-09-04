SELECT TOTAL,
       RN,
       alb.id            AS album_id,
       alb.title AS album_title,
       alb.unaccent_title AS album_unaccent_title,
       ast.id            AS artist_id,
       ast.name          AS artist_name,
       ast.unaccent_name AS artist_inaccent_name
FROM (SELECT COUNT(*) OVER ()        TOTAL,
             ROW_NUMBER() OVER () AS RN,
             *
      FROM album
      LIMIT :limit OFFSET :offset) AS alb
         LEFT JOIN album_artist albast
                   ON alb.id = albast.album_id
         LEFT JOIN artist ast
                   ON albast.artist_id = ast.id
