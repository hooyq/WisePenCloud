package com.oriole.wisepen.resource.domain.base;

import com.oriole.wisepen.resource.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MarketPurchaseBase {
    private String listingId;
    private String buyerId;
    private String sellerId;
    private String sourceResourceId;
    private String forkedResourceId;
    private Integer paidPrice;
    private Long forkedVersion;
    private Integer listingRevision;
    private String tradeTraceId;
    private ResourceType resourceType;
}
