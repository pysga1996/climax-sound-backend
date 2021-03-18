package com.alpha.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtherInfoDTO {

    private Boolean credentialsNonExpired;

    private Boolean accountNonExpired;

    private Boolean enabled;

    private Boolean accountNonLocked;
}
