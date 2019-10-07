package com.lambda.model.form;

import com.lambda.model.entity.Artist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Data
public abstract class MediaForm {
    private String name;

    private Date releaseDate;

    private Collection<Artist> artists;

    private String genres;

    private String tags;

    private String country;

    private String theme;
}
