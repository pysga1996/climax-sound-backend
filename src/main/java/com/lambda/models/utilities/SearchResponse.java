package com.lambda.models.utilities;

import com.lambda.models.entities.Artist;
import com.lambda.models.entities.Song;
import lombok.Data;

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
