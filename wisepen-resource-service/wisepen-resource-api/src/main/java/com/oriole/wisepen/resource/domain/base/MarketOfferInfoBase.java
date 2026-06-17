package com.oriole.wisepen.resource.domain.base;

import com.oriole.wisepen.resource.enums.MarketOfferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MarketOfferInfoBase {
    // 售卖详情
    private int grantedActionsMask; // 购买资源的用户可以获得的权限掩码
    private Integer price; // 售卖价格
    private LocalDateTime editAt;
}
