package com.oriole.wisepen.resource.controller;

import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.resource.domain.dto.*;
import com.oriole.wisepen.resource.domain.dto.res.ResourceItemResponse;
import com.oriole.wisepen.resource.feign.RemoteResourceService;
import com.oriole.wisepen.resource.service.IGroupResService;
import com.oriole.wisepen.resource.service.IResourceService;
import com.oriole.wisepen.resource.service.ITagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "内部资源服务", description = "供其他微服务注册资源、查询资源与校验权限")
@RestController
@RequestMapping("/internal/resource")
@RequiredArgsConstructor
public class InternalResourceItemController implements RemoteResourceService {

    // 内部 Feign 接口，不打 @Log。被调用方（Document/User Controller）负责在自己的入口处审计。
    private final IResourceService resourceService;
    private final IGroupResService groupResService;
    private final ITagService tagService;

    // 注册/新增资源摘要
    @Operation(summary = "内部注册资源")
    @Log(title = "创建资源", businessType = BusinessType.INSERT)
    @PostMapping("/addRes")
    public R<String> createResource(@Validated @RequestBody ResourceCreateReqDTO dto) {
        String resourceId = resourceService.createResourceItem(dto);
        return R.ok(resourceId);
    }

    // 同步修改资源属性
    @Operation(summary = "内部更新资源属性")
    @PostMapping("/changeResAttr")
    public R<Void> updateAttributes(@Validated @RequestBody ResourceUpdateReqDTO dto) {
        resourceService.updateResourceAttributes(dto);
        return R.ok();
    }

    @Operation(summary = "内部获取资源信息")
    @PostMapping("/getResourceInfo")
    public R<ResourceItemResponse> getResourceInfo(ResourceInfoGetReqDTO dto) {
        ResourceItemResponse response = resourceService.getResourceInfo(dto);
        return R.ok(response);
    }

    // 内部鉴权接口，供下游微服务在执行敏感操作（如：导出PDF、分享链接）前进行硬核鉴权
    @Operation(summary = "内部检查资源权限")
    @PostMapping("/checkResPermission")
    public R<ResourceCheckPermissionResDTO> checkResPermission(ResourceCheckPermissionReqDTO dto) {
        ResourceCheckPermissionResDTO hasPermission = resourceService.checkPermission(dto);
        return R.ok(hasPermission);
    }

    // 小组解散：软删除 Tag 树与配置
    @Operation(summary = "内部清理已解散小组资源")
    @PostMapping("/dissolveGroup")
    public R<Void> dissolveGroup(@RequestParam("groupId") Long groupId) {
        tagService.softRemoveAllTagByGroupId(groupId.toString());
        groupResService.softRemoveGroupResConfigByGroupId(groupId.toString());
        return R.ok();
    }
}
