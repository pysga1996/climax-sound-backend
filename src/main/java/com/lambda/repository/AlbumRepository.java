package com.lambda.repository;

import com.lambda.model.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT a FROM Album a LEFT JOIN FETCH a.songs s WHERE a.id=:id")
    Optional<Album> findById(@Param("id") Long id);

    Page<Album> findAll(Pageable pageable);

//    @Query(value = "SELECT * FROM album WHERE BINARY title=:title", nativeQuery = true)
    Iterable<Album> findAllByTitle(String title);

    Page<Album> findAllByTitleContaining(String title, Pageable pageable);

    @Query("SELECT a FROM Album a JOIN a.artists b WHERE b.name=:name")
    Page<Album> findAllByArtist_Name(@Param("name") String name, Pageable pageable);
}
