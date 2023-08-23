CREATE SCHEMA alpha_sound;

delimiter $$
create procedure alpha_sound.delete_album(p_id bigint)
begin
    select * from album where id = p_id;
end $$
delimiter ;


delimiter $$
create procedure alpha_sound.find_album_additional_info(p_album_id numeric)
begin
    select a1.id,
           a1.title,
           '1_album'                                      as type,
           first_value(c.id) over (partition by a1.id)    as country_id,
           first_value(c.name) over (partition by a1.id)  as country_name,
           first_value(th.id) over (partition by a1.id)   as theme_id,
           first_value(th.name) over (partition by a1.id) as theme_name,
           a1.description
    from album a1
             left join country c on c.id = a1.country_id
             left join theme th on a1.theme_id = th.id
    where a1.id = p_album_id
    union
    (select g.id,
            g.name,
            '2_genre' as type,
            null,
            null,
            null,
            null,
            null
     from album a2
              left join album_genre ag on a2.id = ag.album_id
              left join genre g on ag.genre_id = g.id
     where a2.id = p_album_id)
    union
    (select t.id,
            t.name,
            '3_tag' as type,
            null,
            null,
            null,
            null,
            null
     from album a3
              left join album_tag at on a3.id = at.album_id
              left join tag t on at.tag_id = t.id
     where a3.id = p_album_id)
    order by type;
end $$
delimiter ;


delimiter $$
create procedure alpha_sound.find_album_by_conditions(p_base_url varchar(200),
                                                      p_storage_type varchar(200),
                                                      p_artist_id numeric,
                                                      p_album_id numeric,
                                                      p_username_fav varchar(200),
                                                      p_username varchar(200),
                                                      p_phrase varchar(200),
                                                      p_size numeric,
                                                      p_page numeric,
                                                      p_sort varchar(200))
begin
    declare page_begin numeric default 0;
    declare page_end numeric default 0;
    declare modified_phrase varchar(200);

    set p_base_url = ifnull(p_base_url, '');
    set p_storage_type = ifnull(p_storage_type, 'LOCAL');
    set p_size = ifnull(p_size, 10);
    set p_page = ifnull(p_page, 0);
    set page_begin = p_page * p_size;
    set page_end = p_page * p_size + p_size;
    set modified_phrase = concat('%', lower(p_phrase), '%');
    with ri_ref as (select distinct media_id,
                                    p_base_url ||
                                    (first_value(uri) over (partition by media_id)) as url
                    from resource_info ri
                    where ri.media_ref = 'ALBUM_COVER'
                      and ri.storage_type = p_storage_type
                      and ri.status = 1),
         ri_a_ref as (select distinct media_id,
                                      p_base_url ||
                                      (first_value(uri) over (partition by media_id)) as url
                      from resource_info ri
                      where ri.media_ref = 'ARTIST_AVATAR'
                        and ri.storage_type = p_storage_type
                        and ri.status = 1),
         s_ref as (select distinct alb_tmp.id,
                                   first_value(alb_tmp.title) over (partition by alb_tmp.id) as tmp_title,
                                   first_value(alb_tmp.listening_frequency)
                                               over (partition by alb_tmp.id)                as tmp_listening_frequency,
                                   first_value(alb_tmp.release_date)
                                               over (partition by alb_tmp.id)                as tmp_release_date
                   from album alb_tmp
                            left join album_artist aa_tmp on alb_tmp.id = aa_tmp.album_id
                            left join artist art_tmp on aa_tmp.artist_id = art_tmp.id
                            left join user_favorites ufa on alb_tmp.id = ufa.entity_id and ufa.type = 'album'
                   where (p_phrase is null or (lower(alb_tmp.title) like modified_phrase
                       or lower(alb_tmp.unaccent_title) like modified_phrase
                       or lower(art_tmp.name) like modified_phrase
                       or lower(art_tmp.unaccent_name) like modified_phrase))
                     and (p_artist_id is null or art_tmp.id = p_artist_id)
                     and (p_album_id is null or alb_tmp.id = p_album_id)
                     and (p_username_fav is null or ufa.username = p_username_fav)
                     and (p_username is null or alb_tmp.username = p_username)),
         rn_tmp as (select s_ref.id,
                           count(*) over () as total,
                           row_number() over (order by
                               (case
                                    when p_sort = 'listening_frequency'
                                        then tmp_listening_frequency end) desc,
                               (case when p_sort = 'release_date' then tmp_release_date end) desc,
                               tmp_title)   as rn
                    from s_ref),
         result as (select rn_tmp.total,
                           rn_tmp.rn,
                           alb.id             as album_id,
                           alb.title          as album_title,
                           alb.unaccent_title as album_unaccent_title,
                           alb.duration,
                           alb.listening_frequency,
                           alb.like_count,
                           alb.release_date,
                           alb.username,
                           ri_ref.url         as cover_url,
                           art.id             as artist_id,
                           art.name           as artist_name,
                           art.unaccent_name  as artist_unaccent_name,
                           ri_a_ref.url       as artist_avatar
                    from album alb
                             inner join rn_tmp on alb.id = rn_tmp.id
                             left join ri_ref on ri_ref.media_id = rn_tmp.id
                             left join album_artist albart on rn_tmp.id = albart.album_id
                             left join artist art on albart.artist_id = art.id
                             left join ri_a_ref on alb.id = ri_a_ref.media_id)
    select *
    from result
    where rn > page_begin
      and rn <= page_end
    order by rn;
