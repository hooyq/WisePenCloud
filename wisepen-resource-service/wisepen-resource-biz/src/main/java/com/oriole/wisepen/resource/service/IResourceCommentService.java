package com.oriole.wisepen.resource.service;

import com.oriole.wisepen.common.core.domain.PageR;
import com.oriole.wisepen.resource.domain.dto.req.CreateCommentRequest;
import com.oriole.wisepen.resource.domain.dto.req.CreateReplyRequest;
import com.oriole.wisepen.resource.domain.dto.req.DeleteCommentItemRequest;
import com.oriole.wisepen.resource.domain.dto.req.ToggleCommentLikeRequest;
import com.oriole.wisepen.resource.domain.dto.res.ResourceCommentItemResponse;
import com.oriole.wisepen.resource.enums.CommentSortBy;

public interface IResourceCommentService {

    String createComment(CreateCommentRequest request, String operatorUserId);

    String createReply(CreateReplyRequest request, String operatorUserId);

    void deleteCommentItem(DeleteCommentItemRequest request, String operatorUserId);

    boolean toggleLike(ToggleCommentLikeRequest request, String operatorUserId);

    PageR<ResourceCommentItemResponse> listComments(String resourceId, CommentSortBy sortBy, int size, int page);

    PageR<ResourceCommentItemResponse> listReplies(String rootCommentId, int size, int page);
}
