package com.alpha.model.dto;

import com.alpha.constant.EntityType;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author thanhvt
 * @created 26/07/2021 - 11:15 CH
 * @project vengeance
 * @since 1.0
 **/
@Data
public class LikeDTO {

    @NotNull
    private Long id;

    private Boolean isLiked;

    @NotNull
    private EntityType type;
}
