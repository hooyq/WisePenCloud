package com.oriole.wisepen.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.oriole.wisepen.common.core.domain.PageR;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.resource.domain.dto.req.CreateCommentRequest;
import com.oriole.wisepen.resource.domain.dto.req.CreateReplyRequest;
import com.oriole.wisepen.resource.domain.dto.req.DeleteCommentItemRequest;
import com.oriole.wisepen.resource.domain.dto.req.ToggleCommentLikeRequest;
import com.oriole.wisepen.resource.domain.dto.res.ResourceCommentItemResponse;
import com.oriole.wisepen.resource.domain.entity.ResourceCommentEntity;
import com.oriole.wisepen.resource.domain.entity.ResourceItemEntity;
import com.oriole.wisepen.resource.domain.entity.ResourceUserInteractionRecordEntity;
import com.oriole.wisepen.resource.enums.CommentSortBy;
import com.oriole.wisepen.resource.enums.CommentType;
import com.oriole.wisepen.resource.exception.ResourceError;
import com.oriole.wisepen.resource.repository.*;
import com.oriole.wisepen.resource.service.IResourceCommentService;
import com.oriole.wisepen.user.api.domain.base.UserDisplayBase;
import com.oriole.wisepen.user.api.feign.RemoteUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceCommentServiceImpl implements IResourceCommentService {

    private final ResourceItemRepository resourceItemRepository;
    private final ResourceCommentRepository commentRepository;
    private final ResourceUserInteractionRecordRepository resourceUserInteractionRecordRepository;
    private final CustomResourceCommentRepository customCommentRepository;
    private final CustomResourceItemRepository customResourceItemRepository;
    private final CustomResourceUserInteractionRecordRepository customInteractionRecordRepository;
    private final RemoteUserService remoteUserService;

    @Override
    public String createComment(CreateCommentRequest request, String operatorUserId) {
        String resourceId = request.getResourceId();
        ResourceItemEntity resource = resourceItemRepository.findById(resourceId)
                .orElseThrow(() -> new ServiceException(ResourceError.RESOURCE_NOT_FOUND));
        if (resource.getDeletedAt() != null) throw new ServiceException(ResourceError.RESOURCE_NOT_FOUND);

        ResourceCommentEntity comment = ResourceCommentEntity.builder()
                .resourceId(resourceId).authorId(operatorUserId)
                .content(request.getContent()).imageUrls(request.getImageUrls())
                .commentType(CommentType.COMMENT)
                .build();
        comment = commentRepository.save(comment);
        // 新增资源评论计数
        customResourceItemRepository.updateCommentCount(resourceId, 1);
        log.info("comment created. resourceId={} commentId={} authorId={}", resourceId, comment.getCommentId(), operatorUserId);
        return comment.getCommentId();
    }

    @Override
    public String createReply(CreateReplyRequest request, String operatorUserId) {
        // 检查回复的Comment是否存在
        ResourceCommentEntity replyToComment = commentRepository.findByIdAndDeletedAtIsNull(request.getReplyTo())
                .orElseThrow(() -> new ServiceException(ResourceError.COMMENT_NOT_FOUND));

        // 如果回复的Comment是COMMENT类型的，那么当前的Comment类型是REPLY_TO_COMMENT
        // 如果回复的Comment是REPLY_TO_COMMENT、REPLY_TO_REPLY类型的，那么当前的Comment类型是REPLY_TO_REPLY
        CommentType replyType = CommentType.COMMENT.equals(replyToComment.getCommentType()) ? CommentType.REPLY_TO_COMMENT : CommentType.REPLY_TO_REPLY;

        // 如果当前的Comment类型是REPLY_COMMENT类型的，根为回复的Comment的CommentId
        // 如果当前的Comment类型是RREPLY_TO_REPLY类型的，根为回复的Comment的RootCommentId
        String rootCommentId = CommentType.REPLY_TO_COMMENT.equals(replyType) ? replyToComment.getCommentId() : replyToComment.getRootCommentId();

        ResourceCommentEntity reply = ResourceCommentEntity.builder()
                .resourceId(replyToComment.getResourceId()).authorId(operatorUserId)
                .content(request.getContent()).imageUrls(request.getImageUrls())
                .replyToUserId(replyToComment.getAuthorId()).replyTo(replyToComment.getCommentId()).rootCommentId(rootCommentId)
                .commentType(replyType)
                .build();
        reply = commentRepository.save(reply);

        // 新增评论回复计数
        customCommentRepository.updateReplyCount(rootCommentId, 1);
        // 新增资源评论计数
        customResourceItemRepository.updateCommentCount(replyToComment.getResourceId(), 1);

        log.info("reply created. resourceId={} rootCommentId={} replyToCommentId={} commentId={} authorId={}",
                replyToComment.getResourceId(), rootCommentId, replyToComment.getCommentId(), reply.getCommentId(), operatorUserId);
        return reply.getCommentId();
    }

    @Override
    public void deleteCommentItem(DeleteCommentItemRequest request, String operatorUserId) {
        String commentId = request.getCommentId();
        ResourceCommentEntity comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ServiceException(ResourceError.COMMENT_NOT_FOUND));

        // TODO: 管理员，资源所有者，评论者本人可以删除评论
        if (!comment.getAuthorId().equals(operatorUserId)) {
            throw new ServiceException(ResourceError.COMMENT_DELETE_ACCESS_DENIED);
        }

        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);

        // 减少资源评论计数
        customResourceItemRepository.updateCommentCount(comment.getResourceId(), -1);

        if (CommentType.COMMENT.equals(comment.getCommentType())) {
            log.info("comment deleted. commentId={} operatorUserId={}", commentId, operatorUserId);
        } else {
            // 如果被删除的是REPLY，还减少评论回复计数
            customResourceItemRepository.updateCommentCount(comment.getResourceId(), -1);
            log.info("reply deleted. commentId={} rootCommentId={} replyToCommentId={} operatorUserId={}",
                    commentId, comment.getRootCommentId(), comment.getReplyTo(), operatorUserId);
        }
    }

    @Override
    public boolean toggleLike(ToggleCommentLikeRequest request, String operatorUserId) {
        String commentId = request.getCommentId();
        ResourceCommentEntity comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ServiceException(ResourceError.COMMENT_NOT_FOUND));
        String resourceId = comment.getResourceId();

        // 加载用户互动记录，判断 commentId 是否在 likedCommentIds
        ResourceUserInteractionRecordEntity record =
                resourceUserInteractionRecordRepository.findByUserIdAndResourceId(operatorUserId, resourceId).orElse(null);
        boolean alreadyLiked = record != null && record.getLikedCommentIds() != null && record.getLikedCommentIds().contains(commentId);

        if (alreadyLiked) {
            customInteractionRecordRepository.pullFromLikedCommentIds(resourceId, operatorUserId, commentId);
            customCommentRepository.updateLikeCount(commentId, -1); // 评论赞数减少
            log.info("comment like removed. commentId={} resourceId={} operatorUserId={}", commentId, resourceId, operatorUserId);
            return false;
        } else {
            customInteractionRecordRepository.addToLikedCommentIds(resourceId, operatorUserId, commentId);
            customCommentRepository.updateLikeCount(commentId, 1); // 评论赞数增加
            log.info("comment like added. commentId={} resourceId={} operatorUserId={}", commentId, resourceId, operatorUserId);
            return true;
        }
    }

    // 远程批量请求评论者信息
    private Map<Long, UserDisplayBase> fetchCommentAuthorsInfo(List<ResourceCommentEntity> entities) {
        List<Long> ownerIds = entities.stream()
                .map(ResourceCommentEntity::getAuthorId)
                .filter(StringUtils::hasText)
                .map(Long::valueOf).distinct().toList();
        try {
            Map<Long, UserDisplayBase> fetched = remoteUserService.getUserDisplayInfo(ownerIds).getData();
            return fetched == null ? Collections.emptyMap() : fetched;
        } catch (Exception e) {
            log.warn("comment author info batch degraded. ownerCount={}", ownerIds.size(), e);
            return Collections.emptyMap();
        }
    }

    @Override
    public PageR<ResourceCommentItemResponse> listComments(String resourceId, CommentSortBy sortBy, int size, int page) {
        Pageable pageable =  PageRequest.of(page - 1, size);

        Page<ResourceCommentEntity> resourceCommentEntities = customCommentRepository.listCommentsByResourceId(resourceId, sortBy, pageable);

        // 批量查询作者信息
        Map<Long, UserDisplayBase> userMap = fetchCommentAuthorsInfo(resourceCommentEntities.toList());

        List<ResourceCommentItemResponse> responses = resourceCommentEntities.stream().map(entity -> {
            ResourceCommentItemResponse item = BeanUtil.copyProperties(entity, ResourceCommentItemResponse.class);
            item.setDeleted(entity.getDeletedAt() != null);
            item.setAuthorInfo(userMap.get(Long.parseLong(entity.getAuthorId())));
            return item;
        }).toList();

        PageR<ResourceCommentItemResponse> pageR = new PageR<>(resourceCommentEntities.getTotalElements(), page, size);
        pageR.addAll(responses);
        return pageR;
    }

    @Override
    public PageR<ResourceCommentItemResponse> listReplies(String rootCommentId, int size, int page) {
        Pageable pageable =  PageRequest.of(page - 1, size);

        Page<ResourceCommentEntity> resourceCommentEntities = customCommentRepository.listRepliesByRootCommentId(rootCommentId, pageable);

        // 批量查询作者信息
        Map<Long, UserDisplayBase> userMap = fetchCommentAuthorsInfo(resourceCommentEntities.toList());

        List<ResourceCommentItemResponse> responses = resourceCommentEntities.stream().map(entity -> {
            ResourceCommentItemResponse item = BeanUtil.copyProperties(entity, ResourceCommentItemResponse.class);
            item.setDeleted(entity.getDeletedAt() != null);
            item.setAuthorInfo(userMap.get(Long.parseLong(entity.getAuthorId())));
            item.setReplyToUserInfo(userMap.get(Long.parseLong(entity.getReplyToUserId())));
            return item;
        }).toList();

        PageR<ResourceCommentItemResponse> pageR = new PageR<>(resourceCommentEntities.getTotalElements(), page, size);
        pageR.addAll(responses);
        return pageR;
    }
}
