package com.oriole.wisepen.ai.asset.service.impl;

import com.oriole.wisepen.ai.asset.domain.entity.AgentEntity;
import com.oriole.wisepen.ai.asset.domain.entity.AgentVersionBundleEntity;
import com.oriole.wisepen.ai.asset.domain.entity.SkillVersionBundleEntity;
import com.oriole.wisepen.ai.asset.enums.AIResourceSourceType;
import com.oriole.wisepen.ai.asset.repository.AIResourceBaseRepository;
import com.oriole.wisepen.ai.asset.service.IVersionService;
import com.oriole.wisepen.resource.enums.ResourceType;
import com.oriole.wisepen.resource.feign.RemoteResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AgentServiceImpl extends AIResourceServiceImpl<AgentEntity, AgentVersionBundleEntity>{

        public AgentServiceImpl(AIResourceBaseRepository<AgentEntity> aiResourceBaseRepository, IVersionService<AgentVersionBundleEntity> agentVersionService, RemoteResourceService remoteResourceService) {
            super(aiResourceBaseRepository, agentVersionService, remoteResourceService);
        }

        @Override
        protected AgentEntity buildNewResource(String resourceId, String name, String description, AIResourceSourceType agentSourceType) {
            return AgentEntity.builder()
                    .resourceId(resourceId).name(name).description(description).version(0).sourceType(agentSourceType)
                    .build();
        }

        @Override
        protected ResourceType getResourceType() {
            return ResourceType.AGENT;
        }
}
