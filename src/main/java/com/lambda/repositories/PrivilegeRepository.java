package com.lambda.repositories;

import com.lambda.models.entities.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {
//    @Query(value = "SELECT * FROM privilege WHERE BINARY title=:title", nativeQuery = true)
    Privilege findByName(String name);
}
