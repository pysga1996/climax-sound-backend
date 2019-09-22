package com.lambda.repository;

import com.lambda.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends PagingAndSortingRepository<Activity, Integer> {
    Activity findByName(String name);
    Page<Activity> findAllByNameContaining(String name, Pageable pageable);
}
