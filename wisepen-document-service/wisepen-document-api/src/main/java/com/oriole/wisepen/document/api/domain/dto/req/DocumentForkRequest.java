package com.oriole.wisepen.document.api.domain.dto.req;

import com.oriole.wisepen.document.api.constant.DocumentValidationMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentForkRequest {
    @NotBlank(message = DocumentValidationMsg.RESOURCE_ID_EMPTY)
    private String resourceId;

    private Integer forkedResourceVersion;

    private String forkedResourceName;
}