end $$
delimiter ;


delimiter $$
create procedure alpha_sound.find_artist_by_conditions(p_base_url varchar(200),
                                                       p_storage_type varchar(200),
                                                       p_song_id numeric,
                                                       p_album_id numeric,
                                                       p_username_fav varchar(200),
                                                       p_username varchar(200),
                                                       p_phrase varchar(200),
                                                       p_size numeric,
                                                       p_page numeric,
                                                       p_sort varchar(200))
begin
    declare page_begin numeric default 0;
    declare page_end numeric default 0;
    declare modified_phrase varchar(200);

    set p_base_url = ifnull(p_base_url, '');
    set p_storage_type = ifnull(p_storage_type, 'LOCAL');
    set p_size = ifnull(p_size, 10);
    set p_page = ifnull(p_page, 0);
    set page_begin = p_page * p_size;
    set page_end = p_page * p_size + p_size;
    set modified_phrase = concat('%', lower(p_phrase), '%');
    with ri_ref as (select distinct media_id,
                                    p_base_url ||
                                    (first_value(uri) over (partition by media_id)) as url
                    from resource_info ri
                    where ri.media_ref = 'ARTIST_AVATAR'
                      and ri.storage_type = p_storage_type
                      and ri.status = 1),
         s_ref as (select distinct art_tmp.id,
                                   first_value(art_tmp.name) over (partition by art_tmp.id) as tmp_name,
                                   first_value(art_tmp.like_count)
                                               over (partition by art_tmp.id)               as tmp_like_count,
                                   first_value(art_tmp.birth_date)
                                               over (partition by art_tmp.id)               as tmp_birth_date,
                                   first_value(aa_tmp.`order`)
                                               over (partition by art_tmp.id)               as album_order,
                                   first_value(sa_tmp.`order`)
                                               over (partition by art_tmp.id)               as song_order
                   from artist art_tmp
                            left join album_artist aa_tmp on art_tmp.id = aa_tmp.artist_id
                            left join song_artist sa_tmp on art_tmp.id = sa_tmp.artist_id
                            left join user_favorites ufa on art_tmp.id = ufa.entity_id and ufa.type = 'ARTIST'
                   where ((p_phrase is null or
                           (lower(art_tmp.name) like modified_phrase or
                            lower(art_tmp.unaccent_name) like modified_phrase))
                       and (p_song_id is null or sa_tmp.song_id = p_song_id)
                       and (p_album_id is null or aa_tmp.album_id = p_album_id)
                       and (p_username_fav is null or ufa.username = p_username_fav)
                       and (p_username is null or art_tmp.username = p_username)
                             )),
         rn_tmp as (select s_ref.id,
                           count(*) over () as total,
                           row_number() over (order by
                               (case when p_album_id is not null then album_order end),
                               (case when p_song_id is not null then song_order end),
                               (case
                                    when p_sort = 'like_count'
                                        then tmp_birth_date end) desc,
                               (case when p_sort = 'birth_date' then tmp_birth_date end) desc,
                               tmp_name
                               )            as rn
                    from s_ref),
         result as (select rn_tmp.total,
                           rn_tmp.rn,
                           art.id            as artist_id,
                           art.name          as artist_name,
                           art.unaccent_name as artist_unaccent_name,
                           art.birth_date    as birth_date,
                           art.like_count    as like_count,
                           ri_ref.url        as avatar_url
                    from artist art
                             inner join rn_tmp on art.id = rn_tmp.id
                             left join ri_ref on ri_ref.media_id = rn_tmp.id)
    select *
    from result
    where rn > page_begin
      and rn <= page_end
    order by rn;
