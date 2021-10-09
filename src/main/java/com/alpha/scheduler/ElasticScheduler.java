package com.alpha.scheduler;

import com.alpha.constant.ModelStatus;
import com.alpha.constant.MediaRef;
import com.alpha.elastic.model.AlbumEs;
import com.alpha.elastic.model.ArtistEs;
import com.alpha.elastic.model.MediaEs;
import com.alpha.elastic.model.ResourceMapEs;
import com.alpha.elastic.model.SongEs;
import com.alpha.elastic.repo.AlbumEsRepository;
import com.alpha.elastic.repo.ArtistEsRepository;
import com.alpha.elastic.repo.SongEsRepository;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.repositories.AlbumRepository;
import com.alpha.repositories.ArtistRepository;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.repositories.TagRepository;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author thanhvt
 * @created 10/6/2021 - 7:29 PM
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Component
public class ElasticScheduler {

    private final SongRepository songRepository;

    private final SongEsRepository songEsRepository;

    private final AlbumRepository albumRepository;

    private final AlbumEsRepository albumEsRepository;

    private final ArtistRepository artistRepository;

    private final ArtistEsRepository artistEsRepository;

    private final ResourceInfoRepository resourceInfoRepository;

    private final TagRepository tagRepository;

    public ElasticScheduler(SongRepository songRepository,
        SongEsRepository songEsRepository, AlbumRepository albumRepository,
        AlbumEsRepository albumEsRepository,
        ArtistRepository artistRepository,
        ArtistEsRepository artistEsRepository,
        ResourceInfoRepository resourceInfoRepository,
        TagRepository tagRepository) {
        this.songRepository = songRepository;
        this.songEsRepository = songEsRepository;
        this.albumRepository = albumRepository;
        this.albumEsRepository = albumEsRepository;
        this.artistRepository = artistRepository;
        this.artistEsRepository = artistEsRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.tagRepository = tagRepository;
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    @Transactional
    public void synchronizeAlbum() {
        log.info("Synchronize albums to ES start!");
        Pageable pageable = PageRequest.of(0, 20);
        List<AlbumEs> albumEsList = this.albumRepository.findAllBySync(0, pageable)
            .toList()
            .stream()
            .peek(e -> e.setSync(1))
            .map(AlbumEs::fromAlbum)
            .collect(Collectors.toList());
        this.attachTags(albumEsList, AlbumEs.class);
        this.processResourceMap(albumEsList, MediaRef.ALBUM_COVER);
        this.albumEsRepository.saveAll(albumEsList);
        log.info("Synchronize albums to ES end!");
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    @Transactional
    public void synchronizeArtist() {
        log.info("Synchronize artists to ES start!");
        Pageable pageable = PageRequest.of(0, 20);
        List<ArtistEs> artistEsList = this.artistRepository.findAllBySync(0, pageable)
            .toList()
            .stream()
            .peek(e -> e.setSync(1))
            .map(ArtistEs::fromArtist)
            .collect(Collectors.toList());
        this.processResourceMap(artistEsList, MediaRef.ARTIST_AVATAR);
        this.artistEsRepository.saveAll(artistEsList);
        log.info("Synchronize artists to ES end!");
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    @Transactional
    public void synchronizeSong() {
        log.info("Synchronize songs to ES start!");
        Pageable pageable = PageRequest.of(0, 20);
        List<SongEs> songEsList = this.songRepository.findAllBySync(0, pageable)
            .toList()
            .stream()
            .peek(e -> e.setSync(1))
            .map(SongEs::fromSong)
            .collect(Collectors.toList());
        this.attachTags(songEsList, SongEs.class);
        this.processResourceMap(songEsList, MediaRef.SONG_AUDIO);
        this.songEsRepository.saveAll(songEsList);
        log.info("Synchronize songs to ES end!");
    }

    private void attachTags(List<? extends MediaEs> mediaEsList, Class<? extends MediaEs> clazz) {
        Map<Long, Set<String>> tagMap = null;
        if (clazz == SongEs.class) {
            tagMap = this.tagRepository
                .retrieveTagsOfSongIds(mediaEsList.stream().map(MediaEs::getId).collect(
                    Collectors.toList()));
        } else if (clazz == AlbumEs.class) {
            tagMap = this.tagRepository
                .retrieveTagsOfAlbumIds(mediaEsList.stream().map(MediaEs::getId).collect(
                    Collectors.toList()));
        }
        if (tagMap != null) {
            final Map<Long, Set<String>> tagMapSub = tagMap;
            mediaEsList.forEach(e -> {
                Set<String> tagList = tagMapSub.get(e.getId());
                String[] tags = tagList != null ? tagList.toArray(new String[]{}) : new String[]{};
                e.setTags(tags);
            });
        }
    }

    private void processResourceMap(List<? extends MediaEs> mediaEsList, MediaRef mediaRef) {
        List<Long> mediaIds = mediaEsList.stream().map(MediaEs::getId)
            .collect(Collectors.toList());
        Map<Long, ResourceMapEs> resourceMapEsMap = this.resourceInfoRepository
            .findAllByMediaIdInAndMediaRefAndStatus(mediaIds, mediaRef, ModelStatus.ACTIVE)
            .stream()
            .collect(Collectors.groupingBy(ResourceInfo::getMediaId))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Entry::getKey, e -> {
                ResourceMapEs resourceMapEs = new ResourceMapEs();
                e.getValue().forEach(o -> {
                    switch (o.getStorageType()) {
                        case LOCAL:
                            resourceMapEs.setLocalUri(o.getUri());
                            break;
                        case FIREBASE:
                            resourceMapEs.setFirebaseUrl(o.getUri());
                            break;
                        case CLOUDINARY:
                            resourceMapEs.setCloudinaryUrl(o.getUri());
                            break;
                    }
                });
                return resourceMapEs;
            }));
        mediaEsList.forEach(e -> e.setResourceMap(resourceMapEsMap.get(e.getId())));
    }

}
