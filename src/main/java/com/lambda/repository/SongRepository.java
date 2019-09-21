package com.lambda.repository;

import com.lambda.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends PagingAndSortingRepository<Song, Long> {
//    @Query("select n from Note n where n.user.username = ?#{ principal?.username }")
    Page<Song> findAll(Pageable pageable);
}
