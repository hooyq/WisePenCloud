package com.oriole.wisepen.resource.domain.base;

import com.oriole.wisepen.resource.enums.MarketSaleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MarketSaleInfoBase {
    // 预览配置
    private Integer reviewActionsMask; // 未购买资源的用户可以获得的权限掩码
    // 预览配置
    private int reviewContentPercentage; // 可预览内容百分比

    // 售卖档位信息
    private List<MarketSaleTierBase> marketSaleTiers;

    // 售卖状态
    private MarketSaleStatus status = MarketSaleStatus.PENDING_REVIEW; // 状态
    private Integer offerVersion; // 指定售卖版本
}
