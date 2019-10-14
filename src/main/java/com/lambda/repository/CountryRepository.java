package com.lambda.repository;

import com.lambda.model.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
//    @Query(value = "SELECT * FROM country WHERE BINARY name=:name", nativeQuery = true)
    Country findByName(String name);

    Page<Country> findAll(Pageable pageable);

    Page<Country> findAllByNameContaining(String name, Pageable pageable);
}
