package com.alpha.service;

import com.alpha.elastic.model.MediaEs;
import com.alpha.model.dto.UpdateSyncOption;
import java.util.Map;
import org.springframework.data.domain.Page;

/**
 * @author thanhvt
 * @created 10/8/2021 - 7:04 PM
 * @project vengeance
 * @since 1.0
 **/
public interface SearchService {

    Map<String, Page<? extends MediaEs>> search(String q);

    void reloadMapping(String indexName);

    void clearIndex(String indexName, Long id);

    void resetIndex(String indexName);

    void markForSync(String name, UpdateSyncOption indexName);

}
