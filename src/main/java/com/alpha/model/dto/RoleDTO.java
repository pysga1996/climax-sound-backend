package com.alpha.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotBlank
    private String authority;

    public RoleDTO(String authority) {
        this.authority = authority;
    }
}
