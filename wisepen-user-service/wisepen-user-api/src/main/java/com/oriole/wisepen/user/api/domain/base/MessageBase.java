package com.oriole.wisepen.user.api.domain.base;

import com.oriole.wisepen.user.api.enums.MessageDeliveryScope;
import com.oriole.wisepen.user.api.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MessageBase {
    private MessageDeliveryScope deliveryScope;
    private MessageType messageType;
    private String title;
    private String content;
    private String jumpUrl;
    private String extra;
}
