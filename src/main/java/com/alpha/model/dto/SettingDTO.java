package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SettingDTO {

    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Builder.Default
    private Boolean darkMode = true;

    @JsonIgnore
    private UserDTO user;
}
