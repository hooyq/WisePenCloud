package com.oriole.wisepen.ai.asset.domain.entity;

import com.oriole.wisepen.ai.asset.domain.base.SkillVersionBundleBase;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wisepen_skill_versions")
public class SkillVersionBundleEntity extends SkillVersionBundleBase {
    @Id
    private String id;

    private String resourceId;

    @CreatedDate
    private LocalDateTime createTime;

    @LastModifiedDate
    private LocalDateTime updateTime;
}
