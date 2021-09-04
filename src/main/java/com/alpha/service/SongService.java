package com.alpha.service;

import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongSearchDTO;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface SongService {

    Page<SongDTO> findAll(Pageable pageable);

    Page<SongDTO> findAllByConditions(Pageable pageable, SongSearchDTO songSearchDTO);

    SongDTO findById(Long id);

    void listenToSong(Long id);

    SongDTO uploadAndSaveSong(MultipartFile file, SongDTO song) throws IOException;

    SongDTO create(SongDTO song);

    SongDTO update(Long id, SongDTO song, MultipartFile multipartFile) throws IOException;

    void deleteById(Long id);

    void deleteAll(Collection<SongDTO> songs);

    Map<Long, Boolean> getUserSongLikeMap(Map<Long, Boolean> songIdMap);


}
