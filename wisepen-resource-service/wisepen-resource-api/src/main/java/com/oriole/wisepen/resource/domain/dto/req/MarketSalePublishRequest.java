package com.oriole.wisepen.resource.domain.dto.req;

import com.oriole.wisepen.resource.constant.ResourceValidationMsg;
import com.oriole.wisepen.resource.enums.ResourceAction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MarketSalePublishRequest {

    @Data
    public static class MarketSaleTier {
        @NotEmpty
        private List<ResourceAction> grantedActions; // 购买资源的用户可以获得的权限掩码

        @NotNull(message = ResourceValidationMsg.MARKET_SALE_TIER_PRICE_NOT_NULL)
        @Min(value = 0, message = ResourceValidationMsg.MARKET_SALE_TIER_PRICE_INVALID)
        private Integer price; // 售卖价格
    }

    @NotBlank(message = ResourceValidationMsg.RESOURCE_ID_NOT_BLANK)
    private String resourceId;

    @NotBlank(message = ResourceValidationMsg.GROUP_ID_NOT_BLANK)
    private String marketGroupId;

    @NotEmpty(message = ResourceValidationMsg.TAG_IDS_NOT_NULL)
    private List<String> tagIds;

    @Min(value = 0, message = ResourceValidationMsg.MARKET_REVIEW_CONTENT_PERCENTAGE_INVALID)
    @Max(value = 100, message = ResourceValidationMsg.MARKET_REVIEW_CONTENT_PERCENTAGE_INVALID)
    private int reviewContentPercentage;

    private List<ResourceAction> reviewActions;

    @Valid
    @NotEmpty(message = ResourceValidationMsg.MARKET_SALE_TIER_LIST_NOT_EMPTY)
    private List<MarketSaleTier> marketSaleTiers;

    @NotNull(message = ResourceValidationMsg.MARKET_SALE_VERSION_NOT_NULL)
    @Min(value = 1, message = ResourceValidationMsg.MARKET_SALE_VERSION_INVALID)
    private Integer offerVersion;
}
