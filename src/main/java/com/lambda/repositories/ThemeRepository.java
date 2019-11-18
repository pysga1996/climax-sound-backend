package com.lambda.repositories;

import com.lambda.models.entities.Theme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {
//    @Query(value = "SELECT * FROM theme WHERE BINARY title=:title", nativeQuery = true)
    Theme findByName(String name);

    Page<Theme> findAllByNameContaining(String name, Pageable pageable);
}
