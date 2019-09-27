package com.lambda.repository;

import com.lambda.model.entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    @Query("SELECT r FROM Role r INNER JOIN FETCH r.privileges WHERE r.name = :name")
    Role findByName(@Param("name") String name);
}
