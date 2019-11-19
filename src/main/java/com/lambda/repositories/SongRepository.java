package com.lambda.repositories;

import com.lambda.models.entities.Artist;
import com.lambda.models.entities.Song;
import com.lambda.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    Page<Song> findAll(Pageable pageable);

    Page<Song> findAllByOrderByReleaseDateDesc(Pageable pageable);

    Page<Song> findAllByOrderByDisplayRatingDesc(Pageable pageable);

    Page<Song> findAllByOrderByListeningFrequencyDesc(Pageable pageable);

    Iterable<Song> findFirst10ByOrderByListeningFrequencyDesc();

    @Query("SELECT s FROM Song s ORDER BY SIZE(s.users) DESC")
    Page<Song> findAllByOrderByUsers_Size(Pageable pageable);

    @Query("SELECT s from Song s LEFT JOIN FETCH s.comments c WHERE s.id=:id")
    Optional<Song> findById(@Param("id") Long id);

//    @Query(value = "SELECT * FROM song WHERE BINARY title=:title", nativeQuery = true)
    Iterable<Song> findAllByTitle(String title);

    Page<Song> findAllByUploader_Id(Long id, Pageable pageable);

//    @Query(nativeQuery = true, value = "SELECT * FROM public.song "
//            + "WHERE LOWER(unaccent(title)) LIKE LOWER(unaccent(:title))||'%'")
    Iterable<Song> findAllByUnaccentTitleContaining(String title);

    Page<Song> findAllByTitleContaining(String title, Pageable pageable);

    Page<Song> findAllByArtistsContains(Artist artist, Pageable pageable);

    Page<Song> findAllByUsersContains(User user, Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.albums a WHERE a.id = :id")
    Page<Song> findAllByAlbum_Id(@Param("id") Long id, Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.albums a WHERE a.id = :id")
    Iterable<Song> findAllByAlbum_Id(@Param("id") Long id);

    @Query("SELECT DISTINCT s FROM Song s JOIN s.tags t WHERE t.name = :name")
    Page<Song> findAllByTag_Name(@Param("name") String name, Pageable pageable);

}
