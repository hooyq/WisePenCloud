package com.oriole.wisepen.ai.asset.domain.dto.res;

import com.oriole.wisepen.ai.asset.domain.base.SkillVersionBundleBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SkillVersionBundleInfoResponse extends SkillVersionBundleBase {
    private String resourceId;
}
