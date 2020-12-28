package com.lambda.model.utilities;

import com.lambda.model.entities.Artist;
import com.lambda.model.entities.Song;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {
    private Iterable<Song> songs;
    private Iterable<Artist> artists;
}
