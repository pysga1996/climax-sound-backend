package com.alpha.repositories;

import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.MediaDTO;
import com.alpha.util.helper.DataTypeComparer;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author thanhvt
 * @created 28/08/2021 - 12:46 SA
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
public abstract class BaseRepository {

    private final int batchSize = 20;

    protected <E extends MediaDTO> BigInteger addArtist(BigInteger artistId,
        BigInteger currentArtistId,
        boolean isSameSong, E entity,
        Tuple row) {
        if (DataTypeComparer.equal(currentArtistId, artistId) && isSameSong) {
            return artistId;
        }
        if (entity == null) {
            return artistId;
        }
        ArtistDTO artist = new ArtistDTO();
        artistId = currentArtistId;
        artist.setId(artistId.longValue());
        artist.setName((String) row.get("artist_name"));
        artist.setUnaccentName((String) row.get("artist_unaccent_name"));
        entity.getArtists().add(artist);
        return artistId;
    }

    protected <E extends MediaDTO> Long addArtist(Long artistId,
        Long currentArtistId,
        boolean isSameSong, E entity,
        ResultSet rs) throws SQLException {
        if (DataTypeComparer.equal(currentArtistId, artistId) && isSameSong) {
            return artistId;
        }
        if (entity == null) {
            return artistId;
        }
        ArtistDTO artist = new ArtistDTO();
        artistId = currentArtistId;
        artist.setId(artistId);
        artist.setName(rs.getString("artist_name"));
        artist.setUnaccentName(rs.getString("artist_unaccent_name"));
        artist.setAvatarUrl(rs.getString("artist_avatar"));
        entity.getArtists().add(artist);
        return artistId;
    }

    protected void setPaginationParams(Query query, Pageable pageable) {
        query
            .setParameter("limit", pageable.getPageSize())
            .setParameter("offset", pageable.getOffset());
    }

    protected <E> Page<E> extractResult(Query query, Pageable pageable) {
        return Page.empty();
    }

    protected abstract EntityManager getEntityManager();

    protected final <E> int batchInsertUpdateDelete(String sql, Collection<E> list,
        StatementParamsSetupCallback<PreparedStatement, E, SQLException> paramsCallback) {
        Session hibernateSession = this.getEntityManager().unwrap(Session.class);
        hibernateSession.doWork(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                int i = 1;
                for (E element : list) {
                    paramsCallback.accept(preparedStatement, element, i);
                    preparedStatement.addBatch();
                    //Batch size: 20
                    if (i % batchSize == 0) {
                        preparedStatement.executeBatch();
                    }
                    i++;
                }
                preparedStatement.executeBatch();
            } catch (SQLException e) {
                log.error("Error in bulk insert/update/delete using query: {}, error code: {}", sql,
                    e.getErrorCode());
                throw e;
            }
        });
        return list.size();
    }

    @FunctionalInterface
    public interface StatementParamsSetupCallback<S, E, X extends Throwable> {

        void accept(S statement, E element, int index) throws X;
    }
}
