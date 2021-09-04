package com.alpha.repositories;

import com.alpha.model.entity.UserInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 05/06/2021 - 10:30 CH
 * @project vengeance
 * @since 1.0
 **/
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String>,
    UserInfoRepositoryCustom {

    Optional<UserInfo> findByUsername(String username);

}
