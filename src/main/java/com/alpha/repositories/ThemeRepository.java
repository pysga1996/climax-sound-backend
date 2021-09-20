package com.alpha.repositories;

import com.alpha.model.entity.Theme;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {

    boolean existsByIdAndName(Integer id, String name);

    //    @Query(value = "SELECT * FROM theme WHERE BINARY title=:title", nativeQuery = true)
    Optional<Theme> findByName(String name);

    Page<Theme> findAllByOrderByUpdateTimeDescCreateTimeDesc(Pageable pageable);

    Page<Theme> findAllByNameContaining(String name, Pageable pageable);
}
