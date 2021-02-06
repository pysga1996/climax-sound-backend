package com.alpha.repositories;

import com.alpha.model.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    //    @Query(value = "SELECT * FROM country WHERE BINARY title=:title", nativeQuery = true)
    Country findByName(String name);

    Page<Country> findAll(Pageable pageable);

    Page<Country> findAllByNameContaining(String name, Pageable pageable);
}
