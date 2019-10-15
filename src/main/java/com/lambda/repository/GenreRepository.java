package com.lambda.repository;

import com.lambda.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
//    @Query(value = "SELECT * FROM genre WHERE BINARY title=:title", nativeQuery = true)
    Genre findByName(String name);

    Iterable<Genre> findAllByNameContaining(String name);
}
