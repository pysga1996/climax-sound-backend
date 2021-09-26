package com.alpha.model.dto;

import com.alpha.constant.EntityType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"user"}, allowGetters = true, ignoreUnknown = true)
public class CommentDTO {

    private Long id;

    @Length(max = 500)
    @NotBlank
    private String content;

    private EntityType entityType;

    private Long entityId;

    private UserInfoDTO userInfo;

    private Date createTime;

    private Date updateTime;

    private Integer status;
}
