package com.oriole.wisepen.user.api.domain.dto.req;

import com.oriole.wisepen.user.api.constant.MessageValidationMsg;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageReadRequest {
    @NotNull(message = MessageValidationMsg.MESSAGE_ID_EMPTY)
    private Long messageId;
}
