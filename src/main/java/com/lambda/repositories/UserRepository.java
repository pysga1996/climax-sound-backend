package com.lambda.repositories;

import com.lambda.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u INNER JOIN FETCH u.authorities r WHERE u.id=:id")
    Optional<User> findById(@Param("id") Long id);

    @Query("SELECT u FROM User u INNER JOIN FETCH u.authorities r WHERE u.username=:username")
    Optional<User> findByUsername(@Param("username") String username);

    Page<User> findByUsernameContaining(String username, Pageable pageable);

    Page<User> findByAuthorities_Authority(String authority, Pageable pageable);

    User findByEmail(String email);
}
