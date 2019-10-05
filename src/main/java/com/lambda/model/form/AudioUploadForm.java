package com.lambda.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class AudioUploadForm extends MediaForm {
    @NotBlank
    private String name;

    @NotBlank
    private String artists;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @Pattern(regexp = "(^[a-z0-9_-]{3,16}$|^$)")
    private String album;

    private String genres;

    private String tags;

    private String country;

    private String theme;
}
