package com.oriole.wisepen.resource.domain.dto.res;

import com.oriole.wisepen.resource.domain.base.MarketListingBase;
import com.oriole.wisepen.resource.domain.base.ResourceInteractionInfoBase;
import com.oriole.wisepen.user.api.domain.base.UserDisplayBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class MarketListingResponse extends MarketListingBase {
    private UserDisplayBase sellerInfo;
    private Map<String, String> currentTags;
    private ResourceInteractionInfoBase resourceInteractionInfo;
}
