package com.alpha.model.dto;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.MediaRef;
import com.alpha.constant.MediaType;
import com.alpha.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author thanhvt
 * @created 06/06/2021 - 5:35 CH
 * @project vengeance
 * @since 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResourceInfoDTO {

    private Long id;

    private Long mediaId;

    private String username;

    private String uri;

    private StorageType storageType;

    private String storagePath;

    private String folder;

    private String fileName;

    private String extension;

    private MediaType mediaType;

    private MediaRef mediaRef;

    private Status status;
}
