package com.oriole.wisepen.document.api.feign;

import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.resource.domain.dto.req.ResourceForkRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "内部文档服务", description = "提供给其他微服务的文档内部接口")
@FeignClient(contextId = "remoteDocumentService", value = "wisepen-document-service")
public interface RemoteDocumentService {

    @Operation(summary = "复制文档", description = "version=0 表示当前文档内容")
    @PostMapping("/internal/document/forkDocument")
    R<Void> forkDocument(@RequestBody ResourceForkRequest request);
}
