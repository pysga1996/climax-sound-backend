--
-- PostgreSQL database dump
--

-- Dumped from database version 13.8 (Ubuntu 13.8-1.pgdg20.04+1)
-- Dumped by pg_dump version 13.8

-- Started on 2022-09-27 22:48:43

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 998827)
-- Name: alpha_sound; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA alpha_sound;


--
-- TOC entry 4289 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA alpha_sound; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA alpha_sound IS 'Music management database';


--
-- TOC entry 252 (class 1255 OID 14888039)
-- Name: delete_album(bigint); Type: PROCEDURE; Schema: alpha_sound; Owner: -
--

CREATE PROCEDURE alpha_sound.delete_album(p_id bigint)
    LANGUAGE sql
AS $$
SELECT * FROM album WHERE id = p_id;
$$;


--
-- TOC entry 269 (class 1255 OID 15226556)
-- Name: find_album_additional_info(numeric); Type: FUNCTION; Schema: alpha_sound; Owner: -
--

CREATE FUNCTION alpha_sound.find_album_additional_info(p_album_id numeric) RETURNS refcursor
    LANGUAGE plpgsql
AS $$
DECLARE
    ref             REFCURSOR := 'album_cursor'; -- Declare a cursor variable
BEGIN
    OPEN ref FOR
        SELECT a1.id,
               a1.title,
               '1_album'                                      AS type,
               FIRST_VALUE(c.id) OVER (PARTITION BY a1.id)    AS country_id,
               FIRST_VALUE(c.name) OVER (PARTITION BY a1.id)  AS country_name,
               FIRST_VALUE(th.id) OVER (PARTITION BY a1.id)   AS theme_id,
               FIRST_VALUE(th.name) OVER (PARTITION BY a1.id) AS theme_name,
               a1.description
        FROM album a1
                 LEFT JOIN country c on c.id = a1.country_id
                 LEFT JOIN theme th on a1.theme_id = th.id
        WHERE a1.id = p_album_id
        UNION
        (SELECT g.id,
                g.name,
                '2_genre' AS type,
                NULL,
                NULL,
                NULL,
                NULL,
                NULL
         FROM album a2
                  LEFT JOIN album_genre ag on a2.id = ag.album_id
                  LEFT JOIN genre g on ag.genre_id = g.id
         WHERE a2.id = p_album_id)
        UNION
        (SELECT t.id,
                t.name,
                '3_tag' AS type,
                NULL,
                NULL,
                NULL,
                NULL,
                NULL
         FROM album a3
                  LEFT JOIN album_tag at ON a3.id = at.album_id
                  LEFT JOIN tag t on at.tag_id = t.id
         WHERE a3.id = p_album_id)
        ORDER BY type;
    RETURN ref; -- Return the cursor to the caller
END;
$$;


--
-- TOC entry 253 (class 1255 OID 14888040)
-- Name: find_album_by_conditions(character varying, character varying, numeric, numeric, character varying, character varying, character varying, numeric, numeric, character varying); Type: FUNCTION; Schema: alpha_sound; Owner: -
--

CREATE FUNCTION alpha_sound.find_album_by_conditions(p_base_url character varying DEFAULT ''::character varying, p_storage_type character varying DEFAULT 'LOCAL'::character varying, p_artist_id numeric DEFAULT NULL::numeric, p_album_id numeric DEFAULT NULL::numeric, p_username_fav character varying DEFAULT NULL::character varying, p_username character varying DEFAULT NULL::character varying, p_phrase character varying DEFAULT NULL::character varying, p_size numeric DEFAULT 10, p_page numeric DEFAULT 0, p_sort character varying DEFAULT NULL::character varying) RETURNS refcursor
    LANGUAGE plpgsql
AS $$
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
                          AND ri.storage_type = P_STORAGE_TYPE
                          AND ri.status = 1),
             ri_a_ref AS (SELECT DISTINCT media_id,
                                          P_BASE_URL ||
                                          (FIRST_VALUE(uri) OVER (PARTITION BY media_id)) AS url
                          FROM resource_info ri
                          WHERE ri.media_ref = 'ARTIST_AVATAR'
                            AND ri.storage_type = P_STORAGE_TYPE
                            AND ri.status = 1),
             s_ref AS (SELECT DISTINCT alb_tmp.id,
                                       FIRST_VALUE(alb_tmp.title) OVER (PARTITION BY alb_tmp.id) AS tmp_title,
                                       FIRST_VALUE(alb_tmp.listening_frequency)
                                       OVER (PARTITION BY alb_tmp.id)                            AS tmp_listening_frequency,
                                       FIRST_VALUE(alb_tmp.release_date)
                                       OVER (PARTITION BY alb_tmp.id)                            AS tmp_release_date
                       FROM album alb_tmp
                                LEFT JOIN album_artist aa_tmp on alb_tmp.id = aa_tmp.album_id
                                LEFT JOIN artist art_tmp on aa_tmp.artist_id = art_tmp.id
                                LEFT JOIN user_favorites ufa on alb_tmp.id = ufa.entity_id AND ufa.type = 'ALBUM'
                       WHERE (P_PHRASE IS NULL OR (LOWER(alb_tmp.title) LIKE MODIFIED_PHRASE
                           OR LOWER(alb_tmp.unaccent_title) LIKE MODIFIED_PHRASE
                           OR LOWER(art_tmp.name) LIKE MODIFIED_PHRASE
                           OR LOWER(art_tmp.unaccent_name) LIKE MODIFIED_PHRASE))
                         AND (P_ARTIST_ID IS NULL OR art_tmp.id = P_ARTIST_ID)
                         AND (P_ALBUM_ID IS NULL OR alb_tmp.id = P_ALBUM_ID)
                         AND (P_USERNAME_FAV IS NULL OR ufa.username = p_username_fav)
                         AND (P_USERNAME IS NULL OR alb_tmp.username = P_USERNAME)
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
                        alb.id             AS album_id,
                        alb.title          AS album_title,
                        alb.unaccent_title AS album_unaccent_title,
                        alb.duration,
                        alb.listening_frequency,
                        alb.like_count,
                        alb.release_date,
                        alb.username,
                        ri_ref.url         AS cover_url,
                        art.id             AS artist_id,
                        art.name           AS artist_name,
                        art.unaccent_name  AS artist_unaccent_name,
                        ri_a_ref.url       AS artist_avatar
                 FROM album alb
                          INNER JOIN rn_tmp ON alb.id = rn_tmp.id
                          LEFT JOIN ri_ref ON ri_ref.media_id = rn_tmp.id
                          LEFT JOIN album_artist albart ON rn_tmp.id = albart.album_id
                          LEFT JOIN artist art ON albart.artist_id = art.id
                          LEFT JOIN ri_a_ref ON alb.id = ri_a_ref.media_id
             )
        SELECT *
        FROM result
        WHERE RN > PAGE_BEGIN
          AND RN <= PAGE_END
        ORDER BY RN;
    RETURN ref; -- Return the cursor to the caller
END;
$$;


--
-- TOC entry 270 (class 1255 OID 14888042)
-- Name: find_artist_by_conditions(character varying, character varying, numeric, numeric, character varying, character varying, character varying, numeric, numeric, character varying); Type: FUNCTION; Schema: alpha_sound; Owner: -
--

CREATE FUNCTION alpha_sound.find_artist_by_conditions(p_base_url character varying DEFAULT ''::character varying, p_storage_type character varying DEFAULT 'LOCAL'::character varying, p_song_id numeric DEFAULT NULL::numeric, p_album_id numeric DEFAULT NULL::numeric, p_username_fav character varying DEFAULT NULL::character varying, p_username character varying DEFAULT NULL::character varying, p_phrase character varying DEFAULT NULL::character varying, p_size numeric DEFAULT 10, p_page numeric DEFAULT 0, p_sort character varying DEFAULT NULL::character varying) RETURNS refcursor
    LANGUAGE plpgsql
AS $$
DECLARE
    ref             REFCURSOR := 'artist_cursor'; -- Declare a cursor variable
    PAGE_BEGIN      NUMERIC   := p_page * p_size;
    PAGE_END        NUMERIC   := p_page * p_size + p_size;
    MODIFIED_PHRASE VARCHAR   := CONCAT('%', LOWER(P_PHRASE), '%');
BEGIN
    OPEN ref FOR
        WITH ri_ref AS (SELECT DISTINCT media_id,
                                        P_BASE_URL ||
                                        (FIRST_VALUE(uri) OVER (PARTITION BY media_id)) AS url
                        FROM resource_info ri
                        WHERE ri.media_ref = 'ARTIST_AVATAR'
                          AND ri.storage_type = P_STORAGE_TYPE
                          AND ri.status = 1),
             s_ref AS (SELECT DISTINCT art_tmp.id,
                                       FIRST_VALUE(art_tmp.name) OVER (PARTITION BY art_tmp.id) AS tmp_name,
                                       FIRST_VALUE(art_tmp.like_count)
                                       OVER (PARTITION BY art_tmp.id)                           AS tmp_like_count,
                                       FIRST_VALUE(art_tmp.birth_date)
                                       OVER (PARTITION BY art_tmp.id)                           AS tmp_birth_date,
                                       FIRST_VALUE(aa_tmp."order")
                                       OVER (PARTITION BY art_tmp.id)                           AS album_order,
                                       FIRST_VALUE(sa_tmp."order")
                                       OVER (PARTITION BY art_tmp.id)                           AS song_order
                       FROM artist art_tmp
                                LEFT JOIN album_artist aa_tmp ON art_tmp.id = aa_tmp.artist_id
                                LEFT JOIN song_artist sa_tmp ON art_tmp.id = sa_tmp.artist_id
                                LEFT JOIN user_favorites ufa ON art_tmp.id = ufa.entity_id AND ufa.type = 'ARTIST'
                       WHERE ((P_PHRASE IS NULL OR
                               (LOWER(art_tmp.name) LIKE MODIFIED_PHRASE OR
                                LOWER(art_tmp.unaccent_name) LIKE MODIFIED_PHRASE))
                           AND (P_SONG_ID IS NULL OR sa_tmp.song_id = P_SONG_ID)
                           AND (P_ALBUM_ID IS NULL OR aa_tmp.album_id = P_ALBUM_ID)
                           AND (P_USERNAME_FAV IS NULL OR ufa.username = p_username_fav)
                           AND (P_USERNAME IS NULL OR art_tmp.username = P_USERNAME)
                                 )
             ),
             rn_tmp AS (
                 SELECT s_ref.id,
                        COUNT(*) OVER () AS TOTAL,
                        ROW_NUMBER() OVER (ORDER BY
                            (CASE WHEN P_ALBUM_ID IS NOT NULL THEN album_order END),
                            (CASE WHEN P_SONG_ID IS NOT NULL THEN song_order END),
                            (CASE
                                 WHEN P_SORT = 'like_count'
                                     THEN tmp_birth_date END) DESC,
                            (CASE WHEN P_SORT = 'birth_date' THEN tmp_birth_date END) DESC,
                            tmp_name
                            )            AS RN
                 FROM s_ref
             ),
             result AS (
                 SELECT rn_tmp.TOTAL,
                        rn_tmp.RN,
                        art.id            AS artist_id,
                        art.name          AS artist_name,
                        art.unaccent_name AS artist_unaccent_name,
                        art.birth_date    AS birth_date,
                        art.like_count    AS like_count,
                        ri_ref.url        AS avatar_url
                 FROM artist art
                          INNER JOIN rn_tmp ON art.id = rn_tmp.id
                          LEFT JOIN ri_ref ON ri_ref.media_id = rn_tmp.id
             )
        SELECT *
        FROM result
        WHERE RN > PAGE_BEGIN
          AND RN <= PAGE_END
        ORDER BY RN;
    RETURN ref; -- Return the cursor to the caller
END;
$$;


--
-- TOC entry 267 (class 1255 OID 15182251)
-- Name: find_song_additional_info(numeric); Type: FUNCTION; Schema: alpha_sound; Owner: -
--

CREATE FUNCTION alpha_sound.find_song_additional_info(p_song_id numeric) RETURNS refcursor
    LANGUAGE plpgsql
AS $$
DECLARE
    ref             REFCURSOR := 'song_cursor'; -- Declare a cursor variable
BEGIN
    OPEN ref FOR
        SELECT s.id,
               s.title,
               '1_song'                                      AS type,
               FIRST_VALUE(c.id) OVER (PARTITION BY s.id)    AS country_id,
               FIRST_VALUE(c.name) OVER (PARTITION BY s.id)  AS country_name,
               FIRST_VALUE(th.id) OVER (PARTITION BY s.id)   AS theme_id,
               FIRST_VALUE(th.name) OVER (PARTITION BY s.id) AS theme_name,
               s.lyric
        FROM song s
                 LEFT JOIN country c on c.id = s.country_id
                 LEFT JOIN theme th on s.theme_id = th.id
        WHERE s.id = p_song_id
        UNION
        (SELECT g.id,
                g.name,
                '2_genre' AS type,
                NULL,
                NULL,
                NULL,
                NULL,
                NULL
         FROM song s2
                  LEFT JOIN song_genre sg on s2.id = sg.song_id
                  LEFT JOIN genre g on sg.genre_id = g.id
         WHERE s2.id = p_song_id)
        UNION
        (SELECT t.id,
                t.name,
                '3_tag' AS type,
                NULL,
                NULL,
                NULL,
                NULL,
                NULL
         FROM song s3
                  LEFT JOIN song_tag st ON s3.id = st.song_id
                  LEFT JOIN tag t on st.tag_id = t.id
         WHERE s3.id = p_song_id)
        ORDER BY type;
    RETURN ref; -- Return the cursor to the caller
END;
$$;


--
-- TOC entry 266 (class 1255 OID 14888043)
-- Name: find_song_by_conditions(character varying, character varying, numeric, numeric, numeric, character varying, character varying, character varying, numeric, numeric, character varying); Type: FUNCTION; Schema: alpha_sound; Owner: -
--

CREATE FUNCTION alpha_sound.find_song_by_conditions(p_base_url character varying DEFAULT ''::character varying, p_storage_type character varying DEFAULT 'LOCAL'::character varying, p_artist_id numeric DEFAULT NULL::numeric, p_album_id numeric DEFAULT NULL::numeric, p_playlist_id numeric DEFAULT NULL::numeric, p_username_fav character varying DEFAULT NULL::character varying, p_username character varying DEFAULT NULL::character varying, p_phrase character varying DEFAULT NULL::character varying, p_size numeric DEFAULT 10, p_page numeric DEFAULT 0, p_sort character varying DEFAULT NULL::character varying) RETURNS refcursor
    LANGUAGE plpgsql
AS $$
DECLARE
    ref             REFCURSOR := 'song_cursor'; -- Declare a cursor variable
    PAGE_BEGIN      NUMERIC   := p_page * p_size;
    PAGE_END        NUMERIC   := p_page * p_size + p_size;
    MODIFIED_PHRASE VARCHAR   := CONCAT('%', LOWER(P_PHRASE), '%');