end $$
delimiter ;


delimiter $$
create procedure alpha_sound.find_song_additional_info(p_song_id numeric)
begin
    select s.id,
           s.title,
           '1_song'                                      as type,
           first_value(c.id) over (partition by s.id)    as country_id,
           first_value(c.name) over (partition by s.id)  as country_name,
           first_value(th.id) over (partition by s.id)   as theme_id,
           first_value(th.name) over (partition by s.id) as theme_name,
           s.lyric
    from song s
             left join country c on c.id = s.country_id
             left join theme th on s.theme_id = th.id
    where s.id = p_song_id
    union
    (select g.id,
            g.name,
            '2_genre' as type,
            null,
            null,
            null,
            null,
            null
     from song s2
              left join song_genre sg on s2.id = sg.song_id
              left join genre g on sg.genre_id = g.id
     where s2.id = p_song_id)
    union
    (select t.id,
            t.name,
            '3_tag' as type,
            null,
            null,
            null,
            null,
            null
     from song s3
              left join song_tag st on s3.id = st.song_id
              left join tag t on st.tag_id = t.id
     where s3.id = p_song_id)
    order by type;
end $$
delimiter ;


delimiter $$
create procedure alpha_sound.find_song_by_conditions(p_base_url varchar(200),
                                                     p_storage_type varchar(200),
                                                     p_artist_id numeric,
                                                     p_album_id numeric,
                                                     p_playlist_id numeric,
                                                     p_username_fav varchar(200),
                                                     p_username varchar(200),
                                                     p_phrase varchar(200),
                                                     p_size numeric,
                                                     p_page numeric,
                                                     p_sort varchar(200))
