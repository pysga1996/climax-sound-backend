package com.lambda.repository;

import com.lambda.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends PagingAndSortingRepository<Song, Long> {
    Song findByName(String name);
    Page<Song> findAllByNameContaining(String name, Pageable pageable);
    Page<Song> findAllByArtists_Name(String name, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Song s JOIN s.tags t WHERE t.name = :name")
    Page<Song> findAllByTags_Name(@Param("name") String name, Pageable pageable);
}
