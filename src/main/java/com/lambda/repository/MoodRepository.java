package com.lambda.repository;

import com.lambda.model.entity.Mood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MoodRepository extends JpaRepository<Mood, Integer> {
    @Query(value = "SELECT * FROM genre WHERE BINARY name=:name", nativeQuery = true)
    Mood findByName(@Param("name")String name);

    Page<Mood> findAll(Pageable pageable);

    Page<Mood> findAllByNameContaining(String name, Pageable pageable);
}
