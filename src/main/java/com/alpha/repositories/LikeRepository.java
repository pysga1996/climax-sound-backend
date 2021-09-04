package com.alpha.repositories;

import com.alpha.model.entity.UserFavoriteSong;
import com.alpha.service.LikeService.LikeConfig;
import com.alpha.service.LikeService.ListeningConfig;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByLikeId_SongIdAndLikeId_Username(Long songId, String username);
}
