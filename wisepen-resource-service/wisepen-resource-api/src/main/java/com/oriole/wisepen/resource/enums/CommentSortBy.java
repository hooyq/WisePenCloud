package com.oriole.wisepen.resource.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentSortBy {
    CREATE_TIME(1, "CREATE_TIME", "createTime"),
    LIKE_COUNT(2, "LIKE_COUNT", "likeCount");

    private final int code;

    @EnumValue
    @JsonValue
    private final String value;

    private final String dbField;
}
