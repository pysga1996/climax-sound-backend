package com.lambda.repository;

import com.lambda.model.entity.Privilege;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends PagingAndSortingRepository<Privilege, Integer> {
    Privilege findByName(String name);
}
