package com.lambda.model.utilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}
