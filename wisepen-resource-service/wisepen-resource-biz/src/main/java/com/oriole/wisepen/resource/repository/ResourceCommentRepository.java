package com.oriole.wisepen.resource.repository;

import com.oriole.wisepen.resource.domain.entity.ResourceCommentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResourceCommentRepository extends MongoRepository<ResourceCommentEntity, String> {

    @Query("{ '_id': ?0, 'deletedAt': null }")
    Optional<ResourceCommentEntity> findByIdAndDeletedAtIsNull(String id);

    void deleteAllByResourceIdIn(List<String> resourceIds);
}