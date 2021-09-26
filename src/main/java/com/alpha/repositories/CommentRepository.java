package com.alpha.repositories;

import com.alpha.constant.CommentType;
import com.alpha.model.entity.Comment;
import com.alpha.model.entity.UserInfo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByCommentTypeAndEntityIdOrderByCreateTimeDesc(CommentType commentType, Long id,
        Pageable pageable);

    void deleteByIdAndCommentTypeAndUserInfo(Long id, CommentType type, UserInfo userInfo);

    Optional<Comment> findByIdAndCommentTypeAndUserInfo(Long id, CommentType type, UserInfo userInfo);
}
