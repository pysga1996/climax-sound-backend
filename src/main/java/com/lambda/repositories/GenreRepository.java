package com.lambda.repositories;

import com.lambda.models.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
//    @Query(value = "SELECT * FROM genre WHERE BINARY title=:title", nativeQuery = true)
    Genre findByName(String name);

    Iterable<Genre> findAllByNameContaining(String name);
}
