package com.lambda.models.utilities;

import com.lambda.models.entities.Artist;
import com.lambda.models.entities.Song;
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
