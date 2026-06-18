package com.oriole.wisepen.resource.controller;

import com.oriole.wisepen.common.core.context.SecurityContextHolder;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.domain.enums.BusinessType;
import com.oriole.wisepen.common.core.domain.enums.IdentityType;
import com.oriole.wisepen.common.log.annotation.Log;
import com.oriole.wisepen.common.security.annotation.CheckRole;
import com.oriole.wisepen.resource.domain.dto.req.MarketSaleAuditRequest;
import com.oriole.wisepen.resource.service.IMarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理员 - 集市", description = "平台管理员审核集市销售信息")
@RestController
@RequestMapping("/admin/market")
@RequiredArgsConstructor
@CheckRole(IdentityType.ADMIN)
public class AdminMarketController {

    private final IMarketService marketService;

    @Operation(
            summary = "审核资源",
            description = """
                    - 用途：平台管理员审核资源在指定集市组中的整组销售信息。
                    - 请求：resourceId 指定资源；marketGroupId 使用原始集市组ID，定位该资源在目标集市组中的销售信息；offerVersion 必须匹配当前提交版本；status 是审核后写入的目标状态；auditMessage 是审核说明。
                    - 约束：当前用户必须是平台管理员；集市销售信息必须存在；offerVersion 必须与当前提交版本一致；status 为 REJECTED 或 BANNED 时必须填写 auditMessage。
                    - 处理：写入 marketSaleInfo 的目标状态、审核说明、审核时间和审核人；状态为 PUBLISHED 时移除该集市组 override，其他状态设置 override=0；仅当状态在 PUBLISHED 与非 PUBLISHED 之间切换时触发 ACL 重算。
                    - 失败：当前身份不是平台管理员 -> PermissionError.UNAUTHORIZED；资源不存在 -> ResourceError.RESOURCE_NOT_FOUND；集市销售信息不存在 -> ResourceError.MARKET_SALE_INFO_NOT_FOUND；驳回或封禁未填写审核说明 -> ResourceError.MARKET_AUDIT_MESSAGE_INVALID；审核版本与当前提交版本不一致 -> ResourceError.MARKET_AUDIT_VERSION_CONFLICT。
                    - 响应：成功时返回空结果。
                    """
    )
    @Log(title = "审核资源", businessType = BusinessType.UPDATE)
    @PostMapping("/auditSale")
    public R<Void> auditSale(@Valid @RequestBody MarketSaleAuditRequest request) {
        marketService.auditSaleInfo(request, SecurityContextHolder.getUserId().toString());
        return R.ok();
    }
}
