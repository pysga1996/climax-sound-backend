package com.alpha.repositories;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.AlbumDTO.AlbumAdditionalInfoDTO;
import com.alpha.model.dto.AlbumSearchDTO;
import com.alpha.model.dto.AlbumUpdateDTO;
import com.alpha.model.dto.UpdateSyncOption;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author thanhvt
 * @created 16/08/2021 - 10:45 CH
 * @project vengeance
 * @since 1.0
 **/
public interface AlbumRepositoryCustom {

    Page<AlbumDTO> findAllByConditions(Pageable pageable, AlbumSearchDTO albumSearchDTO);

    AlbumAdditionalInfoDTO findAdditionalInfo(Long id);

    void updateSongList(Long albumId, List<AlbumUpdateDTO> albumUpdateDTOList);

    int markForSync(UpdateSyncOption option);
}
