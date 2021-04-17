package com.alpha.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "info")
    private String info;
}
