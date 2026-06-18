package com.oriole.wisepen.resource.domain.dto.res;

import com.oriole.wisepen.resource.enums.ResourceAction;
import lombok.Data;

import java.util.List;

@Data
public class MarketSaleTierResponse {
    private String offerId;
    private List<ResourceAction> grantedActions; // 购买资源的用户可以获得的权限掩码
    private Integer price; // 售卖价格
}
