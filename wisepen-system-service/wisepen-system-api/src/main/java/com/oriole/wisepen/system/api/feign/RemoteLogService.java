package com.oriole.wisepen.system.api.feign;

import com.oriole.wisepen.common.core.domain.PageR;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.system.api.domain.dto.SysOperLogDTO;
import com.oriole.wisepen.system.api.domain.dto.SysOperLogQueryRequest;
import com.oriole.wisepen.system.api.domain.dto.SysOperLogQueryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteLogService", value = "wisepen-system-service")
public interface RemoteLogService {

    // 注意路径要和 Biz 中的 Controller 对应
    @PostMapping("/system/log/save")
    R<Boolean> saveLog(@RequestBody SysOperLogDTO sysOperLog);

    @PostMapping("/system/log/list")
    R<PageR<SysOperLogQueryResponse>> listLogs(@RequestBody SysOperLogQueryRequest query);
}
