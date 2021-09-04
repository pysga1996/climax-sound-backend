create function find_song_by_conditions(p_base_url character varying DEFAULT ''::character varying,
                                        p_storage_type character varying DEFAULT 'LOCAL'::character varying,
                                        p_artist_id numeric DEFAULT NULL::numeric,
                                        p_album_id numeric DEFAULT NULL::numeric,
                                        p_username character varying DEFAULT NULL::character varying,
                                        p_phrase character varying DEFAULT NULL::character varying,
                                        p_size numeric DEFAULT 10, p_page numeric DEFAULT 0,
                                        p_sort character varying DEFAULT NULL::character varying) returns refcursor
    language plpgsql
as
$$
DECLARE
ref        REFCURSOR := 'song_cursor'; -- Declare a cursor variable
    PAGE_BEGIN NUMERIC   := p_page * p_size;
    PAGE_END   NUMERIC   := p_page * p_size + p_size;
BEGIN
OPEN ref FOR
        WITH ri_ref AS (SELECT DISTINCT media_id,
                                        P_BASE_URL ||
                                        (FIRST_VALUE(uri) OVER (PARTITION BY media_id)) AS url
                        FROM resource_info ri
                        WHERE ri.media_ref = 'SONG_AUDIO'
                          AND ri.storage_type = P_STORAGE_TYPE),
             ri_a_ref AS (SELECT DISTINCT media_id,
                                          P_BASE_URL ||
                                          (FIRST_VALUE(uri) OVER (PARTITION BY media_id)) AS url
                          FROM resource_info ri
                          WHERE ri.media_ref = 'ARTIST_AVATAR'
                            AND ri.storage_type = P_STORAGE_TYPE),
             s_ref AS (SELECT DISTINCT s_tmp.id,
                                       FIRST_VALUE(s_tmp.title) OVER (PARTITION BY s_tmp.id) AS tmp_title,
                                       FIRST_VALUE(s_tmp.listening_frequency)
                                       OVER (PARTITION BY s_tmp.id)                          AS tmp_listening_frequency,
                                       FIRST_VALUE(s_tmp.release_date)
                                       OVER (PARTITION BY s_tmp.id)                          AS tmp_release_date
                       FROM song s_tmp
                                LEFT JOIN album_song as_tmp on s_tmp.id = as_tmp.song_id
                                LEFT JOIN song_artist sa_tmp on s_tmp.id = sa_tmp.song_id
                                LEFT JOIN artist a_tmp on sa_tmp.artist_id = a_tmp.id
                       WHERE (P_PHRASE IS NULL OR LOWER(s_tmp.title) LIKE CONCAT('%', P_PHRASE, '%')
                           OR LOWER(s_tmp.unaccent_title) LIKE CONCAT('%', P_PHRASE, '%')
                           OR LOWER(a_tmp.name) LIKE CONCAT('%', P_PHRASE, '%')
                           OR LOWER(a_tmp.unaccent_name) LIKE CONCAT('%', P_PHRASE, '%'))
                         AND (P_ARTIST_ID IS NULL OR a_tmp.id = P_ARTIST_ID)
                         AND (P_ALBUM_ID IS NULL OR as_tmp.album_id = P_ALBUM_ID)
                         AND (P_USERNAME IS NULL OR s_tmp.username = P_USERNAME)
             ),
             rn_tmp AS (
                 SELECT s_ref.id,
                        COUNT(*) OVER ()    AS TOTAL,
                        ROW_NUMBER() OVER (ORDER BY
                            (CASE
                                 WHEN p_sort = 'listening_frequency'
                                     THEN tmp_listening_frequency END) DESC,
                            (CASE WHEN p_sort = 'release_date' THEN tmp_release_date END) DESC,
                            tmp_title ASC ) AS RN
                 FROM s_ref
             ),
             result AS (
                 SELECT rn_tmp.TOTAL,
                        rn_tmp.RN,
                        s.id             AS song_id,
                        s.title          AS song_title,
                        s.unaccent_title AS song_unaccent_title,
                        s.duration,
                        s.listening_frequency,
                        s.like_count,
                        s.release_date,
                        s.username,
                        ri_ref.url       AS url,
                        a.id             AS artist_id,
                        a.name           AS artist_name,
                        a.unaccent_name  AS artist_unaccent_name,
                        ri_a_ref.url     AS artist_avatar
                 FROM song s
                          INNER JOIN rn_tmp ON s.id = rn_tmp.id
                          LEFT JOIN ri_ref ON ri_ref.media_id = rn_tmp.id
                          LEFT JOIN song_artist sa ON rn_tmp.id = sa.song_id
                          LEFT JOIN artist a ON sa.artist_id = a.id
                          LEFT JOIN ri_a_ref ON a.id = ri_a_ref.media_id
             )
