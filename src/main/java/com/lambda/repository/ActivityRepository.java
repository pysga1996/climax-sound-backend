package com.lambda.repository;

import com.lambda.model.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    @Query(value = "SELECT * FROM activity WHERE BINARY name=:name", nativeQuery = true)
    Activity findByName(@Param("name") String name);

    Page<Activity> findAllByNameContaining(String name, Pageable pageable);
}
