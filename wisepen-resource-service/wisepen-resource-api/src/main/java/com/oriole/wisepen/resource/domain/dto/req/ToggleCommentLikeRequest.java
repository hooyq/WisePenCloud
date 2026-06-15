package com.oriole.wisepen.resource.domain.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ToggleCommentLikeRequest {
    /** 评论或回复的统一评论 ID */
    @NotBlank
    private String commentId;
}
