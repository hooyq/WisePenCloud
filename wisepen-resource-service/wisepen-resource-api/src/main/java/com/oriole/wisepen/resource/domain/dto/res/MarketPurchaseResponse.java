package com.oriole.wisepen.resource.domain.dto.res;

import com.oriole.wisepen.resource.domain.base.MarketPurchaseBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MarketPurchaseResponse extends MarketPurchaseBase {
    private String purchaseId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
