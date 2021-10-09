package com.alpha.model.entity;

import com.alpha.constant.Folder;
import com.alpha.constant.MediaRef;
import com.alpha.constant.MediaType;
import com.alpha.constant.ModelStatus;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Builder
@Entity
@Table(name = "user_info")
@Where(clause = "status = 1")
public class UserInfo extends Media {

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

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "status")
    @Convert(converter = ModelStatus.StatusAttributeConverter.class)
    private ModelStatus status;

    @Override
    public ResourceInfo generateResource(MultipartFile file) {
        if (username == null) {
            throw new RuntimeException("Media host username is null!!");
        }
        String ext = this.getExtension(file);
        String fileName = MediaRef.USER_AVATAR.name() + " - " + username + "." + ext;
        fileName = this.normalizeFileName(fileName);
        return ResourceInfo.builder()
            .username(username)
            .extension(ext)
            .folder(Folder.AVATAR)
            .fileName(fileName)
            .status(ModelStatus.INACTIVE)
            .mediaType(MediaType.IMAGE)
            .mediaRef(MediaRef.USER_AVATAR)
            .build();
    }
}
