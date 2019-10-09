package com.lambda.repository;

import com.lambda.model.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {
    @Query(value = "SELECT * FROM privilege WHERE BINARY name=:name", nativeQuery = true)
    Privilege findByName(@Param("name")String name);
}
