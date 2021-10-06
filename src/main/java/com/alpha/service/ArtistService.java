package com.alpha.service;

import com.alpha.elastic.model.ArtistEs;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.ArtistSearchDTO;
import java.io.IOException;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ArtistService {

    ArtistDTO findById(Long id);

    Page<ArtistEs> findByName(String name, Pageable pageable);

    Page<ArtistDTO> findByConditions(Pageable pageable, ArtistSearchDTO artistSearchDTO);

    Page<ArtistDTO> findAll(Pageable pageable);

    ArtistDTO create(ArtistDTO artist, MultipartFile multipartFile);

    ArtistDTO update(Long id, ArtistDTO artist, MultipartFile multipartFile) throws IOException;

    void deleteById(Long id);

    Map<Long, Boolean> getUserArtistLikeMap(Map<Long, Boolean> artistIdMap);
}
