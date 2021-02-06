package com.alpha.repositories;

import com.alpha.model.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    //    @Query(value = "SELECT * FROM tag WHERE BINARY title=:title", nativeQuery = true)
    Tag findByName(String name);

    Page<Tag> findAll(Pageable pageable);

    Page<Tag> findAllByNameContaining(String name, Pageable pageable);
}
