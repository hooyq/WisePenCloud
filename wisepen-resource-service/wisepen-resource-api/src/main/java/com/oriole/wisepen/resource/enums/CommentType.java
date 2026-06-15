package com.oriole.wisepen.resource.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentType {
    COMMENT(1, "COMMENT"),
    REPLY_TO_COMMENT(2, "REPLY_TO_COMMENT"),
    REPLY_TO_REPLY(3, "REPLY_TO_REPLY");

    private final int code;

    @EnumValue
    @JsonValue
    private final String value;
}
