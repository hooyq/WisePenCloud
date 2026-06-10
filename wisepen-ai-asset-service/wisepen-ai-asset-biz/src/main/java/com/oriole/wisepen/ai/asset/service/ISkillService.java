package com.oriole.wisepen.ai.asset.service;

import com.oriole.wisepen.ai.asset.domain.base.SkillInfoBase;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillCreateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillUpdateRequest;
import com.oriole.wisepen.ai.asset.domain.dto.res.SkillMetaInfoResponse;

import java.util.List;

public interface ISkillService {

    String createSkill(SkillCreateRequest req, String userId);

    void deleteSkills(List<String> resourceIds);

    void updateSkill(SkillUpdateRequest req);

    SkillInfoBase getSkillInfo(String resourceId);

    List<SkillMetaInfoResponse> listPublishedSkillsMeta(List<String> resourceIds);

}
