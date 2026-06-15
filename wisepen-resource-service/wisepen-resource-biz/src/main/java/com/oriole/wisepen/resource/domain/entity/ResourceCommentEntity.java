package com.oriole.wisepen.resource.domain.entity;

import com.oriole.wisepen.resource.domain.base.ResourceCommentBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wisepen_resource_comments")
@CompoundIndexes({
        @CompoundIndex(def = "{'resourceId': 1, 'commentType': 1, 'createTime': -1}"),
        @CompoundIndex(def = "{'resourceId': 1, 'commentType': 1, 'likeCount': -1, 'createTime': -1}"),
        @CompoundIndex(def = "{'rootCommentId': 1, 'createTime': -1}")
})
public class ResourceCommentEntity extends ResourceCommentBase {
    @Id
    private String commentId;

    // CommentType.COMMENT 时为 null
    // CommentType.REPLY_TO_COMMENT、CommentType.REPLY_TO_REPLY 时为所属顶级评论的 commentId
    private String rootCommentId;

    // CommentType.COMMENT 时为 null
    // CommentType.REPLY_TO_COMMENT 时为回复的 Comment 的 commentId
    // CommentType.REPLY_TO_REPLY 时为回复的 REPLY 的 commentId
    private String replyTo;

    @CreatedDate
    private LocalDateTime createTime;

    /** null 表示未软删除 */
    private LocalDateTime deletedAt;

    // 软删除时不显示
    @Override
    public String getContent() {
        return this.deletedAt != null ? null : super.getContent();
    }

    // 软删除时不显示
    @Override
    public List<String> getImageUrls() {
        return this.deletedAt != null ? null : super.getImageUrls();
    }
}
