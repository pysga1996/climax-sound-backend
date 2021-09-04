package com.alpha.repositories;

import java.util.List;

/**
 * @author thanhvt
 * @created 24/08/2021 - 10:53 CH
 * @project vengeance
 * @since 1.0
 **/
public interface PlaylistRepositoryCustom {

    void addToPlayList(String username, Long playlistId, List<Long> songIds);

    void removeFromPlaylist(String username, Long playlistId, List<Long> songIds);

}
