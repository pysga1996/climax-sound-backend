package com.alpha.repositories.impl;

import com.alpha.model.dto.CountryDTO;
import com.alpha.model.dto.GenreDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongDTO.SongAdditionalInfoDTO;
import com.alpha.model.dto.SongSearchDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.dto.ThemeDTO;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.repositories.BaseRepository;
import com.alpha.repositories.SongRepositoryCustom;
import com.alpha.service.StorageService;
import com.alpha.util.helper.DataTypeComparer;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 10/06/2021 - 10:28 CH
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Getter
@Repository
public class SongRepositoryImpl extends BaseRepository implements SongRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final StorageService storageService;

    @Autowired
    public SongRepositoryImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    public Page<SongDTO> extractResult(ResultSet rs, Pageable pageable) throws SQLException {
        long rownum;
        Long songId = null;
        Long artistId = null;
        long duration;
        SongDTO song = null;
        long total = 0;
        List<SongDTO> songList = new ArrayList<>();
        while (rs.next()) {
            total = rs.getLong("total");
            rownum = rs.getLong("rn");
            Long currentSongId = rs.getLong("song_id");
            Long currentArtistId = rs.getLong("artist_id");
            if (!DataTypeComparer.equal(currentSongId, songId)) {
                songId = currentSongId;
                song = new SongDTO();
                song.setRn(rownum);
                song.setId(songId);
                song.setTitle(rs.getString("song_title"));
                song.setUnaccentTitle(rs.getString("song_unaccent_title"));
                duration = rs.getLong("duration");
                song.setDuration(Duration.ofSeconds(duration));
                song.setListeningFrequency(
                    (rs.getLong("listening_frequency")));
                song.setLikeCount(rs.getLong("like_count"));
                song.setReleaseDate(rs.getDate("release_date"));
                song.setUrl(rs.getString("url"));
                song.setArtists(new ArrayList<>());
                song.setUploader(UserInfoDTO.builder().username(rs.getString("username")).build());
                artistId = this
                    .addArtist(artistId, currentArtistId, false, song, rs);
                songList.add(song);
            } else {
                artistId = this
                    .addArtist(artistId, currentArtistId, true, song, rs);
            }
        }
        return new PageImpl<>(songList, pageable, total);
    }

    private SongAdditionalInfoDTO extractResult(ResultSet rs) throws SQLException {
        SongAdditionalInfoDTO songAdditionalInfoDTO = new SongAdditionalInfoDTO();
        songAdditionalInfoDTO.setGenres(new ArrayList<>());
        songAdditionalInfoDTO.setTags(new ArrayList<>());
        String type;
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            type = rs.getString("type");
            switch (type) {
                case "1_song":
                    int countryId = rs.getInt("country_id");
                    if (countryId == 0) {
                        return null;
                    }
                    CountryDTO countryDTO = new CountryDTO();
                    countryDTO.setId(countryId);
                    countryDTO.setName(rs.getString("country_name"));
                    songAdditionalInfoDTO.setCountry(countryDTO);
                    int themeId = rs.getInt("theme_id");
                    if (themeId != 0) {
                        ThemeDTO themeDTO = new ThemeDTO();
                        themeDTO.setId(themeId);
                        themeDTO.setName(rs.getString("theme_name"));
                        songAdditionalInfoDTO.setTheme(themeDTO);
                    }
                    songAdditionalInfoDTO.setLyric(rs.getString("lyric"));
                    break;
                case "2_genre":
                    int genreId = rs.getInt("id");
                    if (genreId != 0) {
                        GenreDTO genreDTO = new GenreDTO();
                        genreDTO.setId(genreId);
                        genreDTO.setName(rs.getString("title"));
                        songAdditionalInfoDTO.getGenres().add(genreDTO);
                    }
                    break;
                case "3_tag":
                    long tagId= rs.getLong("id");
                    if (tagId != 0) {
                        TagDTO tagDTO = new TagDTO();
                        tagDTO.setId(tagId);
                        tagDTO.setName(rs.getString("title"));
                        songAdditionalInfoDTO.getTags().add(tagDTO);
                    }
                    break;
            }
        }
        if (rowCount == 0) return null;
        return songAdditionalInfoDTO;
    }

    @Override
    @SneakyThrows
    public Page<SongDTO> findAllConditions(Pageable pageable, SongSearchDTO songSearchDTO) {
        Session session = entityManager.unwrap(Session.class);
        ResultSet rs = session.doReturningWork(connection -> {
            try (CallableStatement function = connection
                .prepareCall(
                    "{ ? = call find_song_by_conditions(?,?,?,?,?,?,?,?,?,?,?) }")) {
                function.registerOutParameter(1, Types.REF_CURSOR);
                function.setString(2, this.storageService.getBaseUrl()); // p_base_url
                function
                    .setString(3, this.storageService.getStorageType().name()); // p_storage_type
                if (songSearchDTO.getArtistId() == null) {
                    function.setNull(4, Types.NUMERIC); // p_artist_id
                } else {
                    function.setLong(4, songSearchDTO.getArtistId());
                }
                if (songSearchDTO.getAlbumId() == null) {
                    function.setNull(5, Types.NUMERIC); // p_album_id
                } else {
                    function.setLong(5, songSearchDTO.getAlbumId());
                }
                if (songSearchDTO.getPlaylistId() == null) {
                    function.setNull(6, Types.NUMERIC); // p_playlist_id
                } else {
                    function.setLong(6, songSearchDTO.getAlbumId());
                }
                if (songSearchDTO.getUsernameFavorite() == null) {
                    function.setNull(7, Types.VARCHAR); // p_username_fav
                } else {
                    function.setString(7, songSearchDTO.getUsername());
                }
                if (songSearchDTO.getUsername() == null) {
                    function.setNull(8, Types.VARCHAR); // p_username
                } else {
                    function.setString(8, songSearchDTO.getUsername());
                }
                if (songSearchDTO.getPhrase() == null) {
                    function.setNull(9, Types.VARCHAR); // p_phrase
                } else {
                    function.setString(9, songSearchDTO.getPhrase());
                }
                function.setInt(10, pageable.getPageSize()); // p_size
                function.setInt(11, pageable.getPageNumber()); // p_page
                if (pageable.getSort().getOrderFor("listening_frequency") != null) {
                    function.setString(12, "listening_frequency");
                } else if (pageable.getSort().getOrderFor("release_date") != null) {
                    function.setString(12, "release_date");
                } else {
                    function.setString(12, "");
                }
                function.execute();
                return function.getObject(1, ResultSet.class);
            }
        });
        return this.extractResult(rs, pageable);
    }

    @Override
    public SongAdditionalInfoDTO findAdditionalInfo(Long id) {
        Session session = entityManager.unwrap(Session.class);
        ResultSet rs = session.doReturningWork(connection -> {
            try (CallableStatement function = connection
                .prepareCall(
                    "{ ? = call find_song_additional_info(?) }")) {
                function.registerOutParameter(1, Types.REF_CURSOR);
                function.setLong(2, id); // p_song_id
                function.execute();
                return function.getObject(1, ResultSet.class);
            }
        });
        try {
            return this.extractResult(rs);
        } catch (SQLException throwable) {
            log.error(throwable);
            throw new RuntimeException(throwable);
        }
    }
}
