package com.alpha.repositories;

import com.alpha.model.entity.Artist;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long>, ArtistRepositoryCustom {

    //    @Query(value = "SELECT * FROM album WHERE BINARY title=:title", nativeQuery = true)
    Artist findByName(String name);

    Page<Artist> findAllBySync(Integer sync, Pageable pageable);

    Page<Artist> findAllByNameContaining(String name, Pageable pageable);

    //    @Query(nativeQuery = true, value = "SELECT * FROM public.artist "
//            + "WHERE LOWER(unaccent(name)) LIKE LOWER(unaccent(:name))||'%'")
    Iterable<Artist> findAllByUnaccentNameContainingIgnoreCase(String name);

    //    @Query("SELECT a, s FROM Artist a JOIN FETCH a.songs s")
    @NonNull
    Optional<Artist> findById(@NonNull @Param("id") Long id);

}
