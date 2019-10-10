package com.lambda.repository;

import com.lambda.model.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

//    @Query("SELECT s, c, t from Song s JOIN FETCH s.country c JOIN FETCH s.theme t WHERE s.id=:id")
    Optional<Song> findById(Long id);

//    @Query(value = "SELECT * FROM song WHERE BINARY name=:name", nativeQuery = true)
    Optional<Song> findByName(String name);

    Iterable<Song> findAllByName(String name);

    Page<Song> findAllByUploader_Id(Long id, Pageable pageable);

    Iterable<Song> findAllByNameContaining(String name);

    Page<Song> findAllByNameContaining(String name, Pageable pageable);

    Page<Song> findAllByArtists_Name(String name, Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.albums a WHERE a.id = :id")
    Page<Song> findAllByAlbum_Id(@Param("id") Long id, Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.albums a WHERE a.id = :id")
    Iterable<Song> findAllByAlbum_Id(@Param("id") Long id);

    @Query("SELECT DISTINCT s FROM Song s JOIN s.tags t WHERE t.name = :name")
    Page<Song> findAllByTags_Name(@Param("name") String name, Pageable pageable);

}
