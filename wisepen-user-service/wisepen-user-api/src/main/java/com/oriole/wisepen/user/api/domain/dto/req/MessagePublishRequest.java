package com.oriole.wisepen.user.api.domain.dto.req;

import com.oriole.wisepen.common.core.domain.enums.BusinessDomain;
import com.oriole.wisepen.user.api.enums.MessageDeliveryScope;
import com.oriole.wisepen.user.api.enums.MessageType;
import lombok.Data;

import java.util.List;

@Data
public class MessagePublishRequest {
    private List<Long> receiverUserIds;
    private MessageDeliveryScope deliveryScope;
    private MessageType messageType;
    private String title;
    private String content;
    private String jumpUrl;
    private BusinessDomain sourceService;
    private String bizTraceId;
    private String extra;
}
