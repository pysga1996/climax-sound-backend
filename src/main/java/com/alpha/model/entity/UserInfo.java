package com.alpha.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Builder
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @Column(name = "username")
    @ToString.Include
    private String username;

    @Column(name = "profile")
    @ToString.Include
    private String profile;

    @Column(name = "setting")
    @ToString.Include
    private String setting;
}
