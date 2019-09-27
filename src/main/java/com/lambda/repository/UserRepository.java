package com.lambda.repository;

import com.lambda.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u INNER JOIN FETCH u.roles r JOIN r.privileges WHERE u.username = :username")
    User findByUsername(@Param("username") String username);

    Page<User> findByUsernameContaining(String username, Pageable pageable);

    Page<User> findByRoles_Name(String username, Pageable pageable);
}
