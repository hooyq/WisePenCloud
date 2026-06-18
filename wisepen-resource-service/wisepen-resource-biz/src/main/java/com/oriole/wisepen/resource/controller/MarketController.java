package com.oriole.wisepen.resource.controller;

import com.oriole.wisepen.common.core.context.SecurityContextHolder;
import com.oriole.wisepen.common.core.domain.PageR;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.domain.enums.BusinessType;
import com.oriole.wisepen.common.log.annotation.Log;
import com.oriole.wisepen.common.security.annotation.CheckLogin;
import com.oriole.wisepen.resource.domain.dto.req.MarketSalePublishRequest;
import com.oriole.wisepen.resource.domain.dto.req.MarketSaleOffShelfRequest;
import com.oriole.wisepen.resource.domain.dto.req.MarketPurchaseRequest;
import com.oriole.wisepen.resource.domain.dto.res.MarketOrderResponse;
import com.oriole.wisepen.resource.service.IMarketService;
import com.oriole.wisepen.resource.service.IResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "集市", description = "资源上架、购买和订单查询")
@RestController
@RequestMapping("/resource/market")
@RequiredArgsConstructor
@CheckLogin
@Validated
public class MarketController {

    private final IMarketService marketService;
    private final IResourceService resourceService;


    @Operation(
            summary = "提交上架信息",
            description = """
                    - 用途：资源所有者提交或修改资源集市上架信息。
                    - 请求：resourceId 指定资源；marketGroupId 指定集市组；tagIds 指定集市标签；offerVersion 指定本次审核版本；marketmarketSaleTiers 指定售卖档位。
                    - 约束：当前用户必须是资源所有者；目标小组必须是集市组；offerVersion 和售卖档位不能为空；预览权限和售卖权限禁止包含 EDIT。
                    - 处理：覆盖该集市组绑定的 tagIds 和整组 marketOffers；已发布或已下架的同 offerVersion 更新直接保持 PUBLISHED 并移除 override；新版本或未审核版本进入 PENDING 并设置 override=0。
                    - 失败：资源不存在 -> ResourceError.RESOURCE_NOT_FOUND；当前用户不是资源所有者 -> ResourceError.RESOURCE_PERMISSION_DENIED；目标小组不是集市组 -> ResourceError.MARKET_GROUP_REQUIRED；上架记录已封禁 -> ResourceError.MARKET_OFFER_BANNED；权限包含 EDIT -> ResourceError.MARKET_FORBIDDEN_ACTION_INCLUDED；售卖档位权限重复 -> ResourceError.MARKET_OFFER_ACTIONS_DUPLICATED。
                    - 响应：成功时返回空结果。
                    """
    )
    @Log(title = "提交上架信息", businessType = BusinessType.INSERT)
    @PostMapping("/publishSaleInfo")
    public R<Void> publishSaleInfo(@Valid @RequestBody MarketSalePublishRequest request) {
        String userId = SecurityContextHolder.getUserId().toString();

        resourceService.assertResourceOwner(request.getResourceId(), userId);
        marketService.publishSaleInfo(request);
        return R.ok();
    }

    @Operation(
            summary = "下架资源",
            description = """
                    - 用途：卖家或集市管理员将已提交到集市的资源下架。
                    - 请求：resourceId 指定已上架资源；marketGroupId 指定集市组。
                    - 约束：目标小组必须是集市组；当前用户必须是卖家本人或该集市群 OWNER、ADMIN。
                    - 处理：整组上架状态置为 OFF_SHELF，并设置该 Market group 的 override=0；不删除已有订单。
                    - 失败：上架记录不存在 -> ResourceError.MARKET_OFFER_NOT_FOUND；目标小组不是集市组 -> ResourceError.MARKET_GROUP_REQUIRED；当前用户无权操作 -> ResourceError.RESOURCE_PERMISSION_DENIED。
                    - 响应：成功时返回空结果。
                    """
    )
    @Log(title = "下架资源", businessType = BusinessType.UPDATE)
    @PostMapping("/offShelfSaleInfo")
    public R<Void> offShelfSaleInfo(@Valid @RequestBody MarketSaleOffShelfRequest request) {
        String userId = SecurityContextHolder.getUserId().toString();

        resourceService.assertResourceOwner(request.getResourceId(), userId);
        marketService.offShelfSaleInfo(request);
        return R.ok();
    }

    @Operation(
            summary = "购买资源",
            description = """
                    - 用途：买家购买集市资源的售卖档位权益。
                    - 请求：resourceId 指定已上架资源；marketGroupId 指定集市组；offerId 指定要购买的售卖档位。
                    - 约束：上架记录必须处于 PUBLISHED；目标小组必须是集市组且资源仍绑定在该集市群；买家不能购买自己上架的资源。
                    - 处理：按 offerId 选中售卖档位并结算；创建订单并记录购买版本；将 purchased mask 按位或写入 marketSpecifiedUsersGrantedActionsMask，随后触发 ACL 重算。
                    - 失败：上架记录不存在 -> ResourceError.MARKET_OFFER_NOT_FOUND；资源未上架或已下架 -> ResourceError.MARKET_OFFER_NOT_ACTIVE；不能购买自己上架的资源 -> ResourceError.MARKET_SELF_ORDER_NOT_ALLOWED；售卖档位不存在或已失效 -> ResourceError.MARKET_OFFER_ID_INVALID；重复购买 -> ResourceError.MARKET_ORDER_ALREADY_EXISTS。
                    - 响应：返回购买记录信息。
                    """
    )
    @Log(title = "购买资源", businessType = BusinessType.INSERT)
    @PostMapping("/purchaseResource")
    public R<MarketOrderResponse> purchaseResource(@Valid @RequestBody MarketPurchaseRequest request) {
        String userId = SecurityContextHolder.getUserId().toString();

        return R.ok(marketService.purchaseResource(request, userId));
    }

    @Operation(
            summary = "分页查询我的购买",
            description = """
                    - 用途：查询当前用户购买过的集市资源订单，供用户查看已购买动作和购买版本。
                    - 请求：page、size 控制分页。
                    - 约束：当前用户必须已登录。
                    - 处理：按当前用户 buyerId 分页查询订单，返回购买动作掩码、支付价格和购买版本；不校验原资源当前是否仍可访问。
                    - 失败：无业务失败分支。
                    - 响应：返回当前用户购买记录分页。
                    """
    )
    @GetMapping("/listOrders")
    public R<PageR<MarketOrderResponse>> listOrders(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) int size) {
        return R.ok(marketService.listOrders(SecurityContextHolder.getUserId().toString(), page, size));
    }
}
