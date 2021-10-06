package com.alpha.repositories;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.MediaRef;
import com.alpha.constant.Status;
import com.alpha.model.entity.ResourceInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 06/06/2021 - 7:35 CH
 * @project vengeance
 * @since 1.0
 **/
@Repository
public interface ResourceInfoRepository extends JpaRepository<ResourceInfo, Long> {

    Optional<ResourceInfo> findByMediaIdAndStorageTypeAndMediaRefAndStatus(Long mediaId,
        StorageType storageType,
        MediaRef mediaRef,
        Status status);

    List<ResourceInfo> findAllByMediaIdAndStorageTypeAndMediaRefAndStatus(Long mediaId,
        StorageType storageType,
        MediaRef mediaRef,
        Status status);

    List<ResourceInfo> findAllByMediaIdInAndMediaRefAndStatus(List<Long> mediaIds,
        MediaRef mediaRef,
        Status status);

    Optional<ResourceInfo> findByUsernameAndStorageTypeAndMediaRefAndStatus(String username,
        StorageType storageType,
        MediaRef mediaRef,
        Status status);
}
