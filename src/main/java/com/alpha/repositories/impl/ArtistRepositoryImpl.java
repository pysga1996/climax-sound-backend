package com.alpha.repositories.impl;

import com.alpha.constant.ModelStatus;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.ArtistSearchDTO;
import com.alpha.model.dto.UpdateSyncOption;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.Artist_;
import com.alpha.repositories.ArtistRepositoryCustom;
import com.alpha.repositories.BaseRepository;
import com.alpha.service.StorageService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author thanhvt
 * @created 27/08/2021 - 9:58 CH
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Getter
@Repository
public class ArtistRepositoryImpl extends BaseRepository implements ArtistRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final StorageService storageService;

    @Autowired
    public ArtistRepositoryImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    private Page<ArtistDTO> extractResult(ResultSet rs, Pageable pageable) throws SQLException {
        long rownum;
        long artistId;
        ArtistDTO artistDTO;
        long total = 0;
        List<ArtistDTO> artistDTOList = new ArrayList<>();
        while (rs.next()) {
            total = rs.getLong("total");
            rownum = rs.getLong("rn");
            artistId = rs.getLong("artist_id");
            artistDTO = ArtistDTO.builder()
                .id(artistId)
                .rn(rownum)
                .name(rs.getString("artist_name"))
                .unaccentName(rs.getString("artist_unaccent_name"))
                .birthDate(rs.getDate("birth_date"))
                .avatarUrl(rs.getString("avatar_url"))
                .build();
            artistDTOList.add(artistDTO);
        }
        return new PageImpl<>(artistDTOList, pageable, total);
    }

    @Override
    public Page<ArtistDTO> findByConditions(Pageable pageable,
        ArtistSearchDTO artistSearchDTO) {
        Session session = entityManager.unwrap(Session.class);
//        this code is for PostgresQL
//        ResultSet rs = session.doReturningWork(connection -> {
//            try (CallableStatement function = connection
//                .prepareCall(
//                    "{ ? = call find_artist_by_conditions(?,?,?,?,?,?,?,?,?,?) }")) {
//                function.registerOutParameter(1, Types.REF_CURSOR);
//                function.setString(2, ""); // p_base_url
//                function
//                    .setString(3, this.storageService.getStorageType().name()); // p_storage_type
//                if (artistSearchDTO.getSongId() == null) {
//                    function.setNull(4, Types.NUMERIC); // p_song_id
//                } else {
//                    function.setLong(4, artistSearchDTO.getSongId());
//                }
//                if (artistSearchDTO.getAlbumId() == null) {
//                    function.setNull(5, Types.NUMERIC); // p_album_id
//                } else {
//                    function.setLong(5, artistSearchDTO.getAlbumId());
//                }
//                if (artistSearchDTO.getUsernameFavorite() == null) {
//                    function.setNull(6, Types.VARCHAR); // p_username_fav
//                } else {
//                    function.setString(6, artistSearchDTO.getUsername());
//                }
//                if (artistSearchDTO.getUsername() == null) {
//                    function.setNull(7, Types.VARCHAR); // p_username
//                } else {
//                    function.setString(7, artistSearchDTO.getUsername());
//                }
//                if (artistSearchDTO.getPhrase() == null) {
//                    function.setNull(8, Types.VARCHAR); // p_phrase
//                } else {
//                    function.setString(8, artistSearchDTO.getPhrase());
//                }
//                function.setInt(9, pageable.getPageSize()); // p_size
//                function.setInt(10, pageable.getPageNumber()); // p_page
//                if (pageable.getSort().getOrderFor("like_count") != null) {
//                    function.setString(11, "like_count");
//                } else if (pageable.getSort().getOrderFor("birth_date") != null) {
//                    function.setString(11, "birth_date");
//                } else {
//                    function.setString(11, "");
//                }
//                function.execute();
//                return function.getObject(1, ResultSet.class);
//            }
//        });
        ResultSet rs = session.doReturningWork(connection -> {
            try (CallableStatement function = connection
                    .prepareCall(
                            "{ call find_artist_by_conditions(?,?,?,?,?,?,?,?,?,?) }")) {
                function.setString("p_base_url", ""); // p_base_url
                function
                        .setString("p_storage_type", this.storageService.getStorageType().name()); // p_storage_type
                function.setLong("p_song_id", artistSearchDTO.getSongId());
                function.setLong("p_album_id", artistSearchDTO.getAlbumId());
                function.setString("p_username_fav", artistSearchDTO.getUsername());
                function.setString("p_username", artistSearchDTO.getUsername());
                function.setString("p_phrase", artistSearchDTO.getPhrase());
                function.setInt("p_size", pageable.getPageSize()); // p_size
                function.setInt("p_page", pageable.getPageNumber()); // p_page
                if (pageable.getSort().getOrderFor("like_count") != null) {
                    function.setString("p_sort", "like_count");
                } else if (pageable.getSort().getOrderFor("birth_date") != null) {
                    function.setString("p_sort", "birth_date");
                } else {
                    function.setString("p_sort", "");
                }
                return function.executeQuery();
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
    public int markForSync(UpdateSyncOption option) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaUpdate<Artist> criteriaUpdate = cb.createCriteriaUpdate(Artist.class);
        Root<Artist> root =  criteriaUpdate.from(Artist.class);
        criteriaUpdate
            .set(root.get(Artist_.SYNC), 0);
        List<Predicate> conditions = new ArrayList<>();
        conditions.add(cb.equal(root.get(Artist_.STATUS), ModelStatus.ACTIVE));
        if (option.getId() != null) {
            conditions.add(cb.equal(root.get(Artist_.ID), option.getId()));
        }
        if (option.getCreateTime() != null) {
            conditions.add(cb.greaterThan(root.get(Artist_.CREATE_TIME), option.getCreateTime()));
        }
        if (option.getUpdateTime() != null) {
            conditions.add(cb.greaterThan(root.get(Artist_.UPDATE_TIME), option.getUpdateTime()));
        }
        criteriaUpdate.where(conditions.toArray(new Predicate[] {}));
        return entityManager.createQuery(criteriaUpdate).executeUpdate();
    }
}
