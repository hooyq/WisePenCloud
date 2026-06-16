package com.oriole.wisepen.ai.asset.domain.base;

import com.oriole.wisepen.ai.asset.enums.AIResourceSourceType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class AIResourceInfoBase {
    private String name;
    private String description;
    private Integer version;
    private AIResourceSourceType sourceType;
}
