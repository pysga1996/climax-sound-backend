package com.alpha.repositories;

import com.alpha.model.entity.Genre;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    //    @Query(value = "SELECT * FROM genre WHERE BINARY title=:title", nativeQuery = true)
    Optional<Genre> findByName(String name);

    Page<Genre> findAllByOrderByUpdateTimeDescCreateTimeDesc(Pageable pageable);

    Page<Genre> findAllByNameContaining(String name, Pageable pageable);
}
