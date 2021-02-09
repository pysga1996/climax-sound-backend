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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_info-id_gen")
    @SequenceGenerator(name = "user_info_id_gen", sequenceName = "user_info_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "info")
    private String info;
}