begin
    declare page_begin numeric default 0;
    declare page_end numeric default 0;
    declare modified_phrase varchar(200);

    set p_base_url = ifnull(p_base_url, '');
    set p_storage_type = ifnull(p_storage_type, 'LOCAL');
    set p_size = ifnull(p_size, 10);
    set p_page = ifnull(p_page, 0);
    set page_begin = p_page * p_size;
    set page_end = p_page * p_size + p_size;
    set modified_phrase = concat('%', lower(p_phrase), '%');
    with ri_ref as (select distinct media_id,
                                    p_base_url ||
                                    (first_value(uri) over (partition by media_id)) as url
                    from resource_info ri
                    where ri.media_ref = 'SONG_AUDIO'
                      and ri.storage_type = p_storage_type
                      and ri.status = 1),
         ri_a_ref as (select distinct media_id,
                                      p_base_url ||
                                      (first_value(uri) over (partition by media_id)) as url
                      from resource_info ri
                      where ri.media_ref = 'ARTIST_AVATAR'
                        and ri.storage_type = p_storage_type
                        and ri.status = 1),
         s_ref as (select distinct s_tmp.id,
                                   first_value(s_tmp.title) over (partition by s_tmp.id) as tmp_title,
                                   first_value(s_tmp.listening_frequency)
                                               over (partition by s_tmp.id)              as tmp_listening_frequency,
                                   first_value(s_tmp.release_date)
                                               over (partition by s_tmp.id)              as tmp_release_date,
                                   first_value(as_tmp.ordinal_number)
                                               over (partition by s_tmp.id)              as album_order
                   from song s_tmp
                            left join album_song as_tmp on s_tmp.id = as_tmp.song_id
                            left join song_artist sa_tmp on s_tmp.id = sa_tmp.song_id
                            left join artist a_tmp on sa_tmp.artist_id = a_tmp.id
                            left join playlist_song p_tmp on s_tmp.id = p_tmp.song_id
                            left join user_favorites ufs on s_tmp.id = ufs.entity_id and ufs.type = 'SONG'
                   where (p_phrase is null or (lower(s_tmp.title) like modified_phrase
                       or lower(s_tmp.unaccent_title) like modified_phrase
                       or lower(a_tmp.name) like modified_phrase
                       or lower(a_tmp.unaccent_name) like modified_phrase))
                     and (p_artist_id is null or a_tmp.id = p_artist_id)
                     and (p_album_id is null or as_tmp.album_id = p_album_id)
                     and (p_playlist_id is null or p_tmp.playlist_id = p_playlist_id)
                     and (p_username_fav is null or ufs.username = p_username_fav)
                     and (p_username is null or s_tmp.username = p_username)),
         rn_tmp as (select s_ref.id,
                           count(*) over ()    as total,
                           row_number() over (order by
                               (case when p_album_id is not null then album_order end),
                               (case
                                    when p_sort = 'listening_frequency'
                                        then tmp_listening_frequency end) desc,
                               (case when p_sort = 'release_date' then tmp_release_date end) desc,
                               tmp_title asc ) as rn
                    from s_ref),
         result as (select rn_tmp.total,
                           rn_tmp.rn,
                           s.id             as song_id,
                           s.title          as song_title,
                           s.unaccent_title as song_unaccent_title,
                           s.duration,
                           s.listening_frequency,
                           s.like_count,
                           s.release_date,
                           s.username,
                           ri_ref.url       as url,
                           a.id             as artist_id,
                           a.name           as artist_name,
                           a.unaccent_name  as artist_unaccent_name,
                           ri_a_ref.url     as artist_avatar
                    from song s
                             inner join rn_tmp on s.id = rn_tmp.id
                             left join ri_ref on ri_ref.media_id = rn_tmp.id
                             left join song_artist sa on rn_tmp.id = sa.song_id
                             left join artist a on sa.artist_id = a.id
                             left join ri_a_ref on a.id = ri_a_ref.media_id)
    select *
    from result
    where rn > page_begin
      and rn <= page_end
    order by rn;
end $$
delimiter ;


delimiter $$
create procedure alpha_sound.update_album_song_list(p_album_id numeric,
                                                    p_song_id numeric,
                                                    p_ordinal_number numeric,
                                                    p_mode varchar(200))
begin
    if (p_mode = 'CREATE' or p_mode = 'VIEW') then
        insert into album_song(album_id, ordinal_number, song_id)
        values (p_album_id, p_ordinal_number, p_song_id)
        on duplicate key update ordinal_number = p_ordinal_number;
    elseif p_mode = 'UPDATE' then
        update album_song
        set ordinal_number = p_ordinal_number
        where album_song.album_id = p_album_id
          and album_song.song_id = p_song_id;
    elseif p_mode = 'DELETE' then
        delete from album_song where album_song.album_id = p_album_id and album_song.song_id = p_song_id;
    end if;
end $$
delimiter ;


create table album_artist
(
    album_id  bigint   not null,
    artist_id bigint   not null,
    `order`   smallint null,
    primary key (album_id, artist_id)
)
    collate = utf8mb3_bin;

create index fk5o0o77w1ed46h3ggftpo7c9kl
    on album_artist (artist_id);

create index fkewu7m144qnl94v79vwwpb47cd
    on album_artist (album_id);

create table album_genre
(
    album_id bigint not null,
    genre_id int    not null,
    primary key (album_id, genre_id)
)
    collate = utf8mb3_bin;

