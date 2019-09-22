package com.lambda.repository;

import com.lambda.model.Mood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoodRepository extends PagingAndSortingRepository<Mood, Integer> {
    Mood findByName(String name);
    Page<Mood> findAll(Pageable pageable);
    Page<Mood> findAllByNameContaining(String name, Pageable pageable);
}
