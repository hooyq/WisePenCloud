package com.oriole.wisepen.user.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    SYSTEM(1, "SYSTEM"),
    GROUP(2, "NORMAL");

    private final int code;

    @EnumValue
    @JsonValue
    private final String value;
}
