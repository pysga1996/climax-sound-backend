package com.lambda.repository;

import com.lambda.model.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaylistRepository extends PagingAndSortingRepository<Playlist, Long> {
    @Query("SELECT p FROM Playlist p JOIN FETCH p.songs WHERE p.id = :id")
    Optional<Playlist> findById(@Param("id") Long id);

    Page<Playlist> findAll(Pageable pageable);

    Page<Playlist> findAllByNameContaining(String name, Pageable pageable);
}
