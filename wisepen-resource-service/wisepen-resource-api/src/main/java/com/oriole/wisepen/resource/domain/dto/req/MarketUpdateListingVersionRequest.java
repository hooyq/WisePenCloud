package com.oriole.wisepen.resource.domain.dto.req;

import com.oriole.wisepen.resource.constant.ResourceValidationMsg;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarketUpdateListingVersionRequest {
    @NotBlank(message = ResourceValidationMsg.MARKET_LISTING_ID_NOT_BLANK)
    private String listingId;

    @NotNull(message = ResourceValidationMsg.MARKET_VERSION_NOT_NULL)
    @Min(value = 0, message = ResourceValidationMsg.MARKET_VERSION_INVALID)
    private Long listedVersion;
}
