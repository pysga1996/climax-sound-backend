package com.lambda.repository;

import com.lambda.model.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends PagingAndSortingRepository<Album, Long> {
    @Query("SELECT a FROM Album a JOIN FETCH a.artists")
    Album findByName(String name);

    Page<Album> findAll(Pageable pageable);

    Page<Album> findAllByNameContaining(String name, Pageable pageable);

    @Query("SELECT a FROM Album a JOIN a.artists b WHERE b.name = :name")
    Page<Album> findAllByArtists_Name(@Param("name") String name, Pageable pageable);
}
