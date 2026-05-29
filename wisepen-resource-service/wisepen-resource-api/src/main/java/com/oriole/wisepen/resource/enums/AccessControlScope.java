package com.oriole.wisepen.resource.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccessControlScope {

    ALL(0, "ALL"),                // 全员
    ONLY_ADMIN(1, "ONLY_ADMIN"),  // 仅管理员
    WHITELIST(2, "WHITELIST"),    // 仅白名单内用户
    BLACKLIST(3, "BLACKLIST");    // 仅黑名单外用户

    @EnumValue
    @JsonValue
    private final int code;

    private final String value;
}
