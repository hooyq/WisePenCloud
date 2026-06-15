package com.oriole.wisepen.resource.domain.dto.res;

import com.oriole.wisepen.resource.domain.base.ResourceCommentBase;
import com.oriole.wisepen.user.api.domain.base.UserDisplayBase;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCommentItemResponse extends ResourceCommentBase {
    private String commentId;
    private UserDisplayBase authorInfo;
    private UserDisplayBase replyToUserInfo;
    private Long createTime;
    private Boolean deleted;
}