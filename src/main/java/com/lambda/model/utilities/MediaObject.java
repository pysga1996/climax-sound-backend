package com.lambda.model.utilities;

import com.lambda.model.entities.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;

@Data
public abstract class MediaObject {

    private String title;

    private Date releaseDate;

    private Collection<Genre> genres;

    private Collection<Artist> artists;

    private Collection<Tag> tags;

    private Country country;

    private Theme theme;

}
