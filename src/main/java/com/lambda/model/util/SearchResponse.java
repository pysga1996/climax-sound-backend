package com.lambda.model.util;

import com.lambda.model.entity.Artist;
import com.lambda.model.entity.Song;
import lombok.Data;

import java.util.Collection;
@Data
public class SearchResponse {
    private Iterable<Song> songs;
    private Iterable<Artist> artists;

    public SearchResponse(Iterable<Song> songs, Iterable<Artist> artists) {
        this.songs = songs;
        this.artists = artists;
    }

    public SearchResponse() {
    }

}