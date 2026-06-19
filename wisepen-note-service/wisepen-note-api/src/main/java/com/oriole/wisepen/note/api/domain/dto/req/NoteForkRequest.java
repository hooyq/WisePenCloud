package com.oriole.wisepen.note.api.domain.dto.req;

import com.oriole.wisepen.note.api.constant.NoteValidationMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoteForkRequest {
    @NotBlank(message = NoteValidationMsg.RESOURCE_ID_NOT_BLANK)
    private String resourceId;

    private Integer forkedResourceVersion;

    private String forkedResourceName;
}
