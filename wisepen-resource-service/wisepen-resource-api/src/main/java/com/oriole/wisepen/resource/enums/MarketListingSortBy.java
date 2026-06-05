package com.oriole.wisepen.resource.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketListingSortBy {
    UPDATE_TIME(1, "UPDATE_TIME", "updateTime", false),
    CREATE_TIME(2, "CREATE_TIME", "createTime", false),
    PRICE(3, "PRICE", "price", false),
    NAME(4, "NAME", "resourceName", false),
    SIZE(5, "SIZE", "size", false),
    READ_COUNT(6, "READ_COUNT", "resourceInteractionInfo.readCount", true),
    LIKE_COUNT(7, "LIKE_COUNT", "resourceInteractionInfo.likeCount", true),
    SCORE_COUNT(8, "SCORE_COUNT", "resourceInteractionInfo.scoreCount", true),
    SCORE_TOTAL(9, "SCORE_TOTAL", "resourceInteractionInfo.scoreTotal", true);

    private final int code;

    @EnumValue
    @JsonValue
    private final String value;

    private final String dbField;
    private final boolean interactionField;
}
