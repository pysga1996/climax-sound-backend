package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class UserInfoDTO {

    @ToString.Include
    private String username;

    @JsonRawValue
    private String profile;

    @JsonRawValue
    private String setting;
}
