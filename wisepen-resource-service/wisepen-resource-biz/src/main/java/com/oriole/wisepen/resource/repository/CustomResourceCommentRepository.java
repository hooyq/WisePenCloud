package com.oriole.wisepen.resource.repository;

import com.oriole.wisepen.resource.domain.entity.ResourceCommentEntity;
import com.oriole.wisepen.resource.enums.CommentSortBy;
import com.oriole.wisepen.resource.enums.CommentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomResourceCommentRepository {

    private final MongoTemplate mongoTemplate;

    public CustomResourceCommentRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<ResourceCommentEntity> listCommentsByResourceId(String resourceId, CommentSortBy sortBy, Pageable pageable) {
        Criteria criteria = Criteria.where("resourceId").is(resourceId).and("commentType").is(CommentType.COMMENT);
        Query query = Query.query(criteria);

        if (sortBy == CommentSortBy.CREATE_TIME) {
            query.with(Sort.by(Sort.Direction.DESC, "likeCount", "createTime"));
        } else {
            query.with(Sort.by(Sort.Direction.DESC, "createTime"));
        }
        query.with(pageable);

        long total = mongoTemplate.count(query, ResourceCommentEntity.class);
        List<ResourceCommentEntity> list = mongoTemplate.find(query, ResourceCommentEntity.class);
        return new PageImpl<>(list, pageable, total);
    }

    public Page<ResourceCommentEntity> listRepliesByRootCommentId(String rootCommentId, Pageable pageable) {
        Criteria criteria = Criteria.where("rootCommentId").is(rootCommentId).and("commentType").ne(CommentType.COMMENT);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createTime")).with(pageable);

        long total = mongoTemplate.count(query, ResourceCommentEntity.class);
        List<ResourceCommentEntity> list = mongoTemplate.find(query, ResourceCommentEntity.class);
        return new PageImpl<>(list, pageable, total);
    }

    /** 原子 $inc 更新回复数 */
    public void updateReplyCount(String commentId, int delta) {
        Query query = Query.query(Criteria.where("_id").is(commentId));
        Update update = new Update().inc("replyCount", delta);
        mongoTemplate.updateFirst(query, update, ResourceCommentEntity.class);
    }

    /** 原子 $inc 更新评论点赞数 */
    public void updateLikeCount(String commentId, int delta) {
        Query query = Query.query(Criteria.where("_id").is(commentId));
        Update update = new Update().inc("likeCount", delta);
        mongoTemplate.updateFirst(query, update, ResourceCommentEntity.class);
    }
}