SELECT *
FROM result
WHERE RN > PAGE_BEGIN
  AND RN <= PAGE_END
ORDER BY RN;
RETURN ref; -- Return the cursor to the caller
END;
$$;


create function find_album_by_conditions(p_base_url character varying DEFAULT ''::character varying, p_storage_type character varying DEFAULT 'LOCAL'::character varying, p_artist_id numeric DEFAULT NULL::numeric, p_album_id numeric DEFAULT NULL::numeric, p_username_fav character varying DEFAULT NULL::character varying, p_username character varying DEFAULT NULL::character varying, p_phrase character varying DEFAULT NULL::character varying, p_size numeric DEFAULT 10, p_page numeric DEFAULT 0, p_sort character varying DEFAULT NULL::character varying) returns refcursor
    language plpgsql
as
$$
DECLARE
    ref             REFCURSOR := 'album_cursor'; -- Declare a cursor variable
    PAGE_BEGIN      NUMERIC   := p_page * p_size;
    PAGE_END        NUMERIC   := p_page * p_size + p_size;
    MODIFIED_PHRASE VARCHAR   := CONCAT('%', LOWER(P_PHRASE), '%');
BEGIN
    OPEN ref FOR
        WITH ri_ref AS (SELECT DISTINCT media_id,
                                        P_BASE_URL ||
                                        (FIRST_VALUE(uri) OVER (PARTITION BY media_id)) AS url
                        FROM resource_info ri
                        WHERE ri.media_ref = 'ALBUM_COVER'
                          AND ri.storage_type = P_STORAGE_TYPE),
             s_ref AS (SELECT DISTINCT a_tmp.id,
                                       FIRST_VALUE(a_tmp.title) OVER (PARTITION BY a_tmp.id) AS tmp_title,
                                       FIRST_VALUE(a_tmp.listening_frequency)
                                       OVER (PARTITION BY a_tmp.id)                          AS tmp_listening_frequency,
                                       FIRST_VALUE(a_tmp.release_date)
                                       OVER (PARTITION BY a_tmp.id)                          AS tmp_release_date
                       FROM album a_tmp
                                LEFT JOIN album_artist aa_tmp on a_tmp.id = aa_tmp.album_id
                                LEFT JOIN artist a_tmp on aa_tmp.artist_id = a_tmp.id
                                LEFT JOIN user_favorite_albums ufa on a_tmp.id = ufa.album_id
                       WHERE (P_PHRASE IS NULL OR LOWER(a_tmp.title) LIKE MODIFIED_PHRASE
                           OR LOWER(a_tmp.unaccent_title) LIKE MODIFIED_PHRASE
                           OR LOWER(a_tmp.name) LIKE MODIFIED_PHRASE
                           OR LOWER(a_tmp.unaccent_name) LIKE MODIFIED_PHRASE)
                         AND (P_ARTIST_ID IS NULL OR a_tmp.id = P_ARTIST_ID)
                         AND (P_ALBUM_ID IS NULL OR a_tmp.id = P_ALBUM_ID)
                         AND (P_USERNAME_FAV IS NULL OR ufa.username = p_username_fav)
                         AND (P_USERNAME IS NULL OR a_tmp.username = P_USERNAME)
             ),
             rn_tmp AS (
                 SELECT s_ref.id,
                        COUNT(*) OVER () AS TOTAL,
                        ROW_NUMBER() OVER (ORDER BY
                            (CASE
                                 WHEN p_sort = 'listening_frequency'
                                     THEN tmp_listening_frequency END) DESC,
                            (CASE WHEN p_sort = 'release_date' THEN tmp_release_date END) DESC,
                            tmp_title)   AS RN
                 FROM s_ref
             ),
             result AS (
                 SELECT rn_tmp.TOTAL,
                        rn_tmp.RN,
                        a.id             AS song_id,
                        a.title          AS song_title,
                        a.unaccent_title AS song_unaccent_title,
                        a.duration,
                        a.listening_frequency,
                        a.like_count,
                        a.release_date,
                        a.username,
                        ri_ref.url       AS cover_url,
                        a.id             AS artist_id,
                        a.name           AS artist_name,
                        a.unaccent_name  AS artist_unaccent_name
                 FROM album a
                          INNER JOIN rn_tmp ON a.id = rn_tmp.id
                          LEFT JOIN ri_ref ON ri_ref.media_id = rn_tmp.id
                          LEFT JOIN song_artist sa ON rn_tmp.id = sa.song_id
                          LEFT JOIN artist a ON sa.artist_id = a.id
             )
        SELECT *
        FROM result
        WHERE RN > PAGE_BEGIN
          AND RN <= PAGE_END
        ORDER BY RN;
    RETURN ref; -- Return the cursor to the caller
END;
$$;