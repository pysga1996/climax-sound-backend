package com.alpha.model.entity;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.MediaRef;
import com.alpha.constant.MediaType;
import com.alpha.constant.Status;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author thanhvt
 * @created 06/06/2021 - 2:48 CH
 * @project vengeance
 * @since 1.0
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Builder
@Entity
@Table(name = "resource_info")
public class ResourceInfo {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resource_info_id_gen")
    @SequenceGenerator(name = "resource_info_id_gen", sequenceName = "resource_info_id_seq", allocationSize = 1)
    @ToString.Include
    private Long id;

    @Column(name = "media_id")
    @ToString.Include
    private Long mediaId;

    @Column(name = "username")
    @ToString.Include
    private String username;

    @Column(name = "uri")
    @ToString.Include
    private String uri;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type")
    @ToString.Include
    private StorageType storageType;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "folder")
    private String folder;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "extension")
    private String extension;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type")
    @ToString.Include
    private MediaType mediaType;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_ref")
    @ToString.Include
    private MediaRef mediaRef;

    @Column(name = "status")
    @Convert(converter = Status.StatusAttributeConverter.class)
    private Status status;
}
