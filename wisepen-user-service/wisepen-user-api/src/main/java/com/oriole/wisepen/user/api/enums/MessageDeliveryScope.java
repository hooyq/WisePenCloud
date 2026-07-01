package com.oriole.wisepen.user.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageDeliveryScope {
    DIRECT(1, "DIRECT"),
    ALL_USERS(2, "ALL_USERS");

    private final int code;

    @EnumValue
    @JsonValue
    private final String value;
}
