package com.oriole.wisepen.resource.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketListingStatus {
    LISTED(1, "LISTED"),
    OFF_SHELF(2, "OFF_SHELF");

    private final int code;

    @EnumValue
    @JsonValue
    private final String value;
}
