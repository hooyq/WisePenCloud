package com.oriole.wisepen.resource.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComputedGroupAcl {
    // 在该小组内，普通成员的最终基础权限掩码
    private Integer baseMask = 0;
    // 在该小组内，拥有特殊权限的用户掩码
    private Map<String, Integer> userMasks = new HashMap<>();
}