package com.alpha.model.util;

import com.alpha.model.entity.Artist;
import com.alpha.model.entity.Song;
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
