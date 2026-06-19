package com.oriole.wisepen.ai.asset.domain.dto.req;

import com.oriole.wisepen.ai.asset.constant.AIAssetValidationMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AIResourceForkRequest {
    @NotBlank(message = AIAssetValidationMsg.RESOURCE_ID_NOT_BLANK)
    private String resourceId;

    private Integer forkedResourceVersion;

    private String forkedResourceName;
}
