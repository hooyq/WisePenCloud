package com.oriole.wisepen.ai.asset.repository;

import com.oriole.wisepen.ai.asset.domain.entity.SkillVersionBundleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillVersionBundleRepository extends MongoRepository<SkillVersionBundleEntity, String> {
    Optional<SkillVersionBundleEntity> findByResourceIdAndVersion(String resourceId, Integer version);

    List<SkillVersionBundleEntity> findByResourceId(String resourceId);

    @Query("{ '$or': [ { 'mainSkillMD.objectKey': ?0 }, { 'skillAssets.objectKey': ?0 } ] }")
    Optional<SkillVersionBundleEntity> findFirstByAssetObjectKey(String objectKey);

    void deleteByResourceIdIn(List<String> resourceIds);
}
