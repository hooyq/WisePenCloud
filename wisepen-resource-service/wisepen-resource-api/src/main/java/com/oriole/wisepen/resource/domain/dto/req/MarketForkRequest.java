package com.oriole.wisepen.resource.domain.dto.req;

import com.oriole.wisepen.resource.constant.ResourceValidationMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarketForkRequest {
    @NotBlank(message = ResourceValidationMsg.MARKET_PURCHASE_ID_NOT_BLANK)
    private String purchaseId;

    private String pathTagId;
}
