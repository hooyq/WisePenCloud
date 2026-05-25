package com.oriole.wisepen.resource.controller;

import com.oriole.wisepen.common.core.context.SecurityContextHolder;
import com.oriole.wisepen.common.core.domain.PageR;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.domain.enums.BusinessType;
import com.oriole.wisepen.common.core.domain.enums.GroupRoleType;
import com.oriole.wisepen.common.log.annotation.Log;
import com.oriole.wisepen.common.security.annotation.CheckLogin;
import com.oriole.wisepen.resource.domain.dto.res.SearchHitItemResponse;
import com.oriole.wisepen.resource.enums.SearchScope;
import com.oriole.wisepen.resource.service.ISearchQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 全文搜索 Controller
 */
@Tag(name = "全文搜索", description = "跨资源类型的全文搜索（带 ACL 可见性过滤 + 高亮）")
@RestController
@RequestMapping("/resource/search")
@RequiredArgsConstructor
@CheckLogin
public class SearchController {

    private final ISearchQueryService searchQueryService;

    @Operation(summary = "全局全文搜索")
    @Log(title = "全文搜索", businessType = BusinessType.SELECT, isSaveRequestData = false, isSaveResponseData = false)
    @GetMapping("/globalSearchResources")
    public R<PageR<SearchHitItemResponse>> searchResources(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "scope") SearchScope scope,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        String userId = SecurityContextHolder.getUserId().toString();
        Map<Long, GroupRoleType> groupRoleMap = SecurityContextHolder.getGroupRoleMap();

        return R.ok(searchQueryService.globalSearch(
                userId,
                groupRoleMap,
                keyword,
                scope,
                page,
                size
        ));
    }
}