BEGIN
    OPEN ref FOR
        WITH ri_ref AS (SELECT DISTINCT media_id,
                                        P_BASE_URL ||
                                        (FIRST_VALUE(uri) OVER (PARTITION BY media_id)) AS url
                        FROM resource_info ri
                        WHERE ri.media_ref = 'SONG_AUDIO'
                          AND ri.storage_type = P_STORAGE_TYPE
                          AND ri.status = 1),
             ri_a_ref AS (SELECT DISTINCT media_id,
                                          P_BASE_URL ||
                                          (FIRST_VALUE(uri) OVER (PARTITION BY media_id)) AS url
                          FROM resource_info ri
                          WHERE ri.media_ref = 'ARTIST_AVATAR'
                            AND ri.storage_type = P_STORAGE_TYPE
                            AND ri.status = 1),
             s_ref AS (SELECT DISTINCT s_tmp.id,
                                       FIRST_VALUE(s_tmp.title) OVER (PARTITION BY s_tmp.id) AS tmp_title,
                                       FIRST_VALUE(s_tmp.listening_frequency)
                                       OVER (PARTITION BY s_tmp.id)                          AS tmp_listening_frequency,
                                       FIRST_VALUE(s_tmp.release_date)
                                       OVER (PARTITION BY s_tmp.id)                          AS tmp_release_date,
                                       FIRST_VALUE(as_tmp.ordinal_number)
                                       OVER (PARTITION BY s_tmp.id)                          AS album_order
                       FROM song s_tmp
                                LEFT JOIN album_song as_tmp on s_tmp.id = as_tmp.song_id
                                LEFT JOIN song_artist sa_tmp on s_tmp.id = sa_tmp.song_id
                                LEFT JOIN artist a_tmp on sa_tmp.artist_id = a_tmp.id
                                LEFT JOIN playlist_song p_tmp on s_tmp.id = p_tmp.song_id
                                LEFT JOIN user_favorites ufs on s_tmp.id = ufs.entity_id AND ufs.type = 'SONG'
                       WHERE (P_PHRASE IS NULL OR (LOWER(s_tmp.title) LIKE MODIFIED_PHRASE
                           OR LOWER(s_tmp.unaccent_title) LIKE MODIFIED_PHRASE
                           OR LOWER(a_tmp.name) LIKE MODIFIED_PHRASE
                           OR LOWER(a_tmp.unaccent_name) LIKE MODIFIED_PHRASE))
                         AND (P_ARTIST_ID IS NULL OR a_tmp.id = P_ARTIST_ID)
                         AND (P_ALBUM_ID IS NULL OR as_tmp.album_id = P_ALBUM_ID)
                         AND (P_PLAYLIST_ID IS NULL OR p_tmp.playlist_id = P_PLAYLIST_ID)
                         AND (P_USERNAME_FAV IS NULL OR ufs.username = p_username_fav)
                         AND (P_USERNAME IS NULL OR s_tmp.username = P_USERNAME)
             ),
             rn_tmp AS (
                 SELECT s_ref.id,
                        COUNT(*) OVER ()    AS TOTAL,
                        ROW_NUMBER() OVER (ORDER BY
                            (CASE WHEN P_ALBUM_ID IS NOT NULL THEN album_order END),
                            (CASE
                                 WHEN P_SORT = 'listening_frequency'
                                     THEN tmp_listening_frequency END) DESC,
                            (CASE WHEN P_SORT = 'release_date' THEN tmp_release_date END) DESC,
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


--
-- TOC entry 271 (class 1255 OID 15310698)
-- Name: update_album_song_list(bigint, bigint, integer, character varying); Type: PROCEDURE; Schema: alpha_sound; Owner: -
--

CREATE PROCEDURE alpha_sound.update_album_song_list(p_album_id bigint, p_song_id bigint, p_ordinal_number integer, p_mode character varying)
    LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    IF (p_mode = 'CREATE' OR p_mode = 'VIEW') THEN
        INSERT INTO album_song(album_id, ordinal_number, song_id)
        VALUES (p_album_id, p_ordinal_number, p_song_id)
        ON CONFLICT ON CONSTRAINT album_song_pk
            DO UPDATE SET ordinal_number = p_ordinal_number
        WHERE album_song.album_id = p_album_id
          AND album_song.song_id = p_song_id;
    ELSEIF p_mode = 'UPDATE' THEN
        UPDATE album_song
        SET ordinal_number = p_ordinal_number
        WHERE album_song.album_id = p_album_id
          AND album_song.song_id = p_song_id;
    ELSEIF p_mode = 'DELETE' THEN
        DELETE FROM album_song WHERE album_song.album_id = p_album_id AND album_song.song_id = p_song_id;
    ELSE
    END IF;
END;
$$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 221 (class 1259 OID 14888050)
-- Name: album; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.album (
                                   id bigint NOT NULL,
                                   release_date timestamp without time zone,
                                   title character varying(255),
                                   country_id integer,
                                   theme_id integer,
                                   username character varying(50),
                                   unaccent_title character varying(100),
                                   listening_frequency bigint DEFAULT 0,
                                   duration smallint DEFAULT 0,
                                   like_count bigint DEFAULT 0,
                                   description text,
                                   create_time timestamp without time zone,
                                   update_time timestamp without time zone,
                                   status integer DEFAULT 1,
                                   sync integer DEFAULT 0
);


--
-- TOC entry 222 (class 1259 OID 14888059)
-- Name: album_artist; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.album_artist (
                                          album_id bigint NOT NULL,
                                          artist_id bigint NOT NULL,
                                          "order" smallint
);


--
-- TOC entry 223 (class 1259 OID 14888062)
-- Name: album_genre; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.album_genre (
                                         album_id bigint NOT NULL,
                                         genre_id integer NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 14888065)
-- Name: album_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.album_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4290 (class 0 OID 0)
-- Dependencies: 224
-- Name: album_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.album_id_seq OWNED BY alpha_sound.album.id;


--
-- TOC entry 225 (class 1259 OID 14888067)
-- Name: album_song; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.album_song (
                                        album_id bigint NOT NULL,
                                        song_id bigint NOT NULL,
                                        ordinal_number smallint
);


--
-- TOC entry 226 (class 1259 OID 14888070)
-- Name: album_tag; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.album_tag (
                                       album_id bigint NOT NULL,
                                       tag_id bigint NOT NULL
);


--
-- TOC entry 227 (class 1259 OID 14888073)
-- Name: artist; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.artist (
                                    id bigint NOT NULL,
                                    biography text,
                                    birth_date timestamp without time zone,
                                    name character varying(255),
                                    unaccent_name character varying(255),
                                    like_count bigint DEFAULT 0,
                                    username character varying,
                                    create_time timestamp without time zone,
                                    update_time timestamp without time zone,
                                    status integer DEFAULT 1,
                                    sync integer DEFAULT 0
);


--
-- TOC entry 228 (class 1259 OID 14888080)
-- Name: artist_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.artist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4291 (class 0 OID 0)
-- Dependencies: 228
-- Name: artist_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.artist_id_seq OWNED BY alpha_sound.artist.id;


--
-- TOC entry 229 (class 1259 OID 14888082)
-- Name: comment; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.comment (
                                     id bigint NOT NULL,
                                     content text,
                                     entity_id bigint,
                                     username character varying(50),
                                     create_time timestamp without time zone,
                                     update_time timestamp without time zone,
                                     entity_type character varying,
                                     status integer
);


--
-- TOC entry 230 (class 1259 OID 14888088)
-- Name: comment_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4292 (class 0 OID 0)
-- Dependencies: 230
-- Name: comment_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.comment_id_seq OWNED BY alpha_sound.comment.id;


--
-- TOC entry 231 (class 1259 OID 14888090)
-- Name: country; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.country (
                                     id integer NOT NULL,
                                     name character varying(255) NOT NULL,
                                     create_time timestamp without time zone,
                                     update_time timestamp without time zone,
                                     status integer DEFAULT 1
);


--
-- TOC entry 232 (class 1259 OID 14888093)
-- Name: country_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.country_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4293 (class 0 OID 0)
-- Dependencies: 232
-- Name: country_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.country_id_seq OWNED BY alpha_sound.country.id;


--
-- TOC entry 233 (class 1259 OID 14888095)
-- Name: genre; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.genre (
                                   id integer NOT NULL,
                                   name character varying(255) NOT NULL,
                                   create_time timestamp without time zone,
                                   update_time timestamp without time zone,
                                   status integer DEFAULT 1
);


--
-- TOC entry 234 (class 1259 OID 14888098)
-- Name: genre_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.genre_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4294 (class 0 OID 0)
-- Dependencies: 234
-- Name: genre_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.genre_id_seq OWNED BY alpha_sound.genre.id;


--
-- TOC entry 235 (class 1259 OID 14888105)
-- Name: playlist; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.playlist (
                                      id bigint NOT NULL,
                                      title character varying(255),
                                      username character varying(50),
                                      name character varying(255),
                                      create_time timestamp without time zone,
                                      update_time timestamp without time zone,
                                      status integer DEFAULT 1
);


--
-- TOC entry 236 (class 1259 OID 14888111)
-- Name: playlist_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.playlist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4295 (class 0 OID 0)
-- Dependencies: 236
-- Name: playlist_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.playlist_id_seq OWNED BY alpha_sound.playlist.id;


--
-- TOC entry 237 (class 1259 OID 14888113)
-- Name: playlist_song; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.playlist_song (
                                           playlist_id bigint NOT NULL,
                                           song_id bigint NOT NULL
);


--
-- TOC entry 238 (class 1259 OID 14888116)
-- Name: resource_info; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.resource_info (
                                           id bigint NOT NULL,
                                           storage_type character varying,
                                           storage_path character varying,
                                           file_name character varying,
                                           extension character varying,
                                           media_type character varying,
                                           status integer DEFAULT 1,
                                           uri character varying,
                                           folder character varying,
                                           media_id bigint,
                                           media_ref character varying,
                                           username character varying
);


--
-- TOC entry 239 (class 1259 OID 14888123)
-- Name: resource_info_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.resource_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4296 (class 0 OID 0)
-- Dependencies: 239
-- Name: resource_info_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.resource_info_id_seq OWNED BY alpha_sound.resource_info.id;


--
-- TOC entry 240 (class 1259 OID 14888125)
-- Name: song; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.song (
                                  id bigint NOT NULL,
                                  display_rating bigint DEFAULT 0,
                                  duration smallint,
                                  listening_frequency bigint DEFAULT 0,
                                  lyric text,
                                  release_date timestamp without time zone,
                                  title character varying(255),
                                  unaccent_title character varying(255),
                                  country_id integer,
                                  theme_id integer,
                                  username character varying(50),
                                  name character varying(255),
                                  album_id bigint,
                                  like_count bigint DEFAULT 0,
                                  create_time timestamp without time zone,
                                  column_16 integer,
                                  update_time timestamp without time zone,
                                  status integer DEFAULT 1,
                                  sync integer DEFAULT 0
);


--
-- TOC entry 241 (class 1259 OID 14888134)
-- Name: song_artist; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.song_artist (
                                         song_id bigint NOT NULL,
                                         artist_id bigint NOT NULL,
                                         "order" smallint
);


--
-- TOC entry 242 (class 1259 OID 14888137)
-- Name: song_genre; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.song_genre (
                                        song_id bigint NOT NULL,
                                        genre_id integer NOT NULL
);


--
-- TOC entry 243 (class 1259 OID 14888140)
-- Name: song_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.song_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4297 (class 0 OID 0)
-- Dependencies: 243
-- Name: song_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.song_id_seq OWNED BY alpha_sound.song.id;


--
-- TOC entry 244 (class 1259 OID 14888142)
-- Name: song_rating; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.song_rating (
                                         id bigint NOT NULL,
                                         rating integer,
                                         song_id bigint,
                                         user_id bigint
);


--
-- TOC entry 245 (class 1259 OID 14888145)
-- Name: song_tag; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.song_tag (
                                      song_id bigint NOT NULL,
                                      tag_id bigint NOT NULL
);


--
-- TOC entry 246 (class 1259 OID 14888148)
-- Name: tag; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.tag (
                                 id bigint NOT NULL,
                                 name character varying(255) NOT NULL,
                                 create_time timestamp without time zone,
                                 update_time timestamp without time zone,
                                 status integer DEFAULT 1
);


--
-- TOC entry 247 (class 1259 OID 14888151)
-- Name: tag_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4298 (class 0 OID 0)
-- Dependencies: 247
-- Name: tag_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.tag_id_seq OWNED BY alpha_sound.tag.id;


--
-- TOC entry 248 (class 1259 OID 14888153)
-- Name: theme; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.theme (
                                   id integer NOT NULL,
                                   name character varying(255) NOT NULL,
                                   create_time timestamp without time zone,
                                   update_time timestamp without time zone,
                                   status integer DEFAULT 1
);


--
-- TOC entry 249 (class 1259 OID 14888156)
-- Name: theme_id_seq; Type: SEQUENCE; Schema: alpha_sound; Owner: -
--

CREATE SEQUENCE alpha_sound.theme_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4299 (class 0 OID 0)
-- Dependencies: 249
-- Name: theme_id_seq; Type: SEQUENCE OWNED BY; Schema: alpha_sound; Owner: -
--

ALTER SEQUENCE alpha_sound.theme_id_seq OWNED BY alpha_sound.theme.id;


--
-- TOC entry 250 (class 1259 OID 14888158)
-- Name: user_favorites; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.user_favorites (
                                            username character varying NOT NULL,
                                            entity_id bigint NOT NULL,
                                            type character varying NOT NULL,
                                            status integer DEFAULT 1,
                                            listening_count bigint DEFAULT 0,
                                            liked boolean DEFAULT false
);


--
-- TOC entry 251 (class 1259 OID 14888176)
-- Name: user_info; Type: TABLE; Schema: alpha_sound; Owner: -
--

CREATE TABLE alpha_sound.user_info (
                                       profile character varying DEFAULT '{}'::character varying NOT NULL,
                                       username character varying(50) NOT NULL,
                                       setting character varying DEFAULT '{}'::character varying,
                                       create_time timestamp without time zone,
                                       update_time timestamp without time zone,
                                       status integer DEFAULT 1
);


--
-- TOC entry 4014 (class 2604 OID 14888467)
-- Name: album id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.album ALTER COLUMN id SET DEFAULT nextval('alpha_sound.album_id_seq'::regclass);


--
-- TOC entry 4020 (class 2604 OID 14888468)
-- Name: artist id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.artist ALTER COLUMN id SET DEFAULT nextval('alpha_sound.artist_id_seq'::regclass);


--
-- TOC entry 4024 (class 2604 OID 14888469)
-- Name: comment id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.comment ALTER COLUMN id SET DEFAULT nextval('alpha_sound.comment_id_seq'::regclass);


--
-- TOC entry 4025 (class 2604 OID 14888470)
-- Name: country id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.country ALTER COLUMN id SET DEFAULT nextval('alpha_sound.country_id_seq'::regclass);


--
-- TOC entry 4027 (class 2604 OID 14888471)
-- Name: genre id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.genre ALTER COLUMN id SET DEFAULT nextval('alpha_sound.genre_id_seq'::regclass);


--
-- TOC entry 4029 (class 2604 OID 14888473)
-- Name: playlist id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.playlist ALTER COLUMN id SET DEFAULT nextval('alpha_sound.playlist_id_seq'::regclass);


--
-- TOC entry 4031 (class 2604 OID 14888474)
-- Name: resource_info id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.resource_info ALTER COLUMN id SET DEFAULT nextval('alpha_sound.resource_info_id_seq'::regclass);


--
-- TOC entry 4033 (class 2604 OID 14888475)
-- Name: song id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.song ALTER COLUMN id SET DEFAULT nextval('alpha_sound.song_id_seq'::regclass);


--
-- TOC entry 4039 (class 2604 OID 14888476)
-- Name: tag id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.tag ALTER COLUMN id SET DEFAULT nextval('alpha_sound.tag_id_seq'::regclass);


--
-- TOC entry 4041 (class 2604 OID 14888477)
-- Name: theme id; Type: DEFAULT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.theme ALTER COLUMN id SET DEFAULT nextval('alpha_sound.theme_id_seq'::regclass);


--
-- TOC entry 4253 (class 0 OID 14888050)
-- Dependencies: 221
-- Data for Name: album; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.album VALUES (9, '1992-01-01 07:00:00', 'Hold me', NULL, NULL, 'admin', 'Hold me', 0, 0, 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.album VALUES (11, '2011-01-01 07:00:00', 'Issen mankai no Kiss', NULL, NULL, 'admin', 'Issen mankai no Kiss', 0, 0, 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.album VALUES (12, '1997-08-27 00:00:00', 'Kimi ga Inai natsu', 147, 12, 'member', 'kimi ga inai natsu', 0, 0, 0, NULL, '2021-10-09 10:14:04.14', NULL, 1, 1);
INSERT INTO alpha_sound.album VALUES (13, '2013-11-27 00:00:00', 'Butterfly Core', NULL, NULL, 'pysga1996', 'butterfly core', 0, 0, 0, NULL, '2021-10-09 10:34:04.239', NULL, 1, 1);
INSERT INTO alpha_sound.album VALUES (7, '2014-01-01 00:00:00', 'Khúc ca cho em', 1, 2, 'thanhvt', 'khuc ca cho em', 0, 0, 0, NULL, NULL, '2021-10-09 10:53:23.362', 1, 1);
INSERT INTO alpha_sound.album VALUES (10, '2018-01-01 07:00:00', 'Kimi Omou - Shunkashuto', NULL, NULL, 'admin', 'Kimi Omou - Shunkashuto', 0, 0, 1, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.album VALUES (8, '2017-01-01 07:00:00', 'Smile', 147, 2, 'admin', 'smile', 0, 0, 0, NULL, NULL, '2021-10-09 11:34:17.668', 1, 1);
INSERT INTO alpha_sound.album VALUES (14, '2014-01-01 00:00:00', 'Odoru Ponpokorin', 147, 7, 'zeronos', 'odoru ponpokorin', 0, 0, 0, NULL, '2021-10-09 10:38:59.801', '2021-10-09 11:39:12.31', 1, 1);
INSERT INTO alpha_sound.album VALUES (60, '2008-01-01 00:00:00', 'Wanbi 0901', NULL, NULL, 'thanhvt', 'wanbi 0901', 0, 0, 0, NULL, NULL, '2021-10-09 10:49:27.849', 1, 1);


--
-- TOC entry 4254 (class 0 OID 14888059)
-- Dependencies: 222
-- Data for Name: album_artist; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.album_artist VALUES (4, 17, NULL);
INSERT INTO alpha_sound.album_artist VALUES (4, 16, NULL);
INSERT INTO alpha_sound.album_artist VALUES (5, 17, NULL);
INSERT INTO alpha_sound.album_artist VALUES (5, 16, NULL);
INSERT INTO alpha_sound.album_artist VALUES (9, 2, NULL);
INSERT INTO alpha_sound.album_artist VALUES (10, 1, NULL);
INSERT INTO alpha_sound.album_artist VALUES (11, 1, NULL);
INSERT INTO alpha_sound.album_artist VALUES (12, 3, NULL);
INSERT INTO alpha_sound.album_artist VALUES (13, 10, NULL);
INSERT INTO alpha_sound.album_artist VALUES (60, 45, NULL);
INSERT INTO alpha_sound.album_artist VALUES (7, 45, NULL);
INSERT INTO alpha_sound.album_artist VALUES (8, 1, NULL);
INSERT INTO alpha_sound.album_artist VALUES (14, 9, NULL);


--
-- TOC entry 4255 (class 0 OID 14888062)
-- Dependencies: 223
-- Data for Name: album_genre; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.album_genre VALUES (12, 4);
INSERT INTO alpha_sound.album_genre VALUES (7, 4);
INSERT INTO alpha_sound.album_genre VALUES (8, 4);
INSERT INTO alpha_sound.album_genre VALUES (14, 4);


--
-- TOC entry 4257 (class 0 OID 14888067)
-- Dependencies: 225
-- Data for Name: album_song; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.album_song VALUES (10, 83, 4);
INSERT INTO alpha_sound.album_song VALUES (10, 75, 5);
INSERT INTO alpha_sound.album_song VALUES (9, 79, 5);
INSERT INTO alpha_sound.album_song VALUES (10, 72, 2);
INSERT INTO alpha_sound.album_song VALUES (11, 78, 2);
INSERT INTO alpha_sound.album_song VALUES (9, 80, 1);
INSERT INTO alpha_sound.album_song VALUES (11, 77, 1);
INSERT INTO alpha_sound.album_song VALUES (10, 73, 3);
INSERT INTO alpha_sound.album_song VALUES (9, 69, 2);
INSERT INTO alpha_sound.album_song VALUES (10, 74, 1);
INSERT INTO alpha_sound.album_song VALUES (9, 71, 3);
INSERT INTO alpha_sound.album_song VALUES (9, 70, 4);
INSERT INTO alpha_sound.album_song VALUES (12, 87, 0);
INSERT INTO alpha_sound.album_song VALUES (12, 56, 1);
INSERT INTO alpha_sound.album_song VALUES (13, 88, 0);
INSERT INTO alpha_sound.album_song VALUES (13, 90, 1);
INSERT INTO alpha_sound.album_song VALUES (13, 89, 2);
INSERT INTO alpha_sound.album_song VALUES (13, 91, 3);
INSERT INTO alpha_sound.album_song VALUES (60, 61, 0);
INSERT INTO alpha_sound.album_song VALUES (7, 10, 0);
INSERT INTO alpha_sound.album_song VALUES (7, 11, 1);
INSERT INTO alpha_sound.album_song VALUES (7, 9, 2);
INSERT INTO alpha_sound.album_song VALUES (7, 12, 3);
INSERT INTO alpha_sound.album_song VALUES (7, 8, 4);
INSERT INTO alpha_sound.album_song VALUES (8, 57, 0);
INSERT INTO alpha_sound.album_song VALUES (8, 64, 1);
INSERT INTO alpha_sound.album_song VALUES (8, 65, 2);
INSERT INTO alpha_sound.album_song VALUES (8, 68, 3);
INSERT INTO alpha_sound.album_song VALUES (8, 67, 4);
INSERT INTO alpha_sound.album_song VALUES (8, 51, 5);
INSERT INTO alpha_sound.album_song VALUES (8, 52, 6);
INSERT INTO alpha_sound.album_song VALUES (14, 92, 0);
INSERT INTO alpha_sound.album_song VALUES (14, 93, 1);


--
-- TOC entry 4258 (class 0 OID 14888070)
-- Dependencies: 226
-- Data for Name: album_tag; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.album_tag VALUES (12, 14);
INSERT INTO alpha_sound.album_tag VALUES (12, 25);
INSERT INTO alpha_sound.album_tag VALUES (7, 17);
INSERT INTO alpha_sound.album_tag VALUES (7, 36);
INSERT INTO alpha_sound.album_tag VALUES (8, 14);
INSERT INTO alpha_sound.album_tag VALUES (8, 40);
INSERT INTO alpha_sound.album_tag VALUES (14, 14);
INSERT INTO alpha_sound.album_tag VALUES (14, 47);


--
-- TOC entry 4259 (class 0 OID 14888073)
-- Dependencies: 227
-- Data for Name: artist; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.artist VALUES (9, 'E-girls (sometimes stylized as E-Girls or e-girls; stands for Exile Girls) were a Japanese collective girl group created and managed by LDH while signed to music label Rhythm Zone from Avex. As of 2017, the band consists of 11 members; 8 of which feature all current members from the groups Happiness and Flower. Additionally, three original members were scouted in Japan and added as part of the group. Created as a sister act to boy band Exile, E-girls debuted in 2011 with their single "Celebration". After a string of promotional recordings, E-girls released their debut record Lesson 1 two years later. In 2013, their single "Gomennasai no Kissing You" catapulted the girls into commercial success, selling over 100,000 units, and its parent album Colorful Pop (2014), was met with positive reviews and high performance on the Oricon Albums Chart.', '2011-01-01 00:00:00', 'E-Girl', 'e-girl', 0, 'admin', '2021-10-09 10:09:43.62', NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (10, 'VALSHE (バルシェ, Barushe, born September 15), is a Japanese female singer, signed under the Being Inc. label. She is well known as a "Ryouseirui" (両声類?, lit. "both voice types"), being able to sing both "male" and "female" voice types.[1][2][3]', '1986-09-15 00:00:00', 'VALSHE', 'valshe', 0, 'admin', '2021-10-09 10:10:30.679', NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (19, 'Lam Trường tên đầy đủ là Tiêu Lam Trường là một ca sĩ, diễn viên Việt Nam. Lam Trường thường được người hâm mộ gọi thân mật là "Anh Hai" và thường viết là "A2". Anh là một trong những ca sĩ tiên phong và có sức ảnh hưởng lớn trong việc khơi dậy dòng nhạc trẻ Việt Nam từ những năm thập niên 90.', '1974-10-14 00:00:00', 'Lam Trường', 'Lam Truong', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (6, 'Lâm Phong là một nam ca sĩ, người dẫn chương trình kiêm diễn viên truyền hình và diễn viên điện ảnh nổi tiếng Hồng Kông. Anh từng là diễn viên độc quyền của hãng TVB.', '1979-12-18 00:00:00', 'Lâm Phong', 'Lam Phong', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (8, 'Kana-Boon là một ban nhạc rock Nhật Bản được thành lập vào năm 2008. Họ đã có màn ra mắt chính với Ki / oon Music vào năm 2013. Kể từ đó, họ đã có bốn album lọt vào top 10 trên Bảng xếp hạng album Oricon hàng tuần, với Doppel là bài hát hay nhất của họ -bạc album, đạt vị trí thứ ba trên bảng xếp hạng.', '2008-01-01 00:00:00', 'Kana Boon', 'Kana Boon', 0, 'thanhvt', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (1, 'Kuraki Mai (倉木麻衣) is a pop and R&B singer-songwriter, producer and composer from Funabashi, Chiba. Kuraki debuted in 1999 with the single, Love, Day After Tomorrow. In 2000, she released her debut album, Delicious Way, which debuted at number-one and sold over 2,210,000 copies in its first week. Mai is one of a few female artists in Japan to have their first four studio albums to debut atop of the Oricon album chart.', '1982-10-28 07:00:00', 'Mai Kuraki', 'Mai Kuraki', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (4, 'Daichi Miura (三浦 大知, Miura Daichi, born 24 August 1987) is a Japanese singer, songwriter, dancer, and choreographer. He also directs his own concerts. He belongs to Rising Production and has a record contract with Avex. He is from Okinawa Prefecture, Japan. He has an official fan club called "Daichishiki"(大知識).', '1987-08-24 07:00:00', 'Daichi Miura', 'Daichi Miura', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (7, 'Tiêu Chính Nam (蕭正楠) có tên tiếng Anh là Edwin Siu (sinh ngày 23 tháng 03 năm 1977 tại Hồng Kông thuộc Anh) là một nam diễn viên truyền hình-diễn viên điện ảnh, người dẫn chương trình kiêm ca sĩ nổi tiếng người Hồng Kông. Anh hiện đang là diễn viên độc quyền của hãng TVB.', '1977-03-23 00:00:00', 'Tiêu Chính Nam', 'Tieu Chinh Nam', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (5, 'U-ka saegusa IN db (三枝夕夏 IN db（イン デシベル）, Saegusa Yūka in Deshiberu (in Decibel)) was a Japanese pop rock band formed in Osaka in 2002. The band consists of lead vocalist U-ka Saegusa, lead guitarist Yūichirō Iwai, lead bassist Taku Ōyabu, and drummer Keisuke Kurumatani. Signed to Giza Studio, the band released four studio albums before disbanding in 2010. They''re known for performing the several theme songs for the Japanese anime series, Case Closed. One of the songs, "Kimi to Yakusoku Shita Yasashii Ano Basho made", became the band''s best-selling single, selling approximately 34,000 copies nationwide.', '1980-06-09 07:00:00', 'U-ka Saegusa in dB', 'U-ka Saegusa in dB', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (17, 'Thu Minh là một nữ ca sĩ của Việt Nam, từng được đề cử 7 lần tại giải Cống hiến, giành 1 giải Mnet Asian Music Awards. Cô sở hữu quãng giọng rộng và chất giọng nữ cao nhẹ nhàng.', '1977-09-22 00:00:00', 'Thu Minh', 'Thu Minh', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (16, 'Mr. Siro, tên thật là Vương Quốc Tuân, sinh 18/11/1982 tại Thành phố Hồ Chí Minh, là một nhạc sĩ viết các bài hát được giới trẻ biết đến. Ngoài khả năng sáng tác nhạc, anh còn có khả năng hát các tác phẩm của chính mình.', '1982-11-18 00:00:00', 'Mr. Siro', 'Mr. Siro', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (21, 'Thanh Thảo có tên thật là Phạm Trịnh Phương Thảo là một nữ ca sĩ nổi tiếng người Việt Nam được khán giả biết đến với biệt danh là "Búp bê". Thanh Thảo thuộc cung Song Ngư, cầm tinh con rắn.', '1977-03-08 00:00:00', 'Thanh Thảo', 'Thanh Thao', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (20, 'Nguyễn Hải Phong là một nhạc sĩ, ca sĩ và nhà sản xuất âm nhạc nổi tiếng của Việt Nam. Anh từng giành được 2 đề cử tại giải Cống hiến. Anh tốt nghiệp Trường Đại học Văn hóa thành phố Hồ Chí Minh và hiện đang định cư tại Thành phố Hồ Chí Minh.', '1982-05-23 00:00:00', 'Nguyễn Hải Phong', 'Nguyen Hai Phong', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (3, 'Deen (ディーン, Dīn) is a Japanese popular music band that formed in 1992. Members frequently changed until the release of the first album, and from there Deen has had four members: vocalist and lyricist Shuichi Ikemori, keyboardist and leader Koji Yamane, guitarist Shinji Tagawa and drummer Naoki Uzumoto. In January 2000, Utsumoto and in March 2018, Tagawa left the group. The band has sold over 15 million compact discs.[1]', '1992-01-01 07:00:00', 'DEEN', 'DEEN', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (2, 'Zard (ザード Zādo?) là một nhóm nhạc pop rock của Nhật Bản. Ban đầu là một nhóm gồm năm thành viên, với giọng ca chính Izumi Sakai là thành viên cố định duy nhất của nhóm. Tác phẩm của Zard đã được bán dưới nhãn hiệu thu âm B-Gram Records, Inc. Các bài hát nổi tiếng và thành công nhất của họ là "Makenaide" (1993) (負けないで), "Yureru Omoi" (1993) (揺れる想い) và "My Friend " (1996). Tính đến năm 2014, Zard đã bán được hơn 37 triệu đĩa, trở thành một trong những nghệ sĩ âm nhạc bán chạy nhất tại Nhật Bản.', '1967-02-06 07:00:00', 'ZARD', 'ZARD', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (15, 'Minh Quân tên đầy đủ là Trịnh Minh Quân. Từ nhỏ Minh Quân đã làm quen với âm nhạc, lúc 5 tuổi tham gia ca hát ở Cung Thiếu nhi Hà Nội. Đến năm lớp 11 để thoả niềm đam mê âm nhạc của mình anh thi vào Nhạc viện Hà Nội.', '1980-07-18 16:33:09', 'Minh Quân', 'Minh Quan', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (159, 'Trần Thu Hà, còn có nghệ danh Hà Trần, là một ca sĩ và nhà sản xuất âm nhạc nổi tiếng của Việt Nam, cô là nghệ sĩ đã giành được 9 đề cử, xuất hiện trong danh sách những người được đề cử nhiều nhất và chiến thắng ở 4 hạng mục tại giải Cống hiến.', '1977-08-26 00:00:00', 'Trần Thu Hà', 'Tran Thu Ha', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (18, 'Thủy Tiên có tên đầy đủ là Trần Thị Thủy Tiên là một nữ ca sĩ, nhạc sĩ, người mẫu, người mẫu ảnh kiêm diễn viên điện ảnh-diễn viên truyền hình nổi tiếng người Việt Nam. Cô là vợ của cầu thủ bóng đá Lê Công Vinh.', '1985-11-25 00:00:00', 'Thủy Tiên', 'Thuy Tien', 0, NULL, NULL, NULL, 1, 1);
INSERT INTO alpha_sound.artist VALUES (45, 'Nguyễn Tuấn Anh, hay được biết đến với nghệ danh WanBi Tuấn Anh, là một nam ca sĩ người Việt Nam. Anh được biết đến khi cùng Thu Thủy giành giải "Ca sĩ triển vọng" của giải Làn Sóng Xanh. Các ca khúc của Wanbi chủ yếu thuộc thể loại pop, R&B, ...', '1987-01-09 00:00:00', 'Wanbi Tuấn Anh', 'Wanbi Tuan Anh', 0, NULL, NULL, NULL, 1, 1);


--
-- TOC entry 4261 (class 0 OID 14888082)
-- Dependencies: 229
-- Data for Name: comment; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.comment VALUES (63, 'co cl y', 10, 'thanhvt', NULL, NULL, NULL, NULL);
INSERT INTO alpha_sound.comment VALUES (33, 'abc', 24, 'thanhvt', NULL, NULL, NULL, NULL);
INSERT INTO alpha_sound.comment VALUES (41, 'hihihi', 23, 'thanhvt', NULL, NULL, NULL, NULL);
INSERT INTO alpha_sound.comment VALUES (151, 'vcl', 8, 'thanhvt', NULL, NULL, NULL, NULL);
INSERT INTO alpha_sound.comment VALUES (1, 'hay do', 4, 'thanhvt', '2021-09-26 12:12:30.833', NULL, 'ARTIST', 1);
INSERT INTO alpha_sound.comment VALUES (2, 'album yeu thich', 10, 'thanhvt', '2021-09-26 12:17:58.638', NULL, 'ALBUM', 1);
INSERT INTO alpha_sound.comment VALUES (3, '+1 like', 10, 'admin', '2021-09-26 20:07:52.957', NULL, 'ALBUM', 1);
INSERT INTO alpha_sound.comment VALUES (4, 'gjhg', 94, 'admin', '2021-11-12 06:47:23.175', NULL, 'SONG', 1);


--
-- TOC entry 4263 (class 0 OID 14888090)
-- Dependencies: 231
-- Data for Name: country; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.country VALUES (2, 'China', NULL, NULL, 1);
INSERT INTO alpha_sound.country VALUES (147, 'Japan', NULL, NULL, 1);
INSERT INTO alpha_sound.country VALUES (4, 'US-Uk', NULL, NULL, 1);
INSERT INTO alpha_sound.country VALUES (1, 'Vietnam', NULL, NULL, 1);
INSERT INTO alpha_sound.country VALUES (3, 'Hong Kong', '2021-09-20 18:00:17.034', NULL, 1);


--
-- TOC entry 4265 (class 0 OID 14888095)
-- Dependencies: 233
-- Data for Name: genre; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.genre VALUES (3, 'Jazz', '2021-09-20 17:58:31.745', NULL, 1);
INSERT INTO alpha_sound.genre VALUES (4, 'Pop', '2021-09-20 17:58:40.265', NULL, 1);
INSERT INTO alpha_sound.genre VALUES (5, 'Piano', '2021-09-20 17:58:49.574', NULL, 1);
INSERT INTO alpha_sound.genre VALUES (6, 'Instrumental', '2021-09-20 17:59:10.21', NULL, 1);
INSERT INTO alpha_sound.genre VALUES (1, 'Saxophone', '2021-09-20 01:04:48.332', '2021-09-20 17:59:21.492', 1);
INSERT INTO alpha_sound.genre VALUES (2, 'Rock', '2021-09-20 01:29:54.632', '2021-09-20 17:59:30.982', 1);
INSERT INTO alpha_sound.genre VALUES (7, 'Metal', '2021-09-20 17:59:35.182', NULL, 1);
INSERT INTO alpha_sound.genre VALUES (8, 'Edm', '2021-09-20 17:59:38.168', NULL, 1);
INSERT INTO alpha_sound.genre VALUES (9, 'Flute', '2021-09-20 17:59:46.558', NULL, 1);


--
-- TOC entry 4267 (class 0 OID 14888105)
-- Dependencies: 235
-- Data for Name: playlist; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.playlist VALUES (156, 'demo 4', 'thanhvt', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (20, 'Demo playlist 6', 'thanhvt', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (18, 'Demo playlis1', 'thanhvt', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (2, 'xyz', 'thanhvt', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (1, 'abc', 'thanhvt', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (150, 'Playlist 01', 'thanhvt', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (21, 'playlist', 'thanhvt', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (180, 'Demo playlist 1', 'thanhvt', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (3, 'Test', 'admin', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (4, 'TEst 2', 'admin', NULL, NULL, NULL, 1);
INSERT INTO alpha_sound.playlist VALUES (5, 'Test 3', 'admin', NULL, NULL, NULL, 1);


--
-- TOC entry 4269 (class 0 OID 14888113)
-- Dependencies: 237
-- Data for Name: playlist_song; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.playlist_song VALUES (20, 8);
INSERT INTO alpha_sound.playlist_song VALUES (18, 8);
INSERT INTO alpha_sound.playlist_song VALUES (150, 9);
INSERT INTO alpha_sound.playlist_song VALUES (156, 9);
INSERT INTO alpha_sound.playlist_song VALUES (156, 25);
INSERT INTO alpha_sound.playlist_song VALUES (156, 23);
INSERT INTO alpha_sound.playlist_song VALUES (156, 12);
INSERT INTO alpha_sound.playlist_song VALUES (150, 12);
INSERT INTO alpha_sound.playlist_song VALUES (21, 8);
INSERT INTO alpha_sound.playlist_song VALUES (2, 14);
INSERT INTO alpha_sound.playlist_song VALUES (4, 34);
INSERT INTO alpha_sound.playlist_song VALUES (5, 19);
INSERT INTO alpha_sound.playlist_song VALUES (5, 34);


--
-- TOC entry 4270 (class 0 OID 14888116)
-- Dependencies: 238
-- Data for Name: resource_info; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.resource_info VALUES (4, 'FIREBASE', 'avatar/artist_avatar_-_5_-_u-ka_saegusa_in_db', 'artist_avatar_-_5_-_u-ka_saegusa_in_db', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_5_-_u-ka_saegusa_in_db?generation=1630249052631134&alt=media', 'avatar', 5, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (5, 'FIREBASE', 'audio/song_audio_-_43_-_43_-_test_01_-_lam_tr__ng_.m4a', 'song_audio_-_43_-_43_-_test_01_-_lam_tr__ng_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_43_-_43_-_test_01_-_lam_tr__ng_.m4a?generation=1630663680822971&alt=media', 'audio', 43, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (2, 'FIREBASE', 'audio/song_audio_-_35_-_35_-_test_06_-_th_y_ti_n_.m4a', 'song_audio_-_35_-_35_-_test_06_-_th_y_ti_n_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_35_-_35_-_test_06_-_th_y_ti_n_.m4a?generation=1630663841052035&alt=media', 'audio', 35, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (1, 'FIREBASE', 'audio/song_audio_-_34_-_34_-_serendipity_-_mai_kuraki_.mp3', 'song_audio_-_34_-_34_-_serendipity_-_mai_kuraki_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_34_-_34_-_serendipity_-_mai_kuraki_.mp3?generation=1630425157429568&alt=media', 'audio', 34, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (15, 'FIREBASE', 'avatar/lam_truong', 'lam_truong', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Flam_truong?generation=1630238632741723&alt=media', 'avatar', 19, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (16, 'FIREBASE', 'avatar/artist_avatar_-_15_-_minh_quan', 'artist_avatar_-_15_-_minh_quan', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_15_-_minh_quan?generation=1630238981899956&alt=media', 'avatar', 15, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (17, 'FIREBASE', 'avatar/artist_avatar_-_16_-_mr._siro', 'artist_avatar_-_16_-_mr._siro', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_16_-_mr._siro?generation=1630239006321039&alt=media', 'avatar', 16, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (18, 'FIREBASE', 'avatar/artist_avatar_-_20_-_nguyen_hai_phong', 'artist_avatar_-_20_-_nguyen_hai_phong', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_20_-_nguyen_hai_phong?generation=1630239019595782&alt=media', 'avatar', 20, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (19, 'FIREBASE', 'avatar/artist_avatar_-_21_-_thanh_thao', 'artist_avatar_-_21_-_thanh_thao', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_21_-_thanh_thao?generation=1630239033378242&alt=media', 'avatar', 21, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (20, 'FIREBASE', 'avatar/artist_avatar_-_17_-_thu_minh', 'artist_avatar_-_17_-_thu_minh', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_17_-_thu_minh?generation=1630239045410893&alt=media', 'avatar', 17, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (21, 'FIREBASE', 'avatar/artist_avatar_-_18_-_thuy_tien', 'artist_avatar_-_18_-_thuy_tien', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_18_-_thuy_tien?generation=1630239059731009&alt=media', 'avatar', 18, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (22, 'FIREBASE', 'avatar/artist_avatar_-_159_-_tran_thu_ha', 'artist_avatar_-_159_-_tran_thu_ha', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_159_-_tran_thu_ha?generation=1630239089123676&alt=media', 'avatar', 159, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (23, 'FIREBASE', 'avatar/artist_avatar_-_1_-_mai_kuraki', 'artist_avatar_-_1_-_mai_kuraki', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_1_-_mai_kuraki?generation=1630244803731643&alt=media', 'avatar', 1, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (24, 'FIREBASE', 'avatar/artist_avatar_-_2_-_zard', 'artist_avatar_-_2_-_zard', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_2_-_zard?generation=1630245533900056&alt=media', 'avatar', 2, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (25, 'FIREBASE', 'avatar/artist_avatar_-_3_-_deen', 'artist_avatar_-_3_-_deen', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_3_-_deen?generation=1630247612680032&alt=media', 'avatar', 3, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (3, 'FIREBASE', 'avatar/artist_avatar_-_4_-_daichi_miura', 'artist_avatar_-_4_-_daichi_miura', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_4_-_daichi_miura?generation=1630248875123732&alt=media', 'avatar', 4, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (27, 'FIREBASE', 'audio/song_audio_-_44_-_44_-_life_is_beautiful_-_daichi_miura_.m4a', 'song_audio_-_44_-_44_-_life_is_beautiful_-_daichi_miura_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_44_-_44_-_life_is_beautiful_-_daichi_miura_.m4a?generation=1630664778353338&alt=media', 'audio', 44, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (26, 'FIREBASE', 'audio/song_audio_-_45_-_45_-_eien_-_zard_.mp3', 'song_audio_-_45_-_45_-_eien_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_45_-_45_-_eien_-_zard_.mp3?generation=1630665051989211&alt=media', 'audio', 45, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (28, 'FIREBASE', 'audio/song_audio_-_46_-_46_-_good_day_-_zard_.mp3', 'song_audio_-_46_-_46_-_good_day_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_46_-_46_-_good_day_-_zard_.mp3?generation=1630665287063581&alt=media', 'audio', 46, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (29, 'FIREBASE', 'audio/song_audio_-_47_-_47_-_today_is_another_day_-_zard_.mp3', 'song_audio_-_47_-_47_-_today_is_another_day_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_47_-_47_-_today_is_another_day_-_zard_.mp3?generation=1630665564866732&alt=media', 'audio', 47, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (30, 'FIREBASE', 'audio/song_audio_-_48_-_48_-_my_friend_-_zard_.mp3', 'song_audio_-_48_-_48_-_my_friend_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_48_-_48_-_my_friend_-_zard_.mp3?generation=1630666077651855&alt=media', 'audio', 48, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (31, 'FIREBASE', 'audio/song_audio_-_49_-_49_-_don_t_you_see_-_zard_.mp3', 'song_audio_-_49_-_49_-_don_t_you_see_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_49_-_49_-_don_t_you_see_-_zard_.mp3?generation=1630668622778322&alt=media', 'audio', 49, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (32, 'FIREBASE', 'audio/song_audio_-_50_-_50_-_i_shall_be_released_-_u-ka_saegusa_in_db_.mp3', 'song_audio_-_50_-_50_-_i_shall_be_released_-_u-ka_saegusa_in_db_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_50_-_50_-_i_shall_be_released_-_u-ka_saegusa_in_db_.mp3?generation=1630674598293674&alt=media', 'audio', 50, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (33, 'FIREBASE', 'cover/album_cover_-_8_-_8_-_smile_-_mai_kuraki_.jpg', 'album_cover_-_8_-_8_-_smile_-_mai_kuraki_.jpg', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/cover%2Falbum_cover_-_8_-_8_-_smile_-_mai_kuraki_.jpg?generation=1630683394089955&alt=media', 'cover', 8, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (35, 'FIREBASE', 'audio/song_audio_-_54_-_54_-_tell_me_why_-_mai_kuraki_.m4a', 'song_audio_-_54_-_54_-_tell_me_why_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_54_-_54_-_tell_me_why_-_mai_kuraki_.m4a?generation=1630683400298539&alt=media', 'audio', 51, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (34, 'FIREBASE', 'audio/song_audio_-_53_-_53_-_yesterday_love_-_mai_kuraki_.m4a', 'song_audio_-_53_-_53_-_yesterday_love_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_53_-_53_-_yesterday_love_-_mai_kuraki_.m4a?generation=1630683399719903&alt=media', 'audio', 52, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (36, 'FIREBASE', 'audio/song_audio_-_55_-_55_-_n_u_ph_i_xa_nhau_-_minh_qu_n_.mp3', 'song_audio_-_55_-_55_-_n_u_ph_i_xa_nhau_-_minh_qu_n_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_55_-_55_-_n_u_ph_i_xa_nhau_-_minh_qu_n_.mp3?generation=1630689097221896&alt=media', 'audio', 55, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (37, 'FIREBASE', 'audio/song_audio_-_56_-_56_-_love_me_-_deen_.mp3', 'song_audio_-_56_-_56_-_love_me_-_deen_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_56_-_56_-_love_me_-_deen_.mp3?generation=1630689480110783&alt=media', 'audio', 56, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (39, 'FIREBASE', 'audio/song_audio_-_64_-_64_-_my_way_-_mai_kuraki_.m4a', 'song_audio_-_64_-_64_-_my_way_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_64_-_64_-_my_way_-_mai_kuraki_.m4a?generation=1630693109572772&alt=media', 'audio', 64, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (38, 'FIREBASE', 'audio/song_audio_-_59_-_59_-_kimi_e_no_uta_-_mai_kuraki_.m4a', 'song_audio_-_59_-_59_-_kimi_e_no_uta_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_59_-_59_-_kimi_e_no_uta_-_mai_kuraki_.m4a?generation=1630692788373591&alt=media', 'audio', 57, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (40, 'FIREBASE', 'audio/song_audio_-_65_-_65_-_open_love_-_mai_kuraki_.m4a', 'song_audio_-_65_-_65_-_open_love_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_65_-_65_-_open_love_-_mai_kuraki_.m4a?generation=1630695175980243&alt=media', 'audio', 65, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (41, 'FIREBASE', 'audio/song_audio_-_66_-_66_-_i_like_it_-_mai_kuraki_.m4a', 'song_audio_-_66_-_66_-_i_like_it_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_66_-_66_-_i_like_it_-_mai_kuraki_.m4a?generation=1630695373619261&alt=media', 'audio', 66, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (42, 'FIREBASE', 'audio/song_audio_-_67_-_67_-_sawage_life_-_mai_kuraki_.m4a', 'song_audio_-_67_-_67_-_sawage_life_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_67_-_67_-_sawage_life_-_mai_kuraki_.m4a?generation=1630695393229582&alt=media', 'audio', 67, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (43, 'FIREBASE', 'audio/song_audio_-_68_-_68_-_mystery_hero_-_mai_kuraki_.m4a', 'song_audio_-_68_-_68_-_mystery_hero_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_68_-_68_-_mystery_hero_-_mai_kuraki_.m4a?generation=1630696323124306&alt=media', 'audio', 68, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (44, 'FIREBASE', 'cover/album_cover_-_9_-_9_-_hold_me_-_zard_.jpg', 'album_cover_-_9_-_9_-_hold_me_-_zard_.jpg', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/cover%2Falbum_cover_-_9_-_9_-_hold_me_-_zard_.jpg?generation=1630696481181062&alt=media', 'cover', 9, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (45, 'FIREBASE', 'audio/song_audio_-_69_-_69_-_ano_hohoemi_wo_wasurenai_de_-_zard_.mp3', 'song_audio_-_69_-_69_-_ano_hohoemi_wo_wasurenai_de_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_69_-_69_-_ano_hohoemi_wo_wasurenai_de_-_zard_.mp3?generation=1630696485035372&alt=media', 'audio', 69, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (46, 'FIREBASE', 'audio/song_audio_-_70_-_70_-_ai_wa_nemutteru_-_zard_.mp3', 'song_audio_-_70_-_70_-_ai_wa_nemutteru_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_70_-_70_-_ai_wa_nemutteru_-_zard_.mp3?generation=1630696604436149&alt=media', 'audio', 70, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (47, 'FIREBASE', 'audio/song_audio_-_71_-_71_-_konna_ni_aishite_mo_-_zard_.mp3', 'song_audio_-_71_-_71_-_konna_ni_aishite_mo_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_71_-_71_-_konna_ni_aishite_mo_-_zard_.mp3?generation=1630696605597976&alt=media', 'audio', 71, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (48, 'FIREBASE', 'avatar/artist_avatar_-_45_-_wanbi_tuan_anh', 'artist_avatar_-_45_-_wanbi_tuan_anh', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/avatar%2Fartist_avatar_-_45_-_wanbi_tuan_anh?generation=1630697356101982&alt=media', 'avatar', 45, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (49, 'FIREBASE', 'cover/album_cover_-_10_-_10_-_kimi_omou_-_shunkashuto_-_mai_kuraki_.jpg', 'album_cover_-_10_-_10_-_kimi_omou_-_shunkashuto_-_mai_kuraki_.jpg', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/cover%2Falbum_cover_-_10_-_10_-_kimi_omou_-_shunkashuto_-_mai_kuraki_.jpg?generation=1630698299667119&alt=media', 'cover', 10, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (50, 'FIREBASE', 'audio/song_audio_-_72_-_72_-_light_up_my_life_-_mai_kuraki_.m4a', 'song_audio_-_72_-_72_-_light_up_my_life_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_72_-_72_-_light_up_my_life_-_mai_kuraki_.m4a?generation=1630698302779344&alt=media', 'audio', 72, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (51, 'FIREBASE', 'audio/song_audio_-_73_-_73_-_togetsukyou__-kun_sou_fu___-_mai_kuraki_.m4a', 'song_audio_-_73_-_73_-_togetsukyou__-kun_sou_fu___-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_73_-_73_-_togetsukyou__-kun_sou_fu___-_mai_kuraki_.m4a?generation=1630698303878443&alt=media', 'audio', 73, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (52, 'FIREBASE', 'audio/song_audio_-_74_-_74_-_hanakotoba_-_mai_kuraki_.m4a', 'song_audio_-_74_-_74_-_hanakotoba_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_74_-_74_-_hanakotoba_-_mai_kuraki_.m4a?generation=1630698304139975&alt=media', 'audio', 74, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (77, 'CLOUDINARY', 'avatar/artist_avatar_-_2_-_zard', 'artist_avatar_-_2_-_zard.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833559/avatar/artist_avatar_-_2_-_zard.jpg', 'avatar', 2, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (53, 'FIREBASE', 'audio/song_audio_-_75_-_75_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a', 'song_audio_-_75_-_75_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_75_-_75_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a?generation=1630702744216570&alt=media', 'audio', 75, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (54, 'FIREBASE', 'audio/song_audio_-_76_-_76_-_koyoi_wa_yume_o_mi_sasete_-_mai_kuraki_.m4a', 'song_audio_-_76_-_76_-_koyoi_wa_yume_o_mi_sasete_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_76_-_76_-_koyoi_wa_yume_o_mi_sasete_-_mai_kuraki_.m4a?generation=1630703019889903&alt=media', 'audio', 76, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (55, 'FIREBASE', 'cover/album_cover_-_11_-_11_-_issen_mankai_no_kiss_-_mai_kuraki_.jpg', 'album_cover_-_11_-_11_-_issen_mankai_no_kiss_-_mai_kuraki_.jpg', 'jpg', 'IMAGE', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/cover%2Falbum_cover_-_11_-_11_-_issen_mankai_no_kiss_-_mai_kuraki_.jpg?generation=1630703371667940&alt=media', 'cover', 11, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (56, 'FIREBASE', 'audio/song_audio_-_77_-_77_-_issen_mankai_no_kiss_-_mai_kuraki_.mp3', 'song_audio_-_77_-_77_-_issen_mankai_no_kiss_-_mai_kuraki_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_77_-_77_-_issen_mankai_no_kiss_-_mai_kuraki_.mp3?generation=1630703376986745&alt=media', 'audio', 77, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (57, 'FIREBASE', 'audio/song_audio_-_78_-_78_-_sayonara_wa_mada_iwanai_de_-_mai_kuraki_.mp3', 'song_audio_-_78_-_78_-_sayonara_wa_mada_iwanai_de_-_mai_kuraki_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_78_-_78_-_sayonara_wa_mada_iwanai_de_-_mai_kuraki_.mp3?generation=1630703726300733&alt=media', 'audio', 78, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (58, 'FIREBASE', 'audio/song_audio_-_79_-_79_-_so_together_-_mai_kuraki_.mp3', 'song_audio_-_79_-_79_-_so_together_-_mai_kuraki_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_79_-_79_-_so_together_-_mai_kuraki_.mp3?generation=1630704386077041&alt=media', 'audio', 79, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (59, 'FIREBASE', 'audio/song_audio_-_80_-_80_-_dangerous_tonight_-_mai_kuraki_.mp3', 'song_audio_-_80_-_80_-_dangerous_tonight_-_mai_kuraki_.mp3', 'mp3', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_80_-_80_-_dangerous_tonight_-_mai_kuraki_.mp3?generation=1630704830542001&alt=media', 'audio', 80, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (60, 'FIREBASE', 'audio/song_audio_-_81_-_81_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a', 'song_audio_-_81_-_81_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_81_-_81_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a?generation=1630747678300541&alt=media', 'audio', 81, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (61, 'FIREBASE', 'audio/song_audio_-_82_-_82_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a', 'song_audio_-_82_-_82_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_82_-_82_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a?generation=1630747976009604&alt=media', 'audio', 82, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (62, 'FIREBASE', 'audio/song_audio_-_83_-_83_-_we_are_happy_women_-_mai_kuraki_.m4a', 'song_audio_-_83_-_83_-_we_are_happy_women_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://storage.googleapis.com/download/storage/v1/b/climax-sound.appspot.com/o/audio%2Fsong_audio_-_83_-_83_-_we_are_happy_women_-_mai_kuraki_.m4a?generation=1630747976558315&alt=media', 'audio', 83, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (63, 'CLOUDINARY', 'audio/song_audio_-_43_-_43_-_do_it__-_mai_kuraki_', 'song_audio_-_43_-_43_-_do_it__-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630832564/audio/song_audio_-_43_-_43_-_do_it__-_mai_kuraki_.m4a', 'audio', 43, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (64, 'CLOUDINARY', 'avatar/artist_avatar_-_4_-_daichi_miura', 'artist_avatar_-_4_-_daichi_miura.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833169/avatar/artist_avatar_-_4_-_daichi_miura.jpg', 'avatar', 4, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (65, 'CLOUDINARY', 'avatar/artist_avatar_-_1_-_mai_kuraki', 'artist_avatar_-_1_-_mai_kuraki.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833425/avatar/artist_avatar_-_1_-_mai_kuraki.jpg', 'avatar', 1, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (66, 'CLOUDINARY', 'avatar/artist_avatar_-_3_-_deen', 'artist_avatar_-_3_-_deen.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833436/avatar/artist_avatar_-_3_-_deen.jpg', 'avatar', 3, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (67, 'CLOUDINARY', 'avatar/artist_avatar_-_19_-_lam_truong', 'artist_avatar_-_19_-_lam_truong.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833445/avatar/artist_avatar_-_19_-_lam_truong.jpg', 'avatar', 19, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (68, 'CLOUDINARY', 'avatar/artist_avatar_-_15_-_minh_quan', 'artist_avatar_-_15_-_minh_quan.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833456/avatar/artist_avatar_-_15_-_minh_quan.jpg', 'avatar', 15, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (69, 'CLOUDINARY', 'avatar/artist_avatar_-_16_-_mr._siro', 'artist_avatar_-_16_-_mr._siro.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833466/avatar/artist_avatar_-_16_-_mr._siro.jpg', 'avatar', 16, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (70, 'CLOUDINARY', 'avatar/artist_avatar_-_20_-_nguyen_hai_phong', 'artist_avatar_-_20_-_nguyen_hai_phong.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833477/avatar/artist_avatar_-_20_-_nguyen_hai_phong.jpg', 'avatar', 20, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (71, 'CLOUDINARY', 'avatar/artist_avatar_-_21_-_thanh_thao', 'artist_avatar_-_21_-_thanh_thao.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833488/avatar/artist_avatar_-_21_-_thanh_thao.jpg', 'avatar', 21, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (72, 'CLOUDINARY', 'avatar/artist_avatar_-_17_-_thu_minh', 'artist_avatar_-_17_-_thu_minh.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833501/avatar/artist_avatar_-_17_-_thu_minh.jpg', 'avatar', 17, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (73, 'CLOUDINARY', 'avatar/artist_avatar_-_18_-_thuy_tien', 'artist_avatar_-_18_-_thuy_tien.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833512/avatar/artist_avatar_-_18_-_thuy_tien.jpg', 'avatar', 18, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (74, 'CLOUDINARY', 'avatar/artist_avatar_-_159_-_tran_thu_ha', 'artist_avatar_-_159_-_tran_thu_ha.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833524/avatar/artist_avatar_-_159_-_tran_thu_ha.jpg', 'avatar', 159, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (75, 'CLOUDINARY', 'avatar/artist_avatar_-_5_-_u-ka_saegusa_in_db', 'artist_avatar_-_5_-_u-ka_saegusa_in_db.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833534/avatar/artist_avatar_-_5_-_u-ka_saegusa_in_db.jpg', 'avatar', 5, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (76, 'CLOUDINARY', 'avatar/artist_avatar_-_45_-_wanbi_tuan_anh', 'artist_avatar_-_45_-_wanbi_tuan_anh.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833543/avatar/artist_avatar_-_45_-_wanbi_tuan_anh.jpg', 'avatar', 45, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (78, 'CLOUDINARY', 'cover/album_cover_-_9_-_9_-_hold_me_-_zard_', 'album_cover_-_9_-_9_-_hold_me_-_zard_.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833640/cover/album_cover_-_9_-_9_-_hold_me_-_zard_.jpg', 'cover', 9, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (79, 'CLOUDINARY', 'audio/song_audio_-_70_-_70_-_ai_wa_nemutteru_-_zard_', 'song_audio_-_70_-_70_-_ai_wa_nemutteru_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833646/audio/song_audio_-_70_-_70_-_ai_wa_nemutteru_-_zard_.mp3', 'audio', 70, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (80, 'CLOUDINARY', 'audio/song_audio_-_79_-_79_-_so_together_-_mai_kuraki_', 'song_audio_-_79_-_79_-_so_together_-_mai_kuraki_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833648/audio/song_audio_-_79_-_79_-_so_together_-_mai_kuraki_.mp3', 'audio', 79, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (81, 'CLOUDINARY', 'audio/song_audio_-_80_-_80_-_dangerous_tonight_-_mai_kuraki_', 'song_audio_-_80_-_80_-_dangerous_tonight_-_mai_kuraki_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833651/audio/song_audio_-_80_-_80_-_dangerous_tonight_-_mai_kuraki_.mp3', 'audio', 80, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (82, 'CLOUDINARY', 'audio/song_audio_-_69_-_69_-_ano_hohoemi_wo_wasurenai_de_-_zard_', 'song_audio_-_69_-_69_-_ano_hohoemi_wo_wasurenai_de_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833651/audio/song_audio_-_69_-_69_-_ano_hohoemi_wo_wasurenai_de_-_zard_.mp3', 'audio', 69, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (83, 'CLOUDINARY', 'audio/song_audio_-_71_-_71_-_konna_ni_aishite_mo_-_zard_', 'song_audio_-_71_-_71_-_konna_ni_aishite_mo_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833654/audio/song_audio_-_71_-_71_-_konna_ni_aishite_mo_-_zard_.mp3', 'audio', 71, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (84, 'CLOUDINARY', 'cover/album_cover_-_10_-_10_-_kimi_omou_-_shunkashuto_-_mai_kuraki_', 'album_cover_-_10_-_10_-_kimi_omou_-_shunkashuto_-_mai_kuraki_.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833804/cover/album_cover_-_10_-_10_-_kimi_omou_-_shunkashuto_-_mai_kuraki_.jpg', 'cover', 10, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (85, 'CLOUDINARY', 'audio/song_audio_-_72_-_72_-_light_up_my_life_-_mai_kuraki_', 'song_audio_-_72_-_72_-_light_up_my_life_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833809/audio/song_audio_-_72_-_72_-_light_up_my_life_-_mai_kuraki_.m4a', 'audio', 72, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (86, 'CLOUDINARY', 'audio/song_audio_-_75_-_75_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_', 'song_audio_-_75_-_75_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833809/audio/song_audio_-_75_-_75_-_makkana_kasa___kyouto_no_ame___-_mai_kuraki_.m4a', 'audio', 75, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (87, 'CLOUDINARY', 'audio/song_audio_-_73_-_73_-_togetsukyou__-kun_sou_fu___-_mai_kuraki_', 'song_audio_-_73_-_73_-_togetsukyou__-kun_sou_fu___-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833813/audio/song_audio_-_73_-_73_-_togetsukyou__-kun_sou_fu___-_mai_kuraki_.m4a', 'audio', 73, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (88, 'CLOUDINARY', 'audio/song_audio_-_83_-_83_-_we_are_happy_women_-_mai_kuraki_', 'song_audio_-_83_-_83_-_we_are_happy_women_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833815/audio/song_audio_-_83_-_83_-_we_are_happy_women_-_mai_kuraki_.m4a', 'audio', 83, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (89, 'CLOUDINARY', 'audio/song_audio_-_74_-_74_-_hanakotoba_-_mai_kuraki_', 'song_audio_-_74_-_74_-_hanakotoba_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833819/audio/song_audio_-_74_-_74_-_hanakotoba_-_mai_kuraki_.m4a', 'audio', 74, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (90, 'CLOUDINARY', 'cover/album_cover_-_11_-_11_-_issen_mankai_no_kiss_-_mai_kuraki_', 'album_cover_-_11_-_11_-_issen_mankai_no_kiss_-_mai_kuraki_.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630833887/cover/album_cover_-_11_-_11_-_issen_mankai_no_kiss_-_mai_kuraki_.jpg', 'cover', 11, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (91, 'CLOUDINARY', 'audio/song_audio_-_78_-_78_-_sayonara_wa_mada_iwanai_de_-_mai_kuraki_', 'song_audio_-_78_-_78_-_sayonara_wa_mada_iwanai_de_-_mai_kuraki_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833893/audio/song_audio_-_78_-_78_-_sayonara_wa_mada_iwanai_de_-_mai_kuraki_.mp3', 'audio', 78, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (92, 'CLOUDINARY', 'audio/song_audio_-_77_-_77_-_issen_mankai_no_kiss_-_mai_kuraki_', 'song_audio_-_77_-_77_-_issen_mankai_no_kiss_-_mai_kuraki_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630833901/audio/song_audio_-_77_-_77_-_issen_mankai_no_kiss_-_mai_kuraki_.mp3', 'audio', 77, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (93, 'CLOUDINARY', 'cover/album_cover_-_8_-_8_-_smile_-_mai_kuraki_', 'album_cover_-_8_-_8_-_smile_-_mai_kuraki_.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1630834056/cover/album_cover_-_8_-_8_-_smile_-_mai_kuraki_.jpg', 'cover', 8, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (94, 'CLOUDINARY', 'audio/song_audio_-_57_-_57_-_kimi_e_no_uta_-_mai_kuraki_', 'song_audio_-_57_-_57_-_kimi_e_no_uta_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834060/audio/song_audio_-_57_-_57_-_kimi_e_no_uta_-_mai_kuraki_.m4a', 'audio', 57, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (95, 'CLOUDINARY', 'audio/song_audio_-_65_-_65_-_open_love_-_mai_kuraki_', 'song_audio_-_65_-_65_-_open_love_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834061/audio/song_audio_-_65_-_65_-_open_love_-_mai_kuraki_.m4a', 'audio', 65, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (96, 'CLOUDINARY', 'audio/song_audio_-_68_-_68_-_mystery_hero_-_mai_kuraki_', 'song_audio_-_68_-_68_-_mystery_hero_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834061/audio/song_audio_-_68_-_68_-_mystery_hero_-_mai_kuraki_.m4a', 'audio', 68, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (97, 'CLOUDINARY', 'audio/song_audio_-_67_-_67_-_sawage_life_-_mai_kuraki_', 'song_audio_-_67_-_67_-_sawage_life_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834062/audio/song_audio_-_67_-_67_-_sawage_life_-_mai_kuraki_.m4a', 'audio', 67, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (98, 'CLOUDINARY', 'audio/song_audio_-_52_-_52_-_yesterday_love_-_mai_kuraki_', 'song_audio_-_52_-_52_-_yesterday_love_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834063/audio/song_audio_-_52_-_52_-_yesterday_love_-_mai_kuraki_.m4a', 'audio', 52, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (99, 'CLOUDINARY', 'audio/song_audio_-_64_-_64_-_my_way_-_mai_kuraki_', 'song_audio_-_64_-_64_-_my_way_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834063/audio/song_audio_-_64_-_64_-_my_way_-_mai_kuraki_.m4a', 'audio', 64, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (100, 'CLOUDINARY', 'audio/song_audio_-_51_-_51_-_tell_me_why_-_mai_kuraki_', 'song_audio_-_51_-_51_-_tell_me_why_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834084/audio/song_audio_-_51_-_51_-_tell_me_why_-_mai_kuraki_.m4a', 'audio', 51, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (101, 'CLOUDINARY', 'audio/song_audio_-_44_-_44_-_life_is_beautiful_-_daichi_miura_', 'song_audio_-_44_-_44_-_life_is_beautiful_-_daichi_miura_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834292/audio/song_audio_-_44_-_44_-_life_is_beautiful_-_daichi_miura_.m4a', 'audio', 44, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (102, 'CLOUDINARY', 'audio/song_audio_-_45_-_45_-_eien_-_zard_', 'song_audio_-_45_-_45_-_eien_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834360/audio/song_audio_-_45_-_45_-_eien_-_zard_.mp3', 'audio', 45, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (103, 'CLOUDINARY', 'audio/song_audio_-_50_-_50_-_i_shall_be_released_-_u-ka_saegusa_in_db_', 'song_audio_-_50_-_50_-_i_shall_be_released_-_u-ka_saegusa_in_db_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834456/audio/song_audio_-_50_-_50_-_i_shall_be_released_-_u-ka_saegusa_in_db_.mp3', 'audio', 50, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (104, 'CLOUDINARY', 'audio/song_audio_-_48_-_48_-_my_friend_-_zard_', 'song_audio_-_48_-_48_-_my_friend_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834532/audio/song_audio_-_48_-_48_-_my_friend_-_zard_.mp3', 'audio', 48, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (106, 'CLOUDINARY', 'audio/song_audio_-_66_-_66_-_i_like_it_-_mai_kuraki_', 'song_audio_-_66_-_66_-_i_like_it_-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834669/audio/song_audio_-_66_-_66_-_i_like_it_-_mai_kuraki_.m4a', 'audio', 66, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (107, 'CLOUDINARY', 'audio/song_audio_-_47_-_47_-_today_is_another_day_-_zard_', 'song_audio_-_47_-_47_-_today_is_another_day_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834722/audio/song_audio_-_47_-_47_-_today_is_another_day_-_zard_.mp3', 'audio', 47, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (108, 'CLOUDINARY', 'audio/song_audio_-_55_-_55_-_n_u_ph_i_xa_nhau_-_wanbi_tu_n_anh_minh_qu_n_', 'song_audio_-_55_-_55_-_n_u_ph_i_xa_nhau_-_wanbi_tu_n_anh_minh_qu_n_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834768/audio/song_audio_-_55_-_55_-_n_u_ph_i_xa_nhau_-_wanbi_tu_n_anh_minh_qu_n_.mp3', 'audio', 55, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (105, 'CLOUDINARY', 'audio/song_audio_-_56_-_56_-_love_me_-_deen_', 'song_audio_-_56_-_56_-_love_me_-_deen_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630834928/audio/song_audio_-_56_-_56_-_love_me_-_deen_.mp3', 'audio', 56, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (109, 'CLOUDINARY', 'audio/song_audio_-_49_-_49_-_don_t_you_see_-_zard_', 'song_audio_-_49_-_49_-_don_t_you_see_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630835003/audio/song_audio_-_49_-_49_-_don_t_you_see_-_zard_.mp3', 'audio', 49, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (110, 'CLOUDINARY', 'audio/song_audio_-_35_-_35_-_be_proud__we_make_new_history__-_mai_kuraki_', 'song_audio_-_35_-_35_-_be_proud__we_make_new_history__-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630874100/audio/song_audio_-_35_-_35_-_be_proud__we_make_new_history__-_mai_kuraki_.m4a', 'audio', 35, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (111, 'CLOUDINARY', 'audio/song_audio_-_34_-_34_-_serendipity_-_mai_kuraki_zard_', 'song_audio_-_34_-_34_-_serendipity_-_mai_kuraki_zard_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1630875094/audio/song_audio_-_34_-_34_-_serendipity_-_mai_kuraki_zard_.m4a', 'audio', 34, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (112, 'CLOUDINARY', 'audio/song_audio_-_46_-_46_-_good_day_-_zard_', 'song_audio_-_46_-_46_-_good_day_-_zard_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1631076062/audio/song_audio_-_46_-_46_-_good_day_-_zard_.mp3', 'audio', 46, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (114, 'CLOUDINARY', 'avatar/artist_avatar_-_6_-_lam_phong', 'artist_avatar_-_6_-_lam_phong.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1631846829/avatar/artist_avatar_-_6_-_lam_phong.jpg', 'avatar', 6, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (115, 'CLOUDINARY', 'audio/song_audio_-_84_-_84_-__u_tr_____ch_a_-_l_m_phong_', 'song_audio_-_84_-_84_-__u_tr_____ch_a_-_l_m_phong_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1631847313/audio/song_audio_-_84_-_84_-__u_tr_____ch_a_-_l_m_phong_.mp3', 'audio', 84, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (121, 'CLOUDINARY', 'audio/song_audio_-_15_-_15_-_test_02_-_th_y_ti_n_', 'song_audio_-_15_-_15_-_test_02_-_th_y_ti_n_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1632160400/audio/song_audio_-_15_-_15_-_test_02_-_th_y_ti_n_.mp3', 'audio', 15, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (122, 'CLOUDINARY', 'avatar/artist_avatar_-_7_-_tieu_chinh_nam', 'artist_avatar_-_7_-_tieu_chinh_nam.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1632161030/avatar/artist_avatar_-_7_-_tieu_chinh_nam.jpg', 'avatar', 7, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (123, 'CLOUDINARY', 'audio/song_audio_-_85_-_85_-_t__ng_th_nh_-_ti_u_ch_nh_nam_', 'song_audio_-_85_-_85_-_t__ng_th_nh_-_ti_u_ch_nh_nam_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1632161208/audio/song_audio_-_85_-_85_-_t__ng_th_nh_-_ti_u_ch_nh_nam_.mp3', 'audio', 85, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (113, 'CLOUDINARY', 'avatar/user_avatar_-_admin', 'user_avatar_-_admin.gif', 'gif', 'IMAGE', 2, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1631639303/avatar/user_avatar_-_admin.gif', 'avatar', NULL, 'USER_AVATAR', 'admin');
INSERT INTO alpha_sound.resource_info VALUES (125, 'CLOUDINARY', 'avatar/artist_avatar_-_8_-_kana_boon', 'artist_avatar_-_8_-_kana_boon.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1632743025/avatar/artist_avatar_-_8_-_kana_boon.jpg', 'avatar', 8, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (126, 'CLOUDINARY', 'audio/song_audio_-_86_-_86_-_silhouette_-_kana_boon_', 'song_audio_-_86_-_86_-_silhouette_-_kana_boon_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1632743857/audio/song_audio_-_86_-_86_-_silhouette_-_kana_boon_.mp3', 'audio', 86, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (127, 'CLOUDINARY', 'audio/song_audio_-_19_-_19_-_test_03_-_minh_qu_n_', 'song_audio_-_19_-_19_-_test_03_-_minh_qu_n_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1632752705/audio/song_audio_-_19_-_19_-_test_03_-_minh_qu_n_.mp3', 'audio', 19, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (129, 'CLOUDINARY', 'cover/album_cover_-_60_-_60_-_wanbi_0901_-_wanbi_tu_n_anh_', 'album_cover_-_60_-_60_-_wanbi_0901_-_wanbi_tu_n_anh_.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1633376939/cover/album_cover_-_60_-_60_-_wanbi_0901_-_wanbi_tu_n_anh_.jpg', 'cover', 60, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (128, 'CLOUDINARY', 'cover/album_cover_-_60_-_60_-_wanbi_0901_-_wanbi_tu_n_anh_', 'album_cover_-_60_-_60_-_wanbi_0901_-_wanbi_tu_n_anh_.jpg', 'jpg', 'IMAGE', 2, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1633376806/cover/album_cover_-_60_-_60_-_wanbi_0901_-_wanbi_tu_n_anh_.jpg', 'cover', 60, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (130, 'CLOUDINARY', 'cover/album_cover_-_7_-_7_-_kh_c_ca_cho_em_-_th_y_ti_n_', 'album_cover_-_7_-_7_-_kh_c_ca_cho_em_-_th_y_ti_n_.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1633377130/cover/album_cover_-_7_-_7_-_kh_c_ca_cho_em_-_th_y_ti_n_.jpg', 'cover', 7, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (120, 'CLOUDINARY', 'audio/song_audio_-_14_-_14_-_test_01_-_thu_minh_', 'song_audio_-_14_-_14_-_test_01_-_thu_minh_.m4a', 'm4a', 'AUDIO', 2, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1632100019/audio/song_audio_-_14_-_14_-_test_01_-_thu_minh_.m4a', 'audio', 14, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (131, 'CLOUDINARY', 'audio/song_audio_-_12_-_12_-_cho_em_-_mr._siro_', 'song_audio_-_12_-_12_-_cho_em_-_mr._siro_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633377225/audio/song_audio_-_12_-_12_-_cho_em_-_mr._siro_.mp3', 'audio', 12, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (132, 'CLOUDINARY', 'avatar/artist_avatar_-_9_-_e-girl', 'artist_avatar_-_9_-_e-girl.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1633774184/avatar/artist_avatar_-_9_-_e-girl.jpg', 'avatar', 9, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (133, 'CLOUDINARY', 'avatar/artist_avatar_-_10_-_valshe', 'artist_avatar_-_10_-_valshe.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1633774231/avatar/artist_avatar_-_10_-_valshe.jpg', 'avatar', 10, 'ARTIST_AVATAR', NULL);
INSERT INTO alpha_sound.resource_info VALUES (134, 'CLOUDINARY', 'cover/album_cover_-_12_-_12_-_kimi_ga_inai_natsu_-_deen_', 'album_cover_-_12_-_12_-_kimi_ga_inai_natsu_-_deen_.jpg', 'JPG', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1633774444/cover/album_cover_-_12_-_12_-_kimi_ga_inai_natsu_-_deen_.jpg', 'cover', 12, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (135, 'CLOUDINARY', 'audio/song_audio_-_87_-_87_-_kimi_ga_inai_natsu_-_deen_', 'song_audio_-_87_-_87_-_kimi_ga_inai_natsu_-_deen_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633774452/audio/song_audio_-_87_-_87_-_kimi_ga_inai_natsu_-_deen_.mp3', 'audio', 87, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (136, 'CLOUDINARY', 'avatar/user_avatar_-_pysga1996', 'user_avatar_-_pysga1996.gif', 'gif', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1633775023/avatar/user_avatar_-_pysga1996.gif', 'avatar', NULL, 'USER_AVATAR', 'pysga1996');
INSERT INTO alpha_sound.resource_info VALUES (124, 'CLOUDINARY', 'avatar/user_avatar_-_admin', 'user_avatar_-_admin.gif', 'gif', 'IMAGE', 2, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1632742333/avatar/user_avatar_-_admin.gif', 'avatar', NULL, 'USER_AVATAR', 'admin');
INSERT INTO alpha_sound.resource_info VALUES (137, 'CLOUDINARY', 'cover/album_cover_-_13_-_13_-_butterfly_core_-_valshe_', 'album_cover_-_13_-_13_-_butterfly_core_-_valshe_.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1633775644/cover/album_cover_-_13_-_13_-_butterfly_core_-_valshe_.jpg', 'cover', 13, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (138, 'CLOUDINARY', 'audio/song_audio_-_88_-_88_-_butterfly_core_-_valshe_', 'song_audio_-_88_-_88_-_butterfly_core_-_valshe_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633775652/audio/song_audio_-_88_-_88_-_butterfly_core_-_valshe_.mp3', 'audio', 88, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (139, 'CLOUDINARY', 'audio/song_audio_-_89_-_89_-_star_gate_-_valshe_', 'song_audio_-_89_-_89_-_star_gate_-_valshe_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633775655/audio/song_audio_-_89_-_89_-_star_gate_-_valshe_.mp3', 'audio', 89, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (140, 'CLOUDINARY', 'audio/song_audio_-_90_-_90_-_aka_toge_-_valshe_', 'song_audio_-_90_-_90_-_aka_toge_-_valshe_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633775661/audio/song_audio_-_90_-_90_-_aka_toge_-_valshe_.mp3', 'audio', 90, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (141, 'CLOUDINARY', 'audio/song_audio_-_91_-_91_-_growing_of_my_heart_-_valshe_', 'song_audio_-_91_-_91_-_growing_of_my_heart_-_valshe_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633775666/audio/song_audio_-_91_-_91_-_growing_of_my_heart_-_valshe_.mp3', 'audio', 91, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (142, 'CLOUDINARY', 'cover/album_cover_-_14_-_14_-_odoru_ponpokorin_-_e-girl_', 'album_cover_-_14_-_14_-_odoru_ponpokorin_-_e-girl_.jpg', 'jpg', 'IMAGE', 1, 'https://res.cloudinary.com/hnjohecnn/image/upload/v1633775940/cover/album_cover_-_14_-_14_-_odoru_ponpokorin_-_e-girl_.jpg', 'cover', 14, 'ALBUM_COVER', NULL);
INSERT INTO alpha_sound.resource_info VALUES (143, 'CLOUDINARY', 'audio/song_audio_-_92_-_92_-_odoru_ponpokorin_-_e-girl_', 'song_audio_-_92_-_92_-_odoru_ponpokorin_-_e-girl_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633775944/audio/song_audio_-_92_-_92_-_odoru_ponpokorin_-_e-girl_.mp3', 'audio', 92, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (144, 'CLOUDINARY', 'audio/song_audio_-_93_-_93_-_ureshii__tanoshii__daisuki__-_e-girl_', 'song_audio_-_93_-_93_-_ureshii__tanoshii__daisuki__-_e-girl_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633775945/audio/song_audio_-_93_-_93_-_ureshii__tanoshii__daisuki__-_e-girl_.mp3', 'audio', 93, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (145, 'CLOUDINARY', 'audio/song_audio_-_61_-_61_-___i_m_t_-_wanbi_tu_n_anh_', 'song_audio_-_61_-_61_-___i_m_t_-_wanbi_tu_n_anh_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633776572/audio/song_audio_-_61_-_61_-___i_m_t_-_wanbi_tu_n_anh_.mp3', 'audio', 61, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (146, 'CLOUDINARY', 'audio/song_audio_-_14_-_14_-_chance_for_you__cinema_ver.__-_mai_kuraki_', 'song_audio_-_14_-_14_-_chance_for_you__cinema_ver.__-_mai_kuraki_.m4a', 'm4a', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1633777715/audio/song_audio_-_14_-_14_-_chance_for_you__cinema_ver.__-_mai_kuraki_.m4a', 'audio', 14, 'SONG_AUDIO', NULL);
INSERT INTO alpha_sound.resource_info VALUES (147, 'CLOUDINARY', 'audio/song_audio_-_94_-_94_-_dem_sau_-_thu_minh_', 'song_audio_-_94_-_94_-_dem_sau_-_thu_minh_.mp3', 'mp3', 'AUDIO', 1, 'https://res.cloudinary.com/hnjohecnn/video/upload/v1636699612/audio/song_audio_-_94_-_94_-_dem_sau_-_thu_minh_.mp3', 'audio', 94, 'SONG_AUDIO', NULL);


--
-- TOC entry 4272 (class 0 OID 14888125)
-- Dependencies: 240
-- Data for Name: song; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.song VALUES (45, 0, 231, 1, NULL, '1999-01-01 07:00:00', 'Eien', 'Eien', 147, NULL, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (88, 0, 262, 0, NULL, '2013-11-27 00:00:00', 'Butterfly Core', 'butterfly core', 147, 12, 'pysga1996', NULL, NULL, 0, '2021-10-09 10:34:10.952', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (89, 0, 219, 0, NULL, '2013-11-27 00:00:00', 'STAR GATE', 'star gate', 147, 12, 'pysga1996', NULL, NULL, 1, '2021-10-09 10:34:14.682', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (90, 0, 293, 0, NULL, '2013-11-27 00:00:00', 'Aka toge', 'aka toge', 147, 12, 'pysga1996', NULL, NULL, 0, '2021-10-09 10:34:20.154', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (91, 0, 261, 0, NULL, '2013-11-27 00:00:00', 'Growing of my heart', 'growing of my heart', 147, 2, 'pysga1996', NULL, NULL, 0, '2021-10-09 10:34:24.936', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (61, 0, 259, 6, NULL, '2008-01-02 00:00:00', 'Đôi mắt', 'doi mat', 1, 2, 'thanhvt', NULL, NULL, 6, '2021-01-01 18:03:44', NULL, '2021-10-09 10:49:34.259', 1, 1);
INSERT INTO alpha_sound.song VALUES (44, 0, 247, 2, NULL, '2017-01-01 07:00:00', 'Life is beautiful', 'Life is beautiful', 147, NULL, 'admin', NULL, NULL, 2, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (43, 0, 339, 8, NULL, '2018-03-09 07:00:00', 'Do It!', 'Do It!', 147, NULL, 'admin', NULL, NULL, 9, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (94, 0, 319, 0, NULL, '1979-12-18 00:00:00', 'Dem sau', 'dem sau', NULL, NULL, 'admin', NULL, NULL, 1, '2021-11-12 06:46:51.06', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (8, 0, 300, 53, NULL, '2014-01-01 00:00:00', 'Dòng thư cuối', 'dong thu cuoi', 147, NULL, 'thanhvt', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (9, 0, 300, 34, NULL, '2014-01-01 00:00:00', 'Còn mong chờ chi', 'con mong cho chi', 147, NULL, 'thanhvt', NULL, NULL, 34, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (10, 0, 300, 26, NULL, '2014-01-01 00:00:00', 'Bắt đầu từ một kết thúc', 'bat dau tu mot ket thuc', 1, NULL, 'thanhvt', NULL, NULL, 26, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (11, 0, 300, 24, NULL, '2014-01-01 00:00:00', 'Chuyện tình gió', 'chuyen tinh gio', 147, NULL, 'thanhvt', NULL, NULL, 24, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (20, 0, 227, 4, NULL, '2021-02-02 07:00:00', 'Test 04', 'test 04', 4, NULL, 'thanhvt', NULL, NULL, 4, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (76, 0, 241, 5, NULL, '2018-01-01 07:00:00', 'Koyoi wa yume o mi sasete', 'Koyoi wa yume o mi sasete', 147, NULL, 'admin', NULL, NULL, 5, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (70, 0, 219, 1, NULL, '1992-01-01 00:00:00', 'Ai wa Nemutteru', 'Ai wa Nemutteru', 147, NULL, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (79, 0, 301, 1, NULL, '1992-01-01 00:00:00', 'So Together', 'So Together', 147, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (80, 0, 253, 0, NULL, '1992-01-01 00:00:00', 'Dangerous Tonight', 'Dangerous Tonight', 147, NULL, 'admin', NULL, NULL, 2, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (69, 0, 265, 6, NULL, '2021-01-01 00:00:00', 'Ano Hohoemi wo Wasurenai de', 'Ano Hohoemi wo Wasurenai de', 147, NULL, 'admin', NULL, NULL, 6, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (71, 0, 299, 0, NULL, '1992-01-01 00:00:00', 'Konna ni Aishite mo', 'Konna ni Aishite mo', 147, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (83, 0, 247, 1, NULL, '2018-01-01 00:00:00', 'WE ARE HAPPY WOMEN', 'WE ARE HAPPY WOMEN', 147, NULL, 'admin', NULL, NULL, 2, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (75, 0, 212, 3, NULL, '2018-01-01 00:00:00', 'Makkana kasa ~ Kyouto no ame ~', 'Makkana kasa ~ Kyouto no ame ~', 147, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (73, 0, 247, 0, NULL, '2018-01-01 00:00:00', 'Togetsukyou ~-kun sou fu ~', 'Togetsukyou ~-kun sou fu ~', 147, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (74, 0, 265, 3, NULL, '2018-01-01 00:00:00', 'Hanakotoba', 'Hanakotoba', 147, NULL, 'admin', NULL, NULL, 3, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (78, 0, 278, 1, NULL, '2011-01-01 00:00:00', 'Sayonara wa Mada Iwanai de', 'Sayonara wa Mada Iwanai de', 147, NULL, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (47, 0, 315, 2, NULL, '1996-01-01 07:00:00', 'Today Is Another Day', 'Today Is Another Day', 147, NULL, 'admin', NULL, NULL, 2, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (55, 0, 318, 0, NULL, '2002-01-01 07:00:00', 'Nếu phải xa nhau', 'Neu phai xa nhau', 1, NULL, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (49, 0, 302, 2, NULL, '1997-01-01 07:00:00', 'Don''t You See', 'Don''t You See', 147, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (35, 0, 276, 6, NULL, '2018-03-09 07:00:00', 'Be Proud ~we make new history~', 'Be Proud ~we make new history~', 1, NULL, 'admin', NULL, NULL, 6, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (34, 0, 256, 28, NULL, '2021-06-11 07:00:00', 'Serendipity', 'Serendipity', 147, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (46, 0, 313, 1, NULL, '1999-01-01 07:00:00', 'Good Day', 'Good Day', 147, NULL, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (84, 0, 266, 0, NULL, '2012-01-01 00:00:00', 'Ấu trĩ đủ chưa', 'Au tri du chua', 2, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (12, 0, 289, 28, NULL, '2014-01-01 00:00:00', 'Cho em', 'Cho em', 147, NULL, 'thanhvt', NULL, NULL, 29, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (14, 0, 220, 3, 'Ame ga mado wo tataku

Itsumademo soko de ame yadori?

Mezasu basho mou sugu

Me no mae ni miete kuru no ni ne

Oh yeah

Sonna fuu ni sora wo mite iru dake ja

Boku wa kimi no kasa ni naru yo dakara

Saisho no ippo fumi dashite miyou

Kyou toiu hi wa nido to tsukame nai kara

Kimi no tame no chance for you

Ame ga yandara dare yori mo

Saki ni niji wo mitsukete miyou

Sunda sora ni kimi no yume ga tsuduku forever

Oh yeah

Kizutsuku koto wo osorezu ima wa

Yuuki wo motte

Kimi wo ikite miyou yo

Boku wa itsudemo soba de miteru chance for you

Kokoro kimetara ryoute wo hiroge

Mune ippai no

Jishin wo motte

Kimi wo ikite miyou yo

Soba de miteru chance for you

Kimi no tame no chance for you', '2018-02-10 00:00:00', 'chance for you ~cinema ver.~', 'chance for you ~cinema ver.~', 147, 3, 'thanhvt', NULL, NULL, 4, '2021-01-01 18:03:44', NULL, '2021-10-09 11:08:35.995', 1, 1);
INSERT INTO alpha_sound.song VALUES (72, 0, 262, 0, NULL, '2018-01-01 00:00:00', 'Light Up My Life', 'light up my life', 147, 13, 'admin', NULL, NULL, 3, '2021-01-01 18:03:44', NULL, '2021-10-09 11:27:43.313', 1, 1);
INSERT INTO alpha_sound.song VALUES (93, 0, 241, 0, NULL, '2014-01-01 00:00:00', 'Ureshii! Tanoshii! Daisuki!', 'ureshii! tanoshii! daisuki!', 147, 7, 'zeronos', NULL, NULL, 0, '2021-10-09 10:39:04.882', NULL, '2021-10-09 11:39:12.827', 1, 1);
INSERT INTO alpha_sound.song VALUES (92, 0, 200, 0, NULL, '2014-01-01 00:00:00', 'Odoru Ponpokorin', 'odoru ponpokorin', 147, 7, 'zeronos', NULL, NULL, 0, '2021-10-09 10:39:03.037', NULL, '2021-10-09 11:39:12.838', 1, 1);
INSERT INTO alpha_sound.song VALUES (56, 0, 261, 0, NULL, '1997-01-01 07:00:00', 'love me', 'love me', 147, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (66, 0, 215, 0, NULL, '2017-01-01 07:00:00', 'I like it', 'I like it', 147, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (85, 0, 207, 0, NULL, '2013-01-01 00:00:00', 'Tường Thành', 'Tuong Thanh', 3, 1, 'thanhvt', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (77, 0, 302, 2, NULL, '2011-01-01 00:00:00', 'Issen mankai no Kiss', 'Issen mankai no Kiss', 147, NULL, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (64, 0, 251, 1, NULL, '2017-01-01 00:00:00', 'My way', 'my way', 147, 2, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, '2021-10-09 11:34:19.862', 1, 1);
INSERT INTO alpha_sound.song VALUES (57, 0, 219, 1, NULL, '2017-01-01 00:00:00', 'Kimi e no uta', 'kimi e no uta', 147, NULL, 'admin', NULL, NULL, 2, '2021-01-01 18:03:44', NULL, '2021-10-09 11:34:20.054', 1, 1);
INSERT INTO alpha_sound.song VALUES (65, 0, 202, 1, NULL, '2017-01-01 00:00:00', 'Open Love', 'open love', 147, 2, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, '2021-10-09 11:34:20.06', 1, 1);
INSERT INTO alpha_sound.song VALUES (68, 0, 246, 1, NULL, '2017-01-01 00:00:00', 'Mystery Hero', 'mystery hero', 147, 2, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, '2021-10-09 11:34:20.082', 1, 1);
INSERT INTO alpha_sound.song VALUES (51, 0, 202, 1, NULL, '2017-01-01 00:00:00', 'Tell me why', 'tell me why', 147, 2, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, '2021-10-09 11:34:20.138', 1, 1);
INSERT INTO alpha_sound.song VALUES (67, 0, 200, 1, NULL, '2017-01-01 00:00:00', 'Sawage Life', 'sawage life', 147, 3, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, '2021-10-09 11:34:20.53', 1, 1);
INSERT INTO alpha_sound.song VALUES (48, 0, 261, 1, NULL, '1996-01-01 07:00:00', 'My Friend', 'My Friend', 147, NULL, 'admin', NULL, NULL, 2, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (50, 0, 245, 1, NULL, '2003-01-01 07:00:00', 'I shall be released', 'I shall be released', 147, NULL, 'admin', NULL, NULL, 1, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (52, 0, 296, 0, NULL, '2017-01-01 00:00:00', 'YESTERDAY LOVE', 'yesterday love', 147, 2, 'admin', NULL, NULL, 0, '2021-01-01 18:03:44', NULL, '2021-10-09 11:34:20.312', 1, 1);
INSERT INTO alpha_sound.song VALUES (86, 0, 241, 0, '[Verse 1]
Isse no se de fumikomu gourain bokura wa
Nanimo nanimo mada shiranu
Issen koete furikaeruto mou nai bokura wa
Nanimo nanimo mada shiranu

[Pre-Chorus]
Udatte udatte udatteku
Kirameku ase ga koboreru no sa

[Chorus]
Oboetenai koto mo takusan attadarou
Daremo kare mo shiruetto
Daiji ni shitetta mono wasureta
Furi o shitanda yo
Nanimo nani yo waraerusa

[Verse 2]
Isse no de omoidasu shounen
Bokura wa nanimo kamo wo hoshiga tta
Wakatteru tte a kizuiteru tte
Tokei no hari wa hibi wa tomaranai
Ubatte ubatte ubatteku nagareru toki to kioku
Tooku tooku tooku ni natte
[Chorus]
Oboetenai koto mo takusan attadarou
Daremo kare mo shiruetto
Wo sore de amaru koto shiranai
Furi wo shitanda yo
Nanimo nani yo waraerusa

[Pre-Chorus]
Hirari to hirari to matteru
Konoha no you ni yureru koto naku
Shousou nakusu sugoshiteitai yo

[Chorus]
Oboetenai koto mo takusan atta kedo
Kitto Zutto kawaranai
Mono ga aru koto o oshiete kureta anata wa
Kieru Kieru Shiruetto
Daiji ni shitai mono motte otona ni naru nda
Don''na toki mo hanasazu ni
Mamori tsuzukeyou soshitara itsu no hi ni ka
Nanimo kamo wo waraerusa

[Outro]
Hirari to hirari to matteru
Konoha ga tonde yuku', '2014-11-26 00:00:00', 'Silhouette', 'Silhouette', 147, 1, 'thanhvt', NULL, NULL, 3, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (19, 0, 298, 1, 'takusareta kono yume wo mune ni
hateshinaku kono tsudzuku michi de
tashika na yume wo
kizamu koto wa igai to muzukashii

* but I can''t do...
well...make it better
oh dakara　I won''t be afraid
everyday
I get pain
soredemo　oh Everything take it all

I just wanna be with you
nakushita mono mitsukete
to be together to be together
kidzukidasu
ima koso　change my heart
negai wa kanau hazu sa
to be together to be together
tsugi e susumou
get back in love, peace and dream!

kawaru koto osorezu in my life
tadoritsuku saki waratte itai
ikudoori mono michi wo sagashite　yeah
erande iku

* repeat

I just wanna be with you
itsu no ma ni ka hohoemu
to be together to be together
te wo tsunagou
kono mama　be with you
hora takaraka ni ima
to be together to be together
utaidasou
get back in love, peace and dream!

I just wanna be with you
arigatou tte kidzukeba hora
to be together to be together
kimi ga iru
ima kara　be with you
donna mukaikaze demo
to be together to be together
tachimukau　with you
get back in love, peace and dream!', '2007-02-02 00:00:00', 'BE WITH U', 'BE WITH U', 147, 9, 'thanhvt', NULL, NULL, 3, '2021-01-01 18:03:44', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (87, 0, 264, 0, NULL, '1997-08-27 00:00:00', 'Kimi ga Inai natsu', 'kimi ga inai natsu', 147, 12, 'member', NULL, NULL, 1, '2021-10-09 10:14:10.99', NULL, NULL, 1, 1);
INSERT INTO alpha_sound.song VALUES (15, 0, 348, 2, 'Đôi khi bên anh em mong được gần em mãi
Đôi khi em tin rằng em đã thầm yêu
Mà sao khi bên nhau em không thấy ngượng ngùng
Còn sau khi xa anh lòng em không thấy nhớ nhung
Phải vì anh vội vàng nên đành lỡ mất con tim muộn màng
Như mây lang thang ngập ngừng lời muốn nói
Bao năm bên nhau sao đành chia xa
Còn đâu ánh mắt nào em muốn gửi trao anh
Nụ hôn nồng say ấm áp trên môi
Thôi cũng quên rồi ôi dĩ vãng ngây thơ
Một tình yêu dại khờ cũng theo người đi mãi xa
Anh tình yêu đã xa rồi
Dẫu có em bên mình anh vẫn hay rằng anh chỉ nâng cánh chim bay
Chẳng bao giờ có được người yêu dấu
Chất chứa trong lòng bao đắng cay
Người hỡi! Tình yêu đã xa rồi thiêu đốt con tim bằng ngọn lửa đắm say
Để lại cô đơn bằng lời nói giá băng.
Dù mãi chỉ là khát khao mong nhớ vẫn biết trong đời một ước mơ còn mãi.', '2008-02-10 00:00:00', 'Lời chưa nói', 'loi chua noi', 1, 1, 'thanhvt', NULL, NULL, 3, '2021-01-01 18:03:44', NULL, '2021-10-09 11:03:18.616', 1, 1);


--
-- TOC entry 4273 (class 0 OID 14888134)
-- Dependencies: 241
-- Data for Name: song_artist; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.song_artist VALUES (8, 20, NULL);
INSERT INTO alpha_sound.song_artist VALUES (9, 18, NULL);
INSERT INTO alpha_sound.song_artist VALUES (10, 17, NULL);
INSERT INTO alpha_sound.song_artist VALUES (11, 19, NULL);
INSERT INTO alpha_sound.song_artist VALUES (24, 19, NULL);
INSERT INTO alpha_sound.song_artist VALUES (29, 19, NULL);
INSERT INTO alpha_sound.song_artist VALUES (53, 45, NULL);
INSERT INTO alpha_sound.song_artist VALUES (59, 45, NULL);
INSERT INTO alpha_sound.song_artist VALUES (27, 19, NULL);
INSERT INTO alpha_sound.song_artist VALUES (3, 17, NULL);
INSERT INTO alpha_sound.song_artist VALUES (4, 17, NULL);
INSERT INTO alpha_sound.song_artist VALUES (5, 17, NULL);
INSERT INTO alpha_sound.song_artist VALUES (6, 17, NULL);
INSERT INTO alpha_sound.song_artist VALUES (7, 17, NULL);
INSERT INTO alpha_sound.song_artist VALUES (16, 15, NULL);
INSERT INTO alpha_sound.song_artist VALUES (17, 15, NULL);
INSERT INTO alpha_sound.song_artist VALUES (18, 15, NULL);
INSERT INTO alpha_sound.song_artist VALUES (20, 16, NULL);
INSERT INTO alpha_sound.song_artist VALUES (76, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (53, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (54, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (59, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (63, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (43, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (70, 2, NULL);
INSERT INTO alpha_sound.song_artist VALUES (79, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (80, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (69, 2, NULL);
INSERT INTO alpha_sound.song_artist VALUES (71, 2, NULL);
INSERT INTO alpha_sound.song_artist VALUES (75, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (73, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (83, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (74, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (78, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (77, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (44, 4, NULL);
INSERT INTO alpha_sound.song_artist VALUES (45, 2, NULL);
INSERT INTO alpha_sound.song_artist VALUES (50, 5, NULL);
INSERT INTO alpha_sound.song_artist VALUES (48, 2, NULL);
INSERT INTO alpha_sound.song_artist VALUES (47, 2, NULL);
INSERT INTO alpha_sound.song_artist VALUES (55, 45, NULL);
INSERT INTO alpha_sound.song_artist VALUES (55, 15, NULL);
INSERT INTO alpha_sound.song_artist VALUES (56, 3, NULL);
INSERT INTO alpha_sound.song_artist VALUES (49, 2, NULL);
INSERT INTO alpha_sound.song_artist VALUES (35, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (66, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (34, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (34, 2, NULL);
INSERT INTO alpha_sound.song_artist VALUES (46, 2, NULL);
INSERT INTO alpha_sound.song_artist VALUES (84, 6, NULL);
INSERT INTO alpha_sound.song_artist VALUES (85, 7, NULL);
INSERT INTO alpha_sound.song_artist VALUES (86, 8, NULL);
INSERT INTO alpha_sound.song_artist VALUES (19, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (12, 45, NULL);
INSERT INTO alpha_sound.song_artist VALUES (87, 3, NULL);
INSERT INTO alpha_sound.song_artist VALUES (88, 10, NULL);
INSERT INTO alpha_sound.song_artist VALUES (89, 10, NULL);
INSERT INTO alpha_sound.song_artist VALUES (90, 10, NULL);
INSERT INTO alpha_sound.song_artist VALUES (91, 10, NULL);
INSERT INTO alpha_sound.song_artist VALUES (61, 45, NULL);
INSERT INTO alpha_sound.song_artist VALUES (15, 159, NULL);
INSERT INTO alpha_sound.song_artist VALUES (14, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (72, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (64, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (57, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (65, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (68, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (51, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (52, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (67, 1, NULL);
INSERT INTO alpha_sound.song_artist VALUES (93, 9, NULL);
INSERT INTO alpha_sound.song_artist VALUES (92, 9, NULL);
INSERT INTO alpha_sound.song_artist VALUES (94, 17, NULL);


--
-- TOC entry 4274 (class 0 OID 14888137)
-- Dependencies: 242
-- Data for Name: song_genre; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.song_genre VALUES (85, 2);
INSERT INTO alpha_sound.song_genre VALUES (86, 2);
INSERT INTO alpha_sound.song_genre VALUES (19, 4);
INSERT INTO alpha_sound.song_genre VALUES (87, 4);
INSERT INTO alpha_sound.song_genre VALUES (88, 2);
INSERT INTO alpha_sound.song_genre VALUES (89, 2);
INSERT INTO alpha_sound.song_genre VALUES (90, 2);
INSERT INTO alpha_sound.song_genre VALUES (91, 2);
INSERT INTO alpha_sound.song_genre VALUES (61, 4);
INSERT INTO alpha_sound.song_genre VALUES (15, 1);
INSERT INTO alpha_sound.song_genre VALUES (14, 2);
INSERT INTO alpha_sound.song_genre VALUES (64, 4);
INSERT INTO alpha_sound.song_genre VALUES (65, 4);
INSERT INTO alpha_sound.song_genre VALUES (68, 4);
INSERT INTO alpha_sound.song_genre VALUES (51, 4);
INSERT INTO alpha_sound.song_genre VALUES (52, 4);
INSERT INTO alpha_sound.song_genre VALUES (67, 4);
INSERT INTO alpha_sound.song_genre VALUES (93, 4);
INSERT INTO alpha_sound.song_genre VALUES (92, 4);


--
-- TOC entry 4276 (class 0 OID 14888142)
-- Dependencies: 244
-- Data for Name: song_rating; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--



--
-- TOC entry 4277 (class 0 OID 14888145)
-- Dependencies: 245
-- Data for Name: song_tag; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.song_tag VALUES (35, 11);
INSERT INTO alpha_sound.song_tag VALUES (43, 11);
INSERT INTO alpha_sound.song_tag VALUES (44, 14);
INSERT INTO alpha_sound.song_tag VALUES (45, 14);
INSERT INTO alpha_sound.song_tag VALUES (46, 14);
INSERT INTO alpha_sound.song_tag VALUES (47, 14);
INSERT INTO alpha_sound.song_tag VALUES (48, 14);
INSERT INTO alpha_sound.song_tag VALUES (49, 14);
INSERT INTO alpha_sound.song_tag VALUES (49, 15);
INSERT INTO alpha_sound.song_tag VALUES (50, 14);
INSERT INTO alpha_sound.song_tag VALUES (50, 16);
INSERT INTO alpha_sound.song_tag VALUES (55, 17);
INSERT INTO alpha_sound.song_tag VALUES (56, 14);
INSERT INTO alpha_sound.song_tag VALUES (56, 15);
INSERT INTO alpha_sound.song_tag VALUES (84, 18);
INSERT INTO alpha_sound.song_tag VALUES (85, 18);
INSERT INTO alpha_sound.song_tag VALUES (86, 24);
INSERT INTO alpha_sound.song_tag VALUES (19, 14);
INSERT INTO alpha_sound.song_tag VALUES (19, 23);
INSERT INTO alpha_sound.song_tag VALUES (87, 14);
INSERT INTO alpha_sound.song_tag VALUES (87, 26);
INSERT INTO alpha_sound.song_tag VALUES (88, 27);
INSERT INTO alpha_sound.song_tag VALUES (88, 28);
INSERT INTO alpha_sound.song_tag VALUES (89, 29);
INSERT INTO alpha_sound.song_tag VALUES (89, 30);
INSERT INTO alpha_sound.song_tag VALUES (90, 31);
INSERT INTO alpha_sound.song_tag VALUES (90, 32);
INSERT INTO alpha_sound.song_tag VALUES (91, 33);
INSERT INTO alpha_sound.song_tag VALUES (91, 34);
INSERT INTO alpha_sound.song_tag VALUES (61, 17);
INSERT INTO alpha_sound.song_tag VALUES (61, 35);
INSERT INTO alpha_sound.song_tag VALUES (15, 17);
INSERT INTO alpha_sound.song_tag VALUES (15, 37);
INSERT INTO alpha_sound.song_tag VALUES (14, 14);
INSERT INTO alpha_sound.song_tag VALUES (14, 38);
INSERT INTO alpha_sound.song_tag VALUES (72, 14);
INSERT INTO alpha_sound.song_tag VALUES (72, 39);
INSERT INTO alpha_sound.song_tag VALUES (64, 14);
INSERT INTO alpha_sound.song_tag VALUES (64, 41);
INSERT INTO alpha_sound.song_tag VALUES (65, 14);
INSERT INTO alpha_sound.song_tag VALUES (65, 42);
INSERT INTO alpha_sound.song_tag VALUES (68, 14);
INSERT INTO alpha_sound.song_tag VALUES (68, 43);
INSERT INTO alpha_sound.song_tag VALUES (51, 14);
INSERT INTO alpha_sound.song_tag VALUES (51, 44);
INSERT INTO alpha_sound.song_tag VALUES (52, 14);
INSERT INTO alpha_sound.song_tag VALUES (52, 45);
INSERT INTO alpha_sound.song_tag VALUES (67, 14);
INSERT INTO alpha_sound.song_tag VALUES (67, 46);
INSERT INTO alpha_sound.song_tag VALUES (93, 14);
INSERT INTO alpha_sound.song_tag VALUES (93, 48);
INSERT INTO alpha_sound.song_tag VALUES (92, 14);
INSERT INTO alpha_sound.song_tag VALUES (92, 49);
INSERT INTO alpha_sound.song_tag VALUES (92, 50);


--
-- TOC entry 4278 (class 0 OID 14888148)
-- Dependencies: 246
-- Data for Name: tag; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.tag VALUES (11, 'administrator', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (14, 'j-pop', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (15, 'dragon-ball', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (16, 'metantei-conan', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (17, 'v-pop', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (18, 'hk-pop', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (35, 'wanbi', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (37, 'tranthuha', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (23, 'mai-k', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (24, 'j-rock', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (25, 'deen', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (48, 'e-girl', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (50, 'anime', NULL, NULL, 1);
INSERT INTO alpha_sound.tag VALUES (28, 'valshe', NULL, NULL, 1);


--
-- TOC entry 4280 (class 0 OID 14888153)
-- Dependencies: 248
-- Data for Name: theme; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.theme VALUES (1, 'Trường học', '2021-09-20 01:28:53.307', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (2, 'Tình yêu', '2021-09-20 17:56:45.693', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (3, 'Tình bạn', '2021-09-20 17:56:53.518', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (4, 'Tuổi học trò', '2021-09-20 17:56:59.07', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (5, 'Thời thơ ấu', '2021-09-20 17:57:04.947', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (6, 'Mẹ', '2021-09-20 17:57:12.96', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (7, 'Gia đình', '2021-09-20 17:57:18.28', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (8, 'Chiến tranh', '2021-09-20 17:57:22.405', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (9, 'Quộc sống', '2021-09-20 17:57:29.257', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (10, 'Thiên nhiên', '2021-09-20 17:57:34.771', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (11, 'Du lịch', '2021-09-20 17:57:38.355', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (12, 'Thần bí', '2021-09-20 17:57:44.79', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (13, 'Cổ xưa', '2021-09-20 17:57:48.853', NULL, 1);
INSERT INTO alpha_sound.theme VALUES (14, 'Thần thoại', '2021-09-20 17:57:56.353', '2021-09-20 17:58:09.509', 1);


--
-- TOC entry 4282 (class 0 OID 14888158)
-- Dependencies: 250
-- Data for Name: user_favorites; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 7, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 21, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 19, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 84, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 12, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 15, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 8, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 20, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 56, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 61, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 1, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 17, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 9, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 6, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 57, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 19, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 43, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 15, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 20, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 60, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 4, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 70, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 14, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 65, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 43, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 11, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 60, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 64, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 71, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 46, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 3, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 16, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 35, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 78, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 45, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 10, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 20, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 74, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 9, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 76, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 69, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 68, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 55, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 48, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 64, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 11, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 70, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 86, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 19, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 9, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 10, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 80, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 79, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 75, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 9, 'ALBUM', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 78, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 72, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 7, 'ALBUM', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 77, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 43, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 73, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 12, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 15, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 67, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 69, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 74, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 80, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 87, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 76, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 61, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 57, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('pysga1996', 89, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 93, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 90, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 15, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('pysga1996', 88, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 69, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 71, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 3, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 46, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 65, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('member', 47, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 12, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 66, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 57, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 88, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 74, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 16, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 14, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 92, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 19, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 1, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 8, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 90, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 74, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 10, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('thanhvt', 72, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 8, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 87, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 67, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 60, 'ALBUM', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 56, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 43, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 50, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 51, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 52, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 86, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 48, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 44, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 6, 'ARTIST', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 83, 'SONG', 1, 0, true);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 13, 'ALBUM', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('zeronos', 89, 'SONG', 1, 0, false);
INSERT INTO alpha_sound.user_favorites VALUES ('admin', 94, 'SONG', 1, 0, true);


--
-- TOC entry 4283 (class 0 OID 14888176)
-- Dependencies: 251
-- Data for Name: user_info; Type: TABLE DATA; Schema: alpha_sound; Owner: -
--

INSERT INTO alpha_sound.user_info VALUES ('{"user_name": "thanhvt","first_name":  "Tất Thành", "last_name": "Vũ", "avatar_url": "https://res.cloudinary.com/hkhrh3ta7/image/upload/v1612104827/avatar/thanhvt.gif"}', 'thanhvt', '{"darkMode":false}', NULL, NULL, 1);
INSERT INTO alpha_sound.user_info VALUES ('{"user_name": "zeronos"}', 'zeronos', '{"darkMode":false}', NULL, NULL, 1);
INSERT INTO alpha_sound.user_info VALUES ('{"user_name": "member"}', 'member', '{"darkMode":false}', NULL, NULL, 1);
INSERT INTO alpha_sound.user_info VALUES ('{"user_name":"admin","first_name":"Vũ","last_name":"Thành","phone_number":"+84374225982","gender":true,"date_of_birth":"1996-09-25T00:00:00.000Z","email":"","avatar_url":"https://res.cloudinary.com/hnjohecnn/image/upload/v1632742333/avatar/user_avatar_-_admin.gif"}', 'admin', '{"darkMode":true}', NULL, '2021-09-27 11:32:14.427', 1);
INSERT INTO alpha_sound.user_info VALUES ('{"user_name":"pysga1996","first_name":"Huy Thái","last_name":"Vũ ","phone_number":"0904630014","gender":true,"date_of_birth":"1961-09-01T00:00:00.000Z","email":"","avatar_url":"https://res.cloudinary.com/hnjohecnn/image/upload/v1633775023/avatar/user_avatar_-_pysga1996.gif"}', 'pysga1996', '{"darkMode":true}', NULL, '2021-10-09 10:23:43.908', 1);


--
-- TOC entry 4300 (class 0 OID 0)
-- Dependencies: 224
-- Name: album_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.album_id_seq', 14, true);


--
-- TOC entry 4301 (class 0 OID 0)
-- Dependencies: 228
-- Name: artist_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.artist_id_seq', 10, true);


--
-- TOC entry 4302 (class 0 OID 0)
-- Dependencies: 230
-- Name: comment_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.comment_id_seq', 4, true);


--
-- TOC entry 4303 (class 0 OID 0)
-- Dependencies: 232
-- Name: country_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.country_id_seq', 3, true);


--
-- TOC entry 4304 (class 0 OID 0)
-- Dependencies: 234
-- Name: genre_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.genre_id_seq', 9, true);


--
-- TOC entry 4305 (class 0 OID 0)
-- Dependencies: 236
-- Name: playlist_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.playlist_id_seq', 5, true);


--
-- TOC entry 4306 (class 0 OID 0)
-- Dependencies: 239
-- Name: resource_info_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.resource_info_id_seq', 147, true);


--
-- TOC entry 4307 (class 0 OID 0)
-- Dependencies: 243
-- Name: song_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.song_id_seq', 94, true);


--
-- TOC entry 4308 (class 0 OID 0)
-- Dependencies: 247
-- Name: tag_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.tag_id_seq', 50, true);


--
-- TOC entry 4309 (class 0 OID 0)
-- Dependencies: 249
-- Name: theme_id_seq; Type: SEQUENCE SET; Schema: alpha_sound; Owner: -
--

SELECT pg_catalog.setval('alpha_sound.theme_id_seq', 14, true);


--
-- TOC entry 4055 (class 2606 OID 14888199)
-- Name: album_artist album_artist_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.album_artist
    ADD CONSTRAINT album_artist_pk PRIMARY KEY (album_id, artist_id);


--
-- TOC entry 4059 (class 2606 OID 14888201)
-- Name: album_genre album_genre_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.album_genre
    ADD CONSTRAINT album_genre_pk PRIMARY KEY (album_id, genre_id);


--
-- TOC entry 4050 (class 2606 OID 14888203)
-- Name: album album_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.album
    ADD CONSTRAINT album_pk PRIMARY KEY (id);


--
-- TOC entry 4063 (class 2606 OID 14888205)
-- Name: album_song album_song_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.album_song
    ADD CONSTRAINT album_song_pk PRIMARY KEY (album_id, song_id);


--
-- TOC entry 4067 (class 2606 OID 14888207)
-- Name: album_tag album_tag_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.album_tag
    ADD CONSTRAINT album_tag_pk PRIMARY KEY (album_id, tag_id);


--
-- TOC entry 4071 (class 2606 OID 14888209)
-- Name: artist artist_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.artist
    ADD CONSTRAINT artist_pk PRIMARY KEY (id);


--
-- TOC entry 4073 (class 2606 OID 14888211)
-- Name: comment comment_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.comment
    ADD CONSTRAINT comment_pk PRIMARY KEY (id);


--
-- TOC entry 4077 (class 2606 OID 14888213)
-- Name: country country_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.country
    ADD CONSTRAINT country_pk PRIMARY KEY (id);


--
-- TOC entry 4114 (class 2606 OID 15227201)
-- Name: user_favorites favorites_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.user_favorites
    ADD CONSTRAINT favorites_pk PRIMARY KEY (username, entity_id, type);


--
-- TOC entry 4079 (class 2606 OID 14888215)
-- Name: genre genre_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.genre
    ADD CONSTRAINT genre_pk PRIMARY KEY (id);


--
-- TOC entry 4082 (class 2606 OID 14888219)
-- Name: playlist playlist_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.playlist
    ADD CONSTRAINT playlist_pk PRIMARY KEY (id);


--
-- TOC entry 4086 (class 2606 OID 14888221)
-- Name: playlist_song playlist_song_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.playlist_song
    ADD CONSTRAINT playlist_song_pk PRIMARY KEY (playlist_id, song_id);


--
-- TOC entry 4096 (class 2606 OID 14888223)
-- Name: song_artist song_artist_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.song_artist
    ADD CONSTRAINT song_artist_pk PRIMARY KEY (song_id, artist_id);


--
-- TOC entry 4100 (class 2606 OID 14888225)
-- Name: song_genre song_genre_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.song_genre
    ADD CONSTRAINT song_genre_pk PRIMARY KEY (song_id, genre_id);


--
-- TOC entry 4092 (class 2606 OID 14888227)
-- Name: song song_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.song
    ADD CONSTRAINT song_pk PRIMARY KEY (id);


--
-- TOC entry 4104 (class 2606 OID 14888229)
-- Name: song_rating song_rating_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.song_rating
    ADD CONSTRAINT song_rating_pk PRIMARY KEY (id);


--
-- TOC entry 4108 (class 2606 OID 14888231)
-- Name: song_tag song_tag_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.song_tag
    ADD CONSTRAINT song_tag_pk PRIMARY KEY (song_id, tag_id);


--
-- TOC entry 4110 (class 2606 OID 14888233)
-- Name: tag tag_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.tag
    ADD CONSTRAINT tag_pk PRIMARY KEY (id);


--
-- TOC entry 4112 (class 2606 OID 14888235)
-- Name: theme theme_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.theme
    ADD CONSTRAINT theme_pk PRIMARY KEY (id);


--
-- TOC entry 4116 (class 2606 OID 14888245)
-- Name: user_info user_info_pk; Type: CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.user_info
    ADD CONSTRAINT user_info_pk PRIMARY KEY (username);


--
-- TOC entry 4064 (class 1259 OID 14888246)
-- Name: fk1631o3hvb3y9ktuxuusnx72v7; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk1631o3hvb3y9ktuxuusnx72v7 ON alpha_sound.album_song USING btree (song_id);


--
-- TOC entry 4065 (class 1259 OID 14888247)
-- Name: fk1m0sexh6p6kk409ptssu2kkgy; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk1m0sexh6p6kk409ptssu2kkgy ON alpha_sound.album_song USING btree (album_id);


--
-- TOC entry 4097 (class 1259 OID 14888248)
-- Name: fk1ssu87dg5vsdxpmyjqqc42if3; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk1ssu87dg5vsdxpmyjqqc42if3 ON alpha_sound.song_genre USING btree (song_id);


--
-- TOC entry 4105 (class 1259 OID 14888249)
-- Name: fk2fem9acf5noopfsm8v4rems4n; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk2fem9acf5noopfsm8v4rems4n ON alpha_sound.song_tag USING btree (song_id);


--
-- TOC entry 4056 (class 1259 OID 14888250)
-- Name: fk5o0o77w1ed46h3ggftpo7c9kl; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk5o0o77w1ed46h3ggftpo7c9kl ON alpha_sound.album_artist USING btree (artist_id);


--
-- TOC entry 4051 (class 1259 OID 14888251)
-- Name: fk5ydwe9p6gubp88i5in66kqwfh; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk5ydwe9p6gubp88i5in66kqwfh ON alpha_sound.album USING btree (country_id);


--
-- TOC entry 4087 (class 1259 OID 14888252)
-- Name: fk7ycpme9rfjfeyo1wxoyal3slc; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk7ycpme9rfjfeyo1wxoyal3slc ON alpha_sound.song USING btree (country_id);


--
-- TOC entry 4074 (class 1259 OID 14888253)
-- Name: fk8j7542fv0p9irs636f0aahnx9; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk8j7542fv0p9irs636f0aahnx9 ON alpha_sound.comment USING btree (username);


--
-- TOC entry 4083 (class 1259 OID 14888254)
-- Name: fk8l4jevlmxwsdm3ppymxm56gh2; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk8l4jevlmxwsdm3ppymxm56gh2 ON alpha_sound.playlist_song USING btree (song_id);


--
-- TOC entry 4093 (class 1259 OID 14888255)
-- Name: fk9tevojs24wnwin3di24wlao1m; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fk9tevojs24wnwin3di24wlao1m ON alpha_sound.song_artist USING btree (artist_id);


--
-- TOC entry 4106 (class 1259 OID 14888256)
-- Name: fka1eytrxtcxr33uwmvhjjmp437; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fka1eytrxtcxr33uwmvhjjmp437 ON alpha_sound.song_tag USING btree (tag_id);


--
-- TOC entry 4094 (class 1259 OID 14888257)
-- Name: fka29cre1dfpdj3gek88ukv43cc; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fka29cre1dfpdj3gek88ukv43cc ON alpha_sound.song_artist USING btree (song_id);


--
-- TOC entry 4075 (class 1259 OID 14888259)
-- Name: fkbkwibkxkhbevo3yg3aoxh3vmy; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkbkwibkxkhbevo3yg3aoxh3vmy ON alpha_sound.comment USING btree (entity_id);


--
-- TOC entry 4057 (class 1259 OID 14888260)
-- Name: fkewu7m144qnl94v79vwwpb47cd; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkewu7m144qnl94v79vwwpb47cd ON alpha_sound.album_artist USING btree (album_id);


--
-- TOC entry 4088 (class 1259 OID 14888261)
-- Name: fkg41bgb9x6cjug17kwj2acm2el; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkg41bgb9x6cjug17kwj2acm2el ON alpha_sound.song USING btree (username);


--
-- TOC entry 4101 (class 1259 OID 14888262)
-- Name: fkg4r860h9a7k6bnr4c8b8r3i6c; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkg4r860h9a7k6bnr4c8b8r3i6c ON alpha_sound.song_rating USING btree (user_id);


--
-- TOC entry 4068 (class 1259 OID 14888264)
-- Name: fkgjctiwu03i0a3a7go4918wpcx; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkgjctiwu03i0a3a7go4918wpcx ON alpha_sound.album_tag USING btree (tag_id);


--
-- TOC entry 4060 (class 1259 OID 14888265)
-- Name: fkgobybiodeygwcmlhr7sxv4y01; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkgobybiodeygwcmlhr7sxv4y01 ON alpha_sound.album_genre USING btree (album_id);


--
-- TOC entry 4052 (class 1259 OID 14888267)
-- Name: fkgv82ykgkamtep4itrf0pf0kvb; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkgv82ykgkamtep4itrf0pf0kvb ON alpha_sound.album USING btree (username);


--
-- TOC entry 4069 (class 1259 OID 14888269)
-- Name: fkhxc7yyi1ie9enecuwufacap81; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkhxc7yyi1ie9enecuwufacap81 ON alpha_sound.album_tag USING btree (album_id);


--
-- TOC entry 4084 (class 1259 OID 14888271)
-- Name: fkji5gt6i2hcwyt9x1fcfndclva; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkji5gt6i2hcwyt9x1fcfndclva ON alpha_sound.playlist_song USING btree (playlist_id);


--
-- TOC entry 4102 (class 1259 OID 14888274)
-- Name: fkl3kwwyc9bmy8889uok85nokmh; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkl3kwwyc9bmy8889uok85nokmh ON alpha_sound.song_rating USING btree (song_id);


--
-- TOC entry 4061 (class 1259 OID 14888275)
-- Name: fkld60pu9t8ff70bc6nrnnv91lx; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkld60pu9t8ff70bc6nrnnv91lx ON alpha_sound.album_genre USING btree (genre_id);


--
-- TOC entry 4098 (class 1259 OID 14888278)
-- Name: fkmpuht870e976moxtxywrfngcr; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkmpuht870e976moxtxywrfngcr ON alpha_sound.song_genre USING btree (genre_id);


--
-- TOC entry 4089 (class 1259 OID 14888279)
-- Name: fknbbebhw7u4atg5c8rsn3ix75v; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fknbbebhw7u4atg5c8rsn3ix75v ON alpha_sound.song USING btree (theme_id);


--
-- TOC entry 4053 (class 1259 OID 14888281)
-- Name: fkr56b8aquyqy9hy70cpcucgfiw; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkr56b8aquyqy9hy70cpcucgfiw ON alpha_sound.album USING btree (theme_id);


--
-- TOC entry 4090 (class 1259 OID 14888282)
-- Name: fkrcjmk41yqj3pl3iyii40niab0; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fkrcjmk41yqj3pl3iyii40niab0 ON alpha_sound.song USING btree (album_id);


--
-- TOC entry 4080 (class 1259 OID 14888283)
-- Name: fks9g3vkulofqy0h06eslr9upan; Type: INDEX; Schema: alpha_sound; Owner: -
--

CREATE INDEX fks9g3vkulofqy0h06eslr9upan ON alpha_sound.playlist USING btree (username);


--
-- TOC entry 4117 (class 2606 OID 14888284)
-- Name: album album_user_info_username_fk; Type: FK CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.album
    ADD CONSTRAINT album_user_info_username_fk FOREIGN KEY (username) REFERENCES alpha_sound.user_info(username);


--
-- TOC entry 4119 (class 2606 OID 14888289)
-- Name: comment comment_user_info_username_fk; Type: FK CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.comment
    ADD CONSTRAINT comment_user_info_username_fk FOREIGN KEY (username) REFERENCES alpha_sound.user_info(username);


--
-- TOC entry 4118 (class 2606 OID 14888294)
-- Name: album fk5ydwe9p6gubp88i5in66kqwfh; Type: FK CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.album
    ADD CONSTRAINT fk5ydwe9p6gubp88i5in66kqwfh FOREIGN KEY (country_id) REFERENCES alpha_sound.country(id);


--
-- TOC entry 4120 (class 2606 OID 14888299)
-- Name: song fk7ycpme9rfjfeyo1wxoyal3slc; Type: FK CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.song
    ADD CONSTRAINT fk7ycpme9rfjfeyo1wxoyal3slc FOREIGN KEY (country_id) REFERENCES alpha_sound.country(id);


--
-- TOC entry 4121 (class 2606 OID 14888304)
-- Name: song fknbbebhw7u4atg5c8rsn3ix75v; Type: FK CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.song
    ADD CONSTRAINT fknbbebhw7u4atg5c8rsn3ix75v FOREIGN KEY (theme_id) REFERENCES alpha_sound.theme(id);


--
-- TOC entry 4122 (class 2606 OID 14888309)
-- Name: song song_user_info_username_fk; Type: FK CONSTRAINT; Schema: alpha_sound; Owner: -
--

ALTER TABLE ONLY alpha_sound.song
    ADD CONSTRAINT song_user_info_username_fk FOREIGN KEY (username) REFERENCES alpha_sound.user_info(username);


-- Completed on 2022-09-27 22:49:38

--
-- PostgreSQL database dump complete
--

