package com.alpha.repositories;

import com.alpha.constant.EntityType;
import com.alpha.model.entity.Comment;
import com.alpha.model.entity.UserInfo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByEntityTypeAndEntityIdOrderByCreateTimeDesc(EntityType entityType, Long id,
        Pageable pageable);

    void deleteByIdAndEntityTypeAndUserInfo(Long id, EntityType type, UserInfo userInfo);

    Optional<Comment> findByIdAndEntityTypeAndUserInfo(Long id, EntityType type, UserInfo userInfo);
}
