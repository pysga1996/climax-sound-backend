package com.alpha.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface CollectionFilterService<T> {

    ResponseEntity<Page<T>> filteredCollection(Page<T> list);
}
