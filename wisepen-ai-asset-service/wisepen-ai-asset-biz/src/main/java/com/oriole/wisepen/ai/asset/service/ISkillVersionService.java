package com.oriole.wisepen.ai.asset.service;

import com.oriole.wisepen.ai.asset.domain.dto.req.SkillAssetDeleteRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillAssetUploadInitRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SkillVersionPublishRequest;
import com.oriole.wisepen.ai.asset.domain.dto.res.SkillAssetUploadInitResponse;
import com.oriole.wisepen.ai.asset.domain.dto.res.SkillVersionBundleInfoResponse;
import com.oriole.wisepen.file.storage.api.domain.mq.FileUploadedMessage;

import java.util.List;

public interface ISkillVersionService {

    void createDraftSkillVersion(String resourceId, Integer draftVersion);

    SkillVersionBundleInfoResponse getSkillVersionBundle(String resourceId, Integer version);

    SkillAssetUploadInitResponse initUploadSkillAssets(SkillAssetUploadInitRequest req);

    void deleteSkillAssets(SkillAssetDeleteRequest req);

    void publishSkillVersion(SkillVersionPublishRequest req);

    void handleFileUploaded(FileUploadedMessage message);

    void deleteAllVersionsByResourceIds(List<String> resourceIds);
}
