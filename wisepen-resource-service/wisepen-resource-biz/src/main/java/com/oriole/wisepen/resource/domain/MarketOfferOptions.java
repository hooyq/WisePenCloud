package com.oriole.wisepen.resource.domain;

import com.oriole.wisepen.resource.domain.base.MarketOfferInfoBase;
import com.oriole.wisepen.resource.enums.MarketOfferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketOfferOptions {
    // 预览配置
    private int reviewContentPercentage; // 可预览内容百分比
    private int reviewActionsMask; // 未购买资源的用户可以获得的权限掩码

    // 已售资源权限
    private Map<String, Integer> marketSpecifiedUsersGrantedActionsMask; // Market自行管理

    // 售卖信息
    private List<MarketOfferInfoBase> marketOfferList;

    // 售卖状态
    private MarketOfferStatus status; // 状态
    private int offerVersion; // 指定售卖版本

    // 管理员审核
    private String auditorId;// 审核人 ID
    private String auditMessage;// 审核说明
    private LocalDateTime auditAt;// 审核完成时间
}