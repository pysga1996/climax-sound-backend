package com.alpha.repositories;

import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongDTO.SongAdditionalInfoDTO;
import com.alpha.model.dto.SongSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author thanhvt
 * @created 10/06/2021 - 10:28 CH
 * @project vengeance
 * @since 1.0
 **/
public interface SongRepositoryCustom {

    Page<SongDTO> findAllConditions(Pageable pageable, SongSearchDTO songSearchDTO);

    SongAdditionalInfoDTO findAdditionalInfo(Long id);
}
