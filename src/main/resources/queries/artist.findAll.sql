SELECT COUNT(*) OVER ()                               TOTAL,
       ROW_NUMBER() OVER (ORDER BY list.artist_id) AS RN,
       *
FROM (
         SELECT DISTINCT a.id                                                       AS artist_id,
                         FIRST_VALUE(a.name) OVER (PARTITION BY a.id)               AS artist_name,
                         FIRST_VALUE(a.unaccent_name)
                         OVER (PARTITION BY a.id ORDER BY ri.id)                    AS artist_unaccent_name,
                         FIRST_VALUE(a.birth_date) OVER (PARTITION BY a.id)         AS birth_date,
                         (:baseUrl || FIRST_VALUE(ri.uri) OVER (PARTITION BY a.id)) AS avatar_url
         FROM artist a
                  LEFT JOIN resource_info ri
                            ON a.id = ri.media_id AND ri.media_ref = 'ARTIST_AVATAR' AND
                               ri.status = 1
     ) AS list
LIMIT :limit OFFSET :offset