package com.lambda.repository;

import com.lambda.model.Genre;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends PagingAndSortingRepository<Genre, Integer> {
    Genre findByName(String name);
    Iterable<Genre> findAllByNameContaining(String name);
}
