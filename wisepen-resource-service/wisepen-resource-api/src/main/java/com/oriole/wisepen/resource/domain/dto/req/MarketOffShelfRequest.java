package com.oriole.wisepen.resource.domain.dto.req;

import com.oriole.wisepen.resource.constant.ResourceValidationMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarketOffShelfRequest {
    @NotBlank(message = ResourceValidationMsg.MARKET_LISTING_ID_NOT_BLANK)
    private String listingId;

    @NotBlank(message = ResourceValidationMsg.GROUP_ID_NOT_BLANK)
    private String marketGroupId;
}
