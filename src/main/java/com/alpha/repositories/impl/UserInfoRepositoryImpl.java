package com.alpha.repositories.impl;

import com.alpha.repositories.UserInfoRepositoryCustom;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * @author thanhvt
 * @created 05/06/2021 - 11:37 CH
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Component
public class UserInfoRepositoryImpl implements UserInfoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void applySetting(String username, String setting) {
        String sql = "UPDATE user_info SET setting = :setting WHERE username = :username";
        Query query = this.entityManager.createNativeQuery(sql);
        query.setParameter("username", username);
        query.setParameter("setting", setting);
        int updateCount = query.executeUpdate();
        log.info("Applied setting for {}, count: {}", username, updateCount);
    }
}
