package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.JsonComponent;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonComponent
@JsonIgnoreProperties(value = {"url"}, allowGetters = true, ignoreUnknown = true)
public class SettingDTO implements Serializable {

    private static final long serialVersionUID = 43L;

    private Long id;

    private Long userId;

    @Builder.Default
    private Boolean darkMode = true;
}
