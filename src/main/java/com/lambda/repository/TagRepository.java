package com.lambda.repository;

import com.lambda.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {
    Tag findByName(String name);
    Page<Tag> findAll(Pageable pageable);
    Page<Tag> findAllByNameContaining(String name, Pageable pageable);
}
