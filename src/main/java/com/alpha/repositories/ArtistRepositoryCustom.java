package com.alpha.repositories;

import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.ArtistSearchDTO;
import com.alpha.model.dto.UpdateSyncOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author thanhvt
 * @created 27/08/2021 - 9:58 CH
 * @project vengeance
 * @since 1.0
 **/
public interface ArtistRepositoryCustom {

    Page<ArtistDTO> findByConditions(Pageable pageable, ArtistSearchDTO artistSearchDTO);

    int markForSync(UpdateSyncOption option);
}
