package com.alpha.repositories;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author thanhvt
 * @created 10/9/2021 - 11:07 AM
 * @project vengeance
 * @since 1.0
 **/
public interface TagRepositoryCustom {

    Map<Long, Set<String>> retrieveTagsOfAlbumIds(List<Long> albumIds);

    Map<Long, Set<String>> retrieveTagsOfSongIds(List<Long> songs);

}
