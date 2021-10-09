package com.alpha.repositories;

import com.alpha.model.entity.Playlist;
import com.alpha.model.entity.Song;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface PlaylistRepository extends JpaRepository<Playlist, Long>,
    PlaylistRepositoryCustom {

    boolean existsPlaylistByTitleAndUsername(String title, String username);

    Optional<Playlist> findByIdAndUsername(Long id, String username);

    Page<Playlist> findAllByUsernameAndSongsNotContains(String username, Song song,
        Pageable pageable);

    Page<Playlist> findAllByUsername(String username, Pageable pageable);

    Page<Playlist> findAllByTitleContaining(String title, Pageable pageable);
}
