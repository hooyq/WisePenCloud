package com.oriole.wisepen.document.controller;

import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.document.api.feign.RemoteDocumentService;
import com.oriole.wisepen.document.service.IDocumentService;
import com.oriole.wisepen.resource.domain.dto.req.ResourceForkRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal/document")
@RequiredArgsConstructor
public class InternalDocumentController implements RemoteDocumentService {

    private final IDocumentService documentService;

    @Override
    @PostMapping("/forkDocument")
    public R<Void> forkDocument(@Valid @RequestBody ResourceForkRequest request) {
        documentService.forkDocument(request);
        return R.ok();
    }
}
