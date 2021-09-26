package com.alpha.repositories;

import com.alpha.model.entity.Song;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface SongRepository extends JpaRepository<Song, Long>, SongRepositoryCustom {

    @NonNull
    Page<Song> findAll(@NonNull Pageable pageable);

    @NonNull
    Optional<Song> findById(@NonNull @Param("id") Long id);

    Optional<Song> findByIdAndUploader_Username(Long id, String username);

}
