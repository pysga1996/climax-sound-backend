package com.alpha.repositories.impl;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.AlbumDTO.AlbumAdditionalInfoDTO;
import com.alpha.model.dto.AlbumSearchDTO;
import com.alpha.model.dto.AlbumUpdateDTO;
import com.alpha.model.dto.AlbumUpdateDTO.UpdateMode;
import com.alpha.model.dto.CountryDTO;
import com.alpha.model.dto.GenreDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.dto.ThemeDTO;
import com.alpha.repositories.AlbumRepositoryCustom;
import com.alpha.repositories.BaseRepository;
import com.alpha.service.StorageService;
import com.alpha.util.helper.DataTypeComparer;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
 * @created 16/08/2021 - 10:45 CH
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Getter
@Repository
public class AlbumRepositoryImpl extends BaseRepository implements AlbumRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final StorageService storageService;

    @Autowired
    public AlbumRepositoryImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    public Page<AlbumDTO> extractResult(ResultSet rs, Pageable pageable) throws SQLException {
        long rownum;
        Long albumId = null;
        Long artistId = null;
        AlbumDTO album = null;
        long total = 0;
        List<AlbumDTO> albumList = new ArrayList<>();
        while (rs.next()) {
            rownum = rs.getLong("rn");
            Long currentAlbumId = rs.getLong("album_id");
            Long currentArtistId = rs.getLong("artist_id");
            if (!DataTypeComparer.equal(currentAlbumId, albumId)) {
                albumId = currentAlbumId;
                album = new AlbumDTO();
                album.setRn(rownum);
                album.setId(albumId);
                album.setTitle(rs.getString("album_title"));
                album.setUnaccentTitle(rs.getString("album_unaccent_title"));
                album.setListeningFrequency(rs.getLong("listening_frequency"));
                album.setDuration(Duration.ofSeconds(rs.getShort("duration")));
                album.setCoverUrl(rs.getString("cover_url"));
                album.setArtists(new ArrayList<>());
                artistId = this
                    .addArtist(artistId, currentArtistId, false, album, rs);
                albumList.add(album);
            } else {
                artistId = this
                    .addArtist(artistId, currentArtistId, true, album, rs);
            }
        }
        return new PageImpl<>(albumList, pageable, total);
    }

    private AlbumAdditionalInfoDTO extractResult(ResultSet rs) throws SQLException {
        AlbumAdditionalInfoDTO albumAdditionalInfoDTO = new AlbumAdditionalInfoDTO();
        albumAdditionalInfoDTO.setGenres(new ArrayList<>());
        albumAdditionalInfoDTO.setTags(new ArrayList<>());
        String type;
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            type = rs.getString("type");
            switch (type) {
                case "1_album":
                    int countryId = rs.getInt("country_id");
                    if (countryId == 0) {
                        return null;
                    }
                    CountryDTO countryDTO = new CountryDTO();
                    countryDTO.setId(countryId);
                    countryDTO.setName(rs.getString("country_name"));
                    albumAdditionalInfoDTO.setCountry(countryDTO);
                    int themeId = rs.getInt("theme_id");
                    if (themeId != 0) {
                        ThemeDTO themeDTO = new ThemeDTO();
                        themeDTO.setId(themeId);
                        themeDTO.setName(rs.getString("theme_name"));
                        albumAdditionalInfoDTO.setTheme(themeDTO);
                    }
                    albumAdditionalInfoDTO.setDescription(rs.getString("description"));
                    break;
                case "2_genre":
                    int genreId = rs.getInt("id");
                    if (genreId != 0) {
                        GenreDTO genreDTO = new GenreDTO();
                        genreDTO.setId(genreId);
                        genreDTO.setName(rs.getString("title"));
                        albumAdditionalInfoDTO.getGenres().add(genreDTO);
                    }
                    break;
                case "3_tag":
                    long tagId= rs.getLong("id");
                    if (tagId != 0) {
                        TagDTO tagDTO = new TagDTO();
                        tagDTO.setId(tagId);
                        tagDTO.setName(rs.getString("title"));
                        albumAdditionalInfoDTO.getTags().add(tagDTO);
                    }
                    break;
            }
        }
        if (rowCount == 0) {
            return null;
        }
        return albumAdditionalInfoDTO;
    }

    @Override
    public Page<AlbumDTO> findAllByConditions(Pageable pageable,
        AlbumSearchDTO albumSearchDTO) {
        Session session = entityManager.unwrap(Session.class);
        ResultSet rs = session.doReturningWork(connection -> {
            try (CallableStatement function = connection
                .prepareCall(
                    "{ ? = call find_album_by_conditions(?,?,?,?,?,?,?,?,?,?) }")) {
                function.registerOutParameter(1, Types.REF_CURSOR);
                function.setString(2, this.storageService.getBaseUrl()); // p_base_url
                function
                    .setString(3, this.storageService.getStorageType().name()); // p_storage_type
                if (albumSearchDTO.getArtistId() == null) {
                    function.setNull(4, Types.NUMERIC); // p_artist_id
                } else {
                    function.setLong(4, albumSearchDTO.getArtistId());
                }
                if (albumSearchDTO.getAlbumId() == null) {
                    function.setNull(5, Types.NUMERIC); // p_album_id
                } else {
                    function.setLong(5, albumSearchDTO.getAlbumId());
                }
                if (albumSearchDTO.getUsernameFavorite() == null) {
                    function.setNull(6, Types.VARCHAR); // p_username_fav
                } else {
                    function.setString(6, albumSearchDTO.getUsername());
                }
                if (albumSearchDTO.getUsername() == null) {
                    function.setNull(7, Types.VARCHAR); // p_username
                } else {
                    function.setString(7, albumSearchDTO.getUsername());
                }
                if (albumSearchDTO.getPhrase() == null) {
                    function.setNull(8, Types.VARCHAR); // p_phrase
                } else {
                    function.setString(8, albumSearchDTO.getPhrase());
                }
                function.setInt(9, pageable.getPageSize()); // p_size
                function.setInt(10, pageable.getPageNumber()); // p_page
                if (pageable.getSort().getOrderFor("listening_frequency") != null) {
                    function.setString(11, "listening_frequency");
                } else if (pageable.getSort().getOrderFor("release_date") != null) {
                    function.setString(11, "release_date");
                } else {
                    function.setString(11, "");
                }
                function.execute();
                return function.getObject(1, ResultSet.class);
            }
        });
        try {
            return this.extractResult(rs, pageable);
        } catch (SQLException throwable) {
            log.error(throwable);
            throw new RuntimeException(throwable);
        }
    }
    
    @Override
    @SneakyThrows
    public AlbumAdditionalInfoDTO findAdditionalInfo(Long id) {
        Session session = entityManager.unwrap(Session.class);
        ResultSet rs = session.doReturningWork(connection -> {
            try (CallableStatement function = connection
                .prepareCall(
                    "{ ? = call find_album_additional_info(?) }")) {
                function.registerOutParameter(1, Types.REF_CURSOR);
                function.setLong(2, id); // p_song_id
                function.execute();
                return function.getObject(1, ResultSet.class);
            }
        });
        return this.extractResult(rs);
    }

    @Override
    public void updateSongList(Long albumId, List<AlbumUpdateDTO> albumUpdateDTOList) {
        List<AlbumUpdateDTO> insertRelList = albumUpdateDTOList.stream()
            .filter(e -> e.getMode() == UpdateMode.CREATE).collect(
                Collectors.toList());
        List<AlbumUpdateDTO> deleteRelList = albumUpdateDTOList.stream()
            .filter(e -> e.getMode() == UpdateMode.DELETE).collect(
                Collectors.toList());
        String sqlInsert = "INSERT INTO album_song (album_id, song_id, \"order\")\n"
            + "VALUES (?, ?, ?)\n"
            + "    ON CONFLICT ON CONSTRAINT album_song_pk\n"
            + "    DO NOTHING";
        this.batchInsertUpdateDelete(sqlInsert, insertRelList, ((statement, element, index) -> {
            statement.setLong(1, albumId);
            statement.setLong(2, element.getSongId());
            statement.setLong(3, element.getOrder());
        }));
        String sqlDelete = "DELETE FROM album_song WHERE album_id = ? AND song_id = ?";
        this.batchInsertUpdateDelete(sqlDelete, deleteRelList, ((statement, element, index) -> {
            statement.setLong(1, albumId);
            statement.setLong(2, element.getSongId());
        }));
    }

}
