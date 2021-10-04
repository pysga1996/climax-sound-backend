package com.alpha.repositories;

import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.MediaDTO;
import com.alpha.util.helper.DataTypeComparer;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import lombok.SneakyThrows;
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

    @FunctionalInterface
    public interface ProcSetupCallback<T> {

        Object[] accept(T inputObj);
    }

    public <T> void executeProcedureInBatch(String procName, List<T> objList,
        ProcSetupCallback<T> setupCallback) {
        Session session = this.getEntityManager().unwrap(Session.class);
        session.doWork(connection -> {
            PreparedStatement preparedStatement = null;
            try {
                int i = 0;
                do {
                    Object[] params = setupCallback.accept(objList.get(i));
                    if (preparedStatement == null) {
                        String sql = this.generateSql(procName, params);
                        preparedStatement = connection.prepareStatement(sql);
                        continue;
                    }
                    this.setupParams(preparedStatement, params);
                    preparedStatement.addBatch();
                    if (i % 20 == 0) {
                        preparedStatement.executeBatch();
                    }
                    i++;
                } while (i < objList.size());
                preparedStatement.executeBatch();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        });
    }

    private String generateSql(String procName, Object[] params) {
        StringBuilder sqlBuilder = new StringBuilder("call ")
            .append(procName)
            .append("(");
        List<String> questionMarkList = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            questionMarkList.add("?");
        }
        sqlBuilder
            .append(String.join(",", questionMarkList))
            .append(")");
        return sqlBuilder.toString();
    }

    @SneakyThrows
    private void setupParams(PreparedStatement preparedStatement, Object[] params) {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                preparedStatement.setNull(i + 1, Types.NULL);
            } else {
                preparedStatement
                    .setObject(i + 1, params[i]);
            }
        }
    }

    protected final <E> int executeInsertUpdateDeleteInBatch(String sql, Collection<E> list,
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