create index fkgobybiodeygwcmlhr7sxv4y01
    on album_genre (album_id);

create index fkld60pu9t8ff70bc6nrnnv91lx
    on album_genre (genre_id);

create table album_song
(
    album_id       bigint   not null,
    song_id        bigint   not null,
    ordinal_number smallint null,
    primary key (album_id, song_id)
)
    collate = utf8mb3_bin;

create index fk1631o3hvb3y9ktuxuusnx72v7
    on album_song (song_id);

create index fk1m0sexh6p6kk409ptssu2kkgy
    on album_song (album_id);

create table album_tag
(
    album_id bigint not null,
    tag_id   bigint not null,
    primary key (album_id, tag_id)
)
    collate = utf8mb3_bin;

create index fkgjctiwu03i0a3a7go4918wpcx
    on album_tag (tag_id);

create index fkhxc7yyi1ie9enecuwufacap81
    on album_tag (album_id);

create table artist
(
    id            bigint auto_increment
        primary key,
    biography     text             null,
    birth_date    timestamp        null,
    name          char(255)        null,
    unaccent_name char(255)        null,
    like_count    bigint default 0 null,
    username      varchar(255)     null,
    create_time   timestamp        null,
    update_time   timestamp        null,
    status        int    default 1 null,
    sync          int    default 0 null
)
    collate = utf8mb3_bin;

create table country
(
    id          int auto_increment
        primary key,
    name        char(255) not null,
    create_time timestamp null,
    update_time timestamp null,
    status      int       null
)
    collate = utf8mb3_bin;

create table genre
(
    id          int auto_increment
        primary key,
    name        char(255) not null,
    create_time timestamp null,
    update_time timestamp null,
    status      int       null
)
    collate = utf8mb3_bin;

create table playlist
(
    id          bigint auto_increment
        primary key,
    title       char(255)     null,
    username    char(50)      null,
    name        char(255)     null,
    status      int default 1 null,
    create_time timestamp     null,
    update_time timestamp     null
)
    collate = utf8mb3_bin;

create index fks9g3vkulofqy0h06eslr9upan
    on playlist (username);

create table playlist_song
(
    playlist_id bigint not null,
    song_id     bigint not null,
    primary key (playlist_id, song_id)
)
    collate = utf8mb3_bin;

create index fk8l4jevlmxwsdm3ppymxm56gh2
    on playlist_song (song_id);

create index fkji5gt6i2hcwyt9x1fcfndclva
    on playlist_song (playlist_id);

create table resource_info
(
    id           bigint,
    storage_type varchar(255)  null,
    storage_path varchar(255)  null,
    file_name    varchar(255)  null,
    extension    varchar(255)  null,
    media_type   varchar(255)  null,
    status       int default 1 null,
    uri          varchar(255)  null,
    folder       varchar(255)  null,
    media_id     bigint        null,
    media_ref    varchar(255)  null,
    username     varchar(255)  null
)
    collate = utf8mb3_bin;

create index resource_info_AutoIDX_id
    on resource_info (id);

alter table resource_info
    modify id bigint auto_increment;

create table song_artist
(
    song_id   bigint   not null,
    artist_id bigint   not null,
    `order`   smallint null,
    primary key (song_id, artist_id)
)
    collate = utf8mb3_bin;

create index fk9tevojs24wnwin3di24wlao1m
    on song_artist (artist_id);

create index fka29cre1dfpdj3gek88ukv43cc
    on song_artist (song_id);

create table song_genre
(
    song_id  bigint not null,
    genre_id int    not null,
    primary key (song_id, genre_id)
)
    collate = utf8mb3_bin;

create index fk1ssu87dg5vsdxpmyjqqc42if3
    on song_genre (song_id);

create index fkmpuht870e976moxtxywrfngcr
    on song_genre (genre_id);

create table song_rating
(
    id      bigint not null
        primary key,
    rating  int    null,
    song_id bigint null,
    user_id bigint null
)
    collate = utf8mb3_bin;

