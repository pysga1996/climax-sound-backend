package com.lambda.service.impl;

import com.lambda.service.CollectionFilterService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CollectionFilterServiceImpl<T> implements CollectionFilterService<T> {
    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Page<T>> filteredCollection(Page<T> list) {
        boolean isEmpty = true;
        if (list instanceof Collection) {
            isEmpty = ((Collection<T>) list).isEmpty();
        }
        if (isEmpty) {
            return new ResponseEntity<Page<T>>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<Page<T>>(list, HttpStatus.OK);
    }
}
