package com.lambda.repositories;

import com.lambda.models.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findBySongIdAndUserId(Long songId, Long UserId);
}
