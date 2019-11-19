package com.lambda.repositories;

import com.lambda.models.entities.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
//    @Query(value = "SELECT * FROM album WHERE BINARY title=:title", nativeQuery = true)
    Artist findByName(String name);

    Iterable<Artist> findFirst10ByUnaccentNameContainingIgnoreCase(String name);

    Iterable<Artist> findFirst10ByNameContainingIgnoreCase(String name);

    Page<Artist> findAllByNameContaining(String name, Pageable pageable);

    Iterable<Artist> findAllByNameContainingIgnoreCase(String name);

//    @Query(nativeQuery = true, value = "SELECT * FROM public.artist "
//            + "WHERE LOWER(unaccent(name)) LIKE LOWER(unaccent(:name))||'%'")
    Iterable<Artist> findAllByUnaccentNameContainingIgnoreCase(String name);

    Page<Artist> findAllByAlbums_Title(String title, Pageable pageable);

//    @Query("SELECT a, s FROM Artist a JOIN FETCH a.songs s")
    Optional<Artist> findById(@Param("id") Long id);

}
