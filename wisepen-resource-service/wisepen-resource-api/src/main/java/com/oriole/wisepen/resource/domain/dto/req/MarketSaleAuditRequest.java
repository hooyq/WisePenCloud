package com.oriole.wisepen.resource.domain.dto.req;

import com.oriole.wisepen.resource.constant.ResourceValidationMsg;
import com.oriole.wisepen.resource.enums.MarketSaleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarketSaleAuditRequest {
    @NotBlank(message = ResourceValidationMsg.RESOURCE_ID_NOT_BLANK)
    private String resourceId;

    @NotBlank(message = ResourceValidationMsg.GROUP_ID_NOT_BLANK)
    private String marketGroupId;

    @NotNull(message = ResourceValidationMsg.MARKET_AUDIT_STATUS_NOT_NULL)
    private MarketSaleStatus status;

    @NotNull(message = ResourceValidationMsg.MARKET_SALE_VERSION_NOT_NULL)
    private Integer offerVersion;

    private String auditMessage;
}
