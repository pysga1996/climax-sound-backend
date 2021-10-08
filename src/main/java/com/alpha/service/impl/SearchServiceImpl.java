package com.alpha.service.impl;

import com.alpha.elastic.model.AlbumEs;
import com.alpha.elastic.model.ArtistEs;
import com.alpha.elastic.model.MediaEs;
import com.alpha.elastic.model.SongEs;
import com.alpha.elastic.repo.AlbumEsRepository;
import com.alpha.elastic.repo.ArtistEsRepository;
import com.alpha.elastic.repo.SongEsRepository;
import com.alpha.model.dto.UpdateSyncOption;
import com.alpha.repositories.AlbumRepository;
import com.alpha.repositories.ArtistRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.service.AlbumService;
import com.alpha.service.ArtistService;
import com.alpha.service.SearchService;
import com.alpha.service.SongService;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author thanhvt
 * @created 10/8/2021 - 7:04 PM
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Service
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    private final SongService songService;

    private final ArtistService artistService;

    private final AlbumService albumService;

    private final SongEsRepository songEsRepository;

    private final AlbumEsRepository albumEsRepository;

    private final ArtistEsRepository artistEsRepository;

    private final SongRepository songRepository;

    private final AlbumRepository albumRepository;

    private final ArtistRepository artistRepository;

    @Autowired
    public SearchServiceImpl(
        ElasticsearchOperations elasticsearchOperations, SongService songService,
        ArtistService artistService, AlbumService albumService,
        SongEsRepository songEsRepository,
        AlbumEsRepository albumEsRepository,
        ArtistEsRepository artistEsRepository, SongRepository songRepository,
        AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.songService = songService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songEsRepository = songEsRepository;
        this.albumEsRepository = albumEsRepository;
        this.artistEsRepository = artistEsRepository;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public Map<String, Page<? extends MediaEs>> search(String q) {
        Map<String, Page<? extends MediaEs>> mediaPageMap = new HashMap<>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<SongEs> songEsPage = this.songService.findPageByName(q, pageable);
        Page<ArtistEs> artistEsPage = this.artistService.findPageByName(q, pageable);
        Page<AlbumEs> albumEsPage = this.albumService.findPageByName(q, pageable);
        mediaPageMap.put("song", songEsPage);
        mediaPageMap.put("artist", artistEsPage);
        mediaPageMap.put("album", albumEsPage);
        return mediaPageMap;
    }

    @Override
    @Transactional
    public void reloadMapping(String indexName) {
        Document mapping;
        switch (indexName) {
            case "song":
                mapping = this.elasticsearchOperations.indexOps(SongEs.class).createMapping();
                this.elasticsearchOperations.indexOps(SongEs.class).putMapping(mapping);
                break;
            case "album":
                mapping = this.elasticsearchOperations.indexOps(AlbumEs.class).createMapping();
                this.elasticsearchOperations.indexOps(AlbumEs.class).putMapping(mapping);
                break;
            case "artist":
                mapping = this.elasticsearchOperations.indexOps(ArtistEs.class).createMapping();
                this.elasticsearchOperations.indexOps(ArtistEs.class).putMapping(mapping);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported index!");
        }
    }

    @Override
    @Transactional
    public void clearIndex(String indexName) {
        switch (indexName) {
            case "song":
                this.songEsRepository.deleteAll();
                break;
            case "album":
                this.albumEsRepository.deleteAll();
                break;
            case "artist":
                this.artistEsRepository.deleteAll();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported index!");
        }
    }

    @Override
    @Transactional
    public void markForSync(String name, UpdateSyncOption option) {
        int syncCount;
        switch (name) {
            case "song":
                syncCount = this.songRepository.markForSync(option);
                log.info("Update sync for song count: {}", syncCount);
                break;
            case "album":
                syncCount = this.albumRepository.markForSync(option);
                log.info("Update sync for album count: {}", syncCount);
                break;
            case "artist":
                syncCount = this.artistRepository.markForSync(option);
                log.info("Update sync for artist count: {}", syncCount);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported index!");
        }
    }
}


