package com.alpha.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponseDTO {

    private String fileName;

    private String fileDownloadUri;

    private String fileType;

    private long size;
}
