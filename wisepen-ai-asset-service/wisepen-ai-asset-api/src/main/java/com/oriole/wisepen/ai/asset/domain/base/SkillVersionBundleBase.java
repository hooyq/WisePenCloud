package com.oriole.wisepen.ai.asset.domain.base;

import com.oriole.wisepen.ai.asset.enums.SkillVersionStatus;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public class SkillVersionBundleBase {
    private Integer version;
    private SkillAssetInfoBase mainSkillMD;
    private SkillVersionStatus status;
    @Default
    private List<SkillAssetInfoBase> skillAssets = new ArrayList<>();
}
