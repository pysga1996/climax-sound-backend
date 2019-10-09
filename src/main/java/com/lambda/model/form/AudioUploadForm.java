package com.lambda.model.form;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambda.model.entity.Artist;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class AudioUploadForm extends MediaForm {
    @NotBlank
    private String name;

//    @JsonProperty
    private Collection<Artist> artists;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @Pattern(regexp = "(^[a-z0-9_-]{3,16}$|^$)")
    private String album;

    private String genres;

    private String tags;

    private String country;

    private String theme;
}
