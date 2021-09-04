package com.alpha.repositories;

import com.alpha.model.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT a FROM Album a LEFT JOIN FETCH a.songs s WHERE a.id=:id")
    @NonNull
    Optional<Album> findById(@NonNull @Param("id") Long id);

    @NonNull
    Page<Album> findAll(@NonNull Pageable pageable);

    @Query("SELECT a FROM Album a JOIN a.artists b WHERE b.name=:name")
    Page<Album> findAllByArtist_Name(@Param("name") String name, Pageable pageable);
}
