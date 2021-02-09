package com.alpha.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements UserDetails, Serializable {

    private Long id;

    private UserProfileDTO userProfile;

    private SettingDTO setting;

    private String username;

    private String password;

    private boolean enabled;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private Set<GrantedAuthority> authorities;

    public UserDTO(String username, String password, boolean enabled,
                   boolean accountNonExpired, boolean accountNonLocked,
                   boolean credentialsNonExpired, Set<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Map<String, Boolean> getBasicInfo() {
        Map<String, Boolean> otherInfo = new HashMap<>();
        otherInfo.put("enabled", this.isEnabled());
        otherInfo.put("accountNonLocked", this.isAccountNonLocked());
        otherInfo.put("accountNonExpired", this.isAccountNonExpired());
        otherInfo.put("credentialsNonExpired", this.isCredentialsNonExpired());
        return otherInfo;
    }
}

