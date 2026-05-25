package com.oriole.wisepen.resource.repository;

import com.oriole.wisepen.resource.domain.entity.ESIndexEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ESIndexRepository extends ElasticsearchRepository<ESIndexEntity, String> {
}