create index fkg4r860h9a7k6bnr4c8b8r3i6c
    on song_rating (user_id);

create index fkl3kwwyc9bmy8889uok85nokmh
    on song_rating (song_id);

create table song_tag
(
    song_id bigint not null,
    tag_id  bigint not null,
    primary key (song_id, tag_id)
)
    collate = utf8mb3_bin;

create index fk2fem9acf5noopfsm8v4rems4n
    on song_tag (song_id);

create index fka1eytrxtcxr33uwmvhjjmp437
    on song_tag (tag_id);

create table tag
(
    id          bigint auto_increment
        primary key,
    name        char(255)     not null,
    create_time timestamp     null,
    update_time timestamp     null,
    status      int default 1 null
)
    collate = utf8mb3_bin;

create table theme
(
    id          int auto_increment
        primary key,
    name        char(255)     not null,
    create_time timestamp     null,
    update_time timestamp     null,
    status      int default 1 null
)
    collate = utf8mb3_bin;

create table user_favorites
(
    username        varchar(255)         not null,
    entity_id       bigint               not null,
    type            varchar(255)         not null,
    status          int        default 1 null,
    liked           tinyint(1) default 0 null,
    listening_count bigint     default 0 null,
    constraint favorites_pk
        unique (username, entity_id, type)
)
    collate = utf8mb3_bin;

create table user_info
(
    profile     longtext not null,
    username    char(50)                  not null
        primary key,
    setting     longtext null,
    create_time timestamp                 null,
    update_time timestamp                 null,
    status      int          default 1    null
)
    collate = utf8mb3_bin;

create table album
(
    id                  bigint auto_increment
        primary key,
    release_date        timestamp          null,
    title               char(255)          null,
    country_id          int                null,
    theme_id            int                null,
    username            char(50)           null,
    unaccent_title      char(100)          null,
    listening_frequency bigint   default 0 null,
    duration            smallint default 0 null,
    like_count          bigint   default 0 null,
    description         text               null,
    create_time         timestamp          null,
    update_time         timestamp          null,
    status              int      default 1 null,
    sync                int      default 0 null,
    constraint album_user_info_username_fk
        foreign key (username) references user_info (username),
    constraint fk5ydwe9p6gubp88i5in66kqwfh
        foreign key (country_id) references country (id)
)
    collate = utf8mb3_bin;

create index fkgv82ykgkamtep4itrf0pf0kvb
    on album (username);

create index fkr56b8aquyqy9hy70cpcucgfiw
    on album (theme_id);

create table comment
(
    id          bigint auto_increment
        primary key,
    content     text          null,
    create_time timestamp     null,
    entity_id   bigint        null,
    username    char(50)      null,
    status      int default 1 null,
    entity_type varchar(255)  null,
    update_time timestamp     null,
    constraint comment_user_info_username_fk
        foreign key (username) references user_info (username)
)
    collate = utf8mb3_bin;

create index fk8j7542fv0p9irs636f0aahnx9
    on comment (username);

create index fkbkwibkxkhbevo3yg3aoxh3vmy
    on comment (entity_id);

create table song
(
    id                  bigint auto_increment
        primary key,
    display_rating      bigint default 0 null,
    duration            smallint         null,
    listening_frequency bigint default 0 null,
    lyric               text             null,
    release_date        timestamp        null,
    title               char(255)        null,
    unaccent_title      char(255)        null,
    country_id          int              null,
    theme_id            int              null,
    username            char(50)         null,
    name                char(255)        null,
    album_id            bigint           null,
    like_count          bigint default 0 null,
    create_time         timestamp        null,
    update_time         timestamp        null,
    status              int    default 1 null,
    sync                int    default 0 null,
    constraint fk7ycpme9rfjfeyo1wxoyal3slc
        foreign key (country_id) references country (id),
    constraint fknbbebhw7u4atg5c8rsn3ix75v
        foreign key (theme_id) references theme (id),
    constraint song_user_info_username_fk
        foreign key (username) references user_info (username)
)
    collate = utf8mb3_bin;


