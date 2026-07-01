package com.oriole.wisepen.user.api.feign;

import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.user.api.domain.dto.req.MessagePublishRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserMessageService", value = "wisepen-user-service")
public interface RemoteUserMessageService {

    @PostMapping("/internal/user/message/publishMessage")
    R<Void> publishMessage(@RequestBody @Valid MessagePublishRequest req);
}
