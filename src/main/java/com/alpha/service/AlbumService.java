package com.alpha.service;

import com.alpha.elastic.model.AlbumEs;
import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.AlbumDTO.AlbumAdditionalInfoDTO;
import com.alpha.model.dto.AlbumSearchDTO;
import com.alpha.model.dto.AlbumUpdateDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AlbumService {

    AlbumDTO detail(Long id, Pageable pageable);

    AlbumAdditionalInfoDTO findAdditionalInfoById(Long id);

    Page<AlbumDTO> findAll(Pageable pageable);

    Page<AlbumEs> findPageByName(String name, Pageable pageable);

    Page<AlbumDTO> findAllByConditions(Pageable pageable, AlbumSearchDTO albumSearchDTO);

    void create(AlbumDTO album);

    void deleteById(Long id);

    AlbumDTO upload(MultipartFile file, AlbumDTO album);

    void updateSongList(Long albumId, List<AlbumUpdateDTO> songDTOList);

    AlbumDTO update(MultipartFile file, AlbumDTO album, Long id);

}
