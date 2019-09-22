package com.lambda.repository;

import com.lambda.model.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends PagingAndSortingRepository<Artist, Long> {
    Artist findByName(String name);
    Page<Artist> findAllByNameContaining(String name, Pageable pageable);
    Page<Artist> findAllByAlbums_Name(String name, Pageable pageable);
}
