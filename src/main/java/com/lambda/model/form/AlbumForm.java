package com.lambda.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlbumForm extends MediaForm {
    @NotBlank
    private String name;

    @NotBlank
    private String artists;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String genres;

    private String tags;

    private String country;

    private String theme;
}
