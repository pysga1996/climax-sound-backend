package com.lambda.repository;

import com.lambda.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends PagingAndSortingRepository<Album, Long> {
    Album findByName(String name);
    Page<Album> findAllByNameContaining(String name, Pageable pageable);

//    @Query("SELECT a FROM Album a JOIN FETCH a.artists b WHERE b.name = ?1")
    Page<Album> findAllByArtists_Name(String name, Pageable pageable);
}
