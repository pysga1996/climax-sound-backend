package com.lambda.repository;

import com.lambda.model.entity.PeopleWhoLiked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeopleWhoLikedRepository extends JpaRepository<PeopleWhoLiked, Long> {
    PeopleWhoLiked findBySongIdAndUserId(Long songId, Long UserId);
}
