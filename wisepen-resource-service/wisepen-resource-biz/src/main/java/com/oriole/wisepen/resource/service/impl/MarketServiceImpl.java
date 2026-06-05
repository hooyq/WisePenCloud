package com.oriole.wisepen.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.oriole.wisepen.common.core.domain.PageR;
import com.oriole.wisepen.common.core.domain.enums.GroupRoleType;
import com.oriole.wisepen.common.core.domain.enums.GroupType;
import com.oriole.wisepen.common.core.domain.enums.list.SortDirectionEnum;
import com.oriole.wisepen.common.core.exception.ServiceException;
import com.oriole.wisepen.document.api.domain.dto.req.DocumentForkRequest;
import com.oriole.wisepen.document.api.feign.RemoteDocumentService;
import com.oriole.wisepen.note.api.domain.dto.req.NoteForkRequest;
import com.oriole.wisepen.note.api.feign.RemoteNoteService;
import com.oriole.wisepen.resource.domain.dto.ResourceCheckPermissionReqDTO;
import com.oriole.wisepen.resource.domain.dto.ResourceCheckPermissionResDTO;
import com.oriole.wisepen.resource.domain.dto.ResourceCreateReqDTO;
import com.oriole.wisepen.resource.domain.dto.req.MarketForkRequest;
import com.oriole.wisepen.resource.domain.dto.req.MarketListResourceRequest;
import com.oriole.wisepen.resource.domain.dto.req.MarketOffShelfRequest;
import com.oriole.wisepen.resource.domain.dto.req.MarketPurchaseRequest;
import com.oriole.wisepen.resource.domain.dto.req.MarketUpdateListingVersionRequest;
import com.oriole.wisepen.resource.domain.dto.res.MarketListingResponse;
import com.oriole.wisepen.resource.domain.dto.res.MarketPurchaseResponse;
import com.oriole.wisepen.resource.domain.entity.MarketListingEntity;
import com.oriole.wisepen.resource.domain.entity.MarketPurchaseEntity;
import com.oriole.wisepen.resource.domain.entity.ResourceInteractionInfoEntity;
import com.oriole.wisepen.resource.domain.entity.ResourceItemEntity;
import com.oriole.wisepen.resource.domain.entity.TagEntity;
import com.oriole.wisepen.resource.enums.MarketListingSortBy;
import com.oriole.wisepen.resource.enums.MarketListingStatus;
import com.oriole.wisepen.resource.enums.ResourceAccessRole;
import com.oriole.wisepen.resource.enums.ResourceAction;
import com.oriole.wisepen.resource.enums.ResourceType;
import com.oriole.wisepen.resource.exception.ResourceError;
import com.oriole.wisepen.resource.repository.MarketListingRepository;
import com.oriole.wisepen.resource.repository.MarketPurchaseRepository;
import com.oriole.wisepen.resource.repository.ResourceInteractionInfoRepository;
import com.oriole.wisepen.resource.repository.ResourceItemRepository;
import com.oriole.wisepen.resource.repository.TagRepository;
import com.oriole.wisepen.resource.service.IMarketService;
import com.oriole.wisepen.resource.service.IResourceService;
import com.oriole.wisepen.user.api.domain.base.GroupDisplayBase;
import com.oriole.wisepen.user.api.domain.base.UserDisplayBase;
import com.oriole.wisepen.user.api.domain.dto.req.WalletSettleCoinTradeRequest;
import com.oriole.wisepen.user.api.feign.RemoteUserService;
import com.oriole.wisepen.user.api.feign.RemoteWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements IMarketService {

    private final MarketListingRepository marketListingRepository;
    private final MarketPurchaseRepository marketPurchaseRepository;
    private final ResourceInteractionInfoRepository resourceInteractionInfoRepository;
    private final ResourceItemRepository resourceItemRepository;
    private final TagRepository tagRepository;
    private final MongoTemplate mongoTemplate;
    private final IResourceService resourceService;
    private final RemoteUserService remoteUserService;
    private final RemoteWalletService remoteWalletService;
    private final RemoteNoteService remoteNoteService;
    private final RemoteDocumentService remoteDocumentService;

    @Override
    public MarketListingResponse listResource(MarketListResourceRequest request, Long sellerId, Map<Long, GroupRoleType> groupRoles) {
        // 检验是否为资源拥有者
        String sellerIdStr = sellerId.toString();
        resourceService.assertResourceOwner(request.getResourceId(), sellerIdStr);
        ResourceItemEntity resource = resourceItemRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ServiceException(ResourceError.RESOURCE_NOT_FOUND));

        // 检验是否为集市组
        Long marketGroupId = Long.valueOf(request.getMarketGroupId());
        Map<Long, GroupDisplayBase> groupMap = remoteUserService.getGroupDisplayInfo(List.of(marketGroupId)).getData();
        GroupDisplayBase groupInfo = groupMap == null ? null : groupMap.get(marketGroupId);
        if (groupInfo == null || groupInfo.getGroupType() != GroupType.MARKET_GROUP) {
            throw new ServiceException(ResourceError.MARKET_GROUP_REQUIRED);
        }

        // 检验上架权限 or 公开？
        GroupRoleType marketRole = groupRoles == null ? null : groupRoles.get(marketGroupId);
        if (marketRole == null || marketRole == GroupRoleType.NOT_MEMBER) {
            throw new ServiceException(ResourceError.RESOURCE_PERMISSION_DENIED);
        }

        // 检验上架 Tag
        List<TagEntity> tags = tagRepository.findAllById(request.getTagIds());
        if (tags.size() != request.getTagIds().size()
                || tags.stream().anyMatch(tag -> !request.getMarketGroupId().equals(tag.getGroupId()))) {
            throw new ServiceException(ResourceError.TAG_NODE_NOT_FOUND);
        }

        // 检验上架 Version
        Long listedVersion = request.getListedVersion();
        // TODO: 文档版本管理
        if (resource.getResourceType() != ResourceType.NOTE && listedVersion != 0L) {
            throw new ServiceException(ResourceError.MARKET_VERSION_NOT_SUPPORTED);
        }

        // 检验是否已上架
        MarketListingEntity entity = marketListingRepository.findByMarketGroupIdAndSourceResourceId(request.getMarketGroupId(), request.getResourceId());
        if (entity != null && entity.getStatus() == MarketListingStatus.LISTED) {
            throw new ServiceException(ResourceError.MARKET_LISTING_ALREADY_EXISTS);
        }

        resourceService.updateGroupResourceTags(
                request.getResourceId(),
                request.getMarketGroupId(),
                sellerIdStr,
                marketRole,
                request.getTagIds()
        );
        if (entity != null) {
            entity.setStatus(MarketListingStatus.LISTED);
            entity.setRevision(entity.getRevision() == null ? 1 : entity.getRevision() + 1);
        } else {
            entity = MarketListingEntity.builder()
                    .sourceResourceId(resource.getResourceId())
                    .sellerId(sellerIdStr)
                    .marketGroupId(request.getMarketGroupId())
                    .tagIds(request.getTagIds())
                    .price(request.getPrice())
                    .listedVersion(listedVersion)
                    .status(MarketListingStatus.LISTED)
                    .revision(1)
                    .resourceName(resource.getResourceName())
                    .resourceType(resource.getResourceType())
                    .preview(resource.getPreview())
                    .size(resource.getSize())
                    .build();
        }
        MarketListingEntity saved = marketListingRepository.save(entity);
        log.info("market listing saved listingId={} sourceResourceId={} sellerId={} marketGroupId={} revision={}",
                saved.getListingId(), saved.getSourceResourceId(), sellerIdStr, saved.getMarketGroupId(), saved.getRevision());

        MarketListingResponse response = BeanUtil.copyProperties(saved, MarketListingResponse.class);
        Map<String, String> tagMap = new HashMap<>();
        if (saved.getTagIds() != null && !saved.getTagIds().isEmpty()) {
            tagRepository.findAllById(saved.getTagIds()).forEach(tag -> tagMap.put(tag.getTagId(), tag.getTagName()));
        }
        response.setCurrentTags(tagMap);
        ResourceInteractionInfoEntity interactionInfo = resourceInteractionInfoRepository.findById(saved.getSourceResourceId())
                .orElseGet(ResourceInteractionInfoEntity::new);
        response.setResourceInteractionInfo(interactionInfo);
        UserDisplayBase sellerInfo;
        try {
            Long seller = Long.valueOf(saved.getSellerId());
            sellerInfo = remoteUserService.getUserDisplayInfo(List.of(seller)).getData().get(seller);
        } catch (Exception e) {
            // Feign 调用失败
            log.debug("market seller info degraded sellerId={}", saved.getSellerId(), e);
            sellerInfo = new UserDisplayBase("UNKNOW", null, null, null);
        }
        response.setSellerInfo(sellerInfo);
        return response;
    }

    @Override
    public MarketListingResponse updateListingVersion(MarketUpdateListingVersionRequest request, Long sellerId) {
        MarketListingEntity entity = marketListingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ServiceException(ResourceError.MARKET_LISTING_NOT_FOUND));
        if (!sellerId.toString().equals(entity.getSellerId())) {
            throw new ServiceException(ResourceError.RESOURCE_PERMISSION_DENIED);
        }
        ResourceItemEntity resource = resourceItemRepository.findById(entity.getSourceResourceId())
                .orElseThrow(() -> new ServiceException(ResourceError.RESOURCE_NOT_FOUND));

        Long newVersion = request.getListedVersion() == null ? 0L : request.getListedVersion();
        // TODO: 文档版本管理（目前只支持Note版本，其余默认0）
        if (resource.getResourceType() != ResourceType.NOTE && newVersion != 0L) {
            throw new ServiceException(ResourceError.MARKET_VERSION_NOT_SUPPORTED);
        }

        if (!Objects.equals(entity.getListedVersion(), newVersion)) {
            entity.setListedVersion(newVersion);
            entity.setRevision(entity.getRevision() == null ? 1 : entity.getRevision() + 1);
            marketListingRepository.save(entity);
        }

        MarketListingResponse response = BeanUtil.copyProperties(entity, MarketListingResponse.class);
        Map<String, String> tagMap = new HashMap<>();
        if (entity.getTagIds() != null && !entity.getTagIds().isEmpty()) {
            tagRepository.findAllById(entity.getTagIds()).forEach(tag -> tagMap.put(tag.getTagId(), tag.getTagName()));
        }
        response.setCurrentTags(tagMap);
        ResourceInteractionInfoEntity interactionInfo = resourceInteractionInfoRepository.findById(entity.getSourceResourceId())
                .orElseGet(ResourceInteractionInfoEntity::new);
        response.setResourceInteractionInfo(interactionInfo);
        UserDisplayBase sellerInfo;
        try {
            Long seller = Long.valueOf(entity.getSellerId());
            sellerInfo = remoteUserService.getUserDisplayInfo(List.of(seller)).getData().get(seller);
        } catch (Exception e) {
            // Feign 调用失败
            log.debug("market seller info degraded sellerId={}", entity.getSellerId(), e);
            sellerInfo = new UserDisplayBase("UNKNOW", null, null, null);
        }
        response.setSellerInfo(sellerInfo);
        return response;
    }

    @Override
    public void offShelf(MarketOffShelfRequest request, Long operatorId, Map<Long, GroupRoleType> groupRoles) {
        MarketListingEntity entity = marketListingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ServiceException(ResourceError.MARKET_LISTING_NOT_FOUND));
        boolean isSeller = operatorId.toString().equals(entity.getSellerId());
        GroupRoleType marketRole = groupRoles == null ? null : groupRoles.get(Long.valueOf(entity.getMarketGroupId()));
        boolean isMarketAdmin = marketRole == GroupRoleType.OWNER || marketRole == GroupRoleType.ADMIN;
        if (!isSeller && !isMarketAdmin) {
            throw new ServiceException(ResourceError.RESOURCE_PERMISSION_DENIED);
        }

        resourceService.updateGroupResourceTags(
                entity.getSourceResourceId(),
                entity.getMarketGroupId(),
                operatorId.toString(),
                marketRole,
                null
        );
        entity.setStatus(MarketListingStatus.OFF_SHELF);
        entity.setRevision(entity.getRevision() == null ? 1 : entity.getRevision() + 1);
        marketListingRepository.save(entity);
        log.info("market listing offShelf listingId={} sourceResourceId={} operatorId={}",
                entity.getListingId(), entity.getSourceResourceId(), operatorId);
    }

    @Override
    public MarketPurchaseResponse purchase(MarketPurchaseRequest request, Long buyerId, Map<Long, GroupRoleType> groupRoles) {
        MarketListingEntity listing = marketListingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ServiceException(ResourceError.MARKET_LISTING_NOT_FOUND));
        if (listing.getStatus() != MarketListingStatus.LISTED) {
            throw new ServiceException(ResourceError.MARKET_LISTING_NOT_ACTIVE);
        }
        if (buyerId.toString().equals(listing.getSellerId())) {
            throw new ServiceException(ResourceError.MARKET_SELF_PURCHASE_NOT_ALLOWED);
        }

        // 检验权限 or 公开？
        GroupRoleType marketRole = groupRoles == null ? null : groupRoles.get(Long.valueOf(listing.getMarketGroupId()));
        if (marketRole == null || marketRole == GroupRoleType.NOT_MEMBER) {
            throw new ServiceException(ResourceError.RESOURCE_PERMISSION_DENIED);
        }
        ResourceCheckPermissionResDTO permission = resourceService.checkPermission(ResourceCheckPermissionReqDTO.builder()
                .resourceId(listing.getSourceResourceId())
                .userId(buyerId)
                .groupRoles(groupRoles)
                .build());
        boolean viewable = permission.getResourceAccessRole() == ResourceAccessRole.OWNER
                || permission.getAllowedActions() != null && permission.getAllowedActions().contains(ResourceAction.VIEW);
        if (!viewable) {
            throw new ServiceException(ResourceError.RESOURCE_PERMISSION_DENIED);
        }

        String traceId = "market:" + listing.getListingId() + ":" + buyerId + ":" + listing.getRevision();
        MarketPurchaseEntity existing = marketPurchaseRepository.findByTradeTraceId(traceId).orElse(null);
        if (existing != null) {
            return BeanUtil.copyProperties(existing, MarketPurchaseResponse.class);
        }

        remoteWalletService.settleCoinTrade(WalletSettleCoinTradeRequest.builder()
                .traceId(traceId)
                .buyerId(buyerId)
                .sellerId(Long.valueOf(listing.getSellerId()))
                .price(listing.getPrice())
                .meta("market listing " + listing.getListingId())
                .build());

        MarketPurchaseEntity purchase = MarketPurchaseEntity.builder()
                .listingId(listing.getListingId())
                .buyerId(buyerId.toString())
                .sellerId(listing.getSellerId())
                .sourceResourceId(listing.getSourceResourceId())
                .paidPrice(listing.getPrice())
                .forkedVersion(listing.getListedVersion() == null ? 0L : listing.getListedVersion())
                .listingRevision(listing.getRevision())
                .tradeTraceId(traceId)
                .resourceType(listing.getResourceType())
                .build();
        MarketPurchaseEntity saved = marketPurchaseRepository.save(purchase);
        log.info("market purchase saved purchaseId={} listingId={} buyerId={} revision={}",
                saved.getPurchaseId(), listing.getListingId(), buyerId, listing.getRevision());
        return BeanUtil.copyProperties(saved, MarketPurchaseResponse.class);
    }

    @Override
    public MarketPurchaseResponse fork(MarketForkRequest request, Long buyerId) {
        MarketPurchaseEntity purchase = marketPurchaseRepository.findById(request.getPurchaseId())
                .orElseThrow(() -> new ServiceException(ResourceError.MARKET_PURCHASE_NOT_FOUND));
        if (!buyerId.toString().equals(purchase.getBuyerId())) {
            throw new ServiceException(ResourceError.RESOURCE_PERMISSION_DENIED);
        }
        if (StringUtils.hasText(purchase.getForkedResourceId())) {
            return BeanUtil.copyProperties(purchase, MarketPurchaseResponse.class);
        }

        ResourceItemEntity source = resourceItemRepository.findById(purchase.getSourceResourceId())
                .orElseThrow(() -> new ServiceException(ResourceError.RESOURCE_NOT_FOUND));
        String forkedResourceId = resourceService.createResourceItem(ResourceCreateReqDTO.builder()
                .resourceName(source.getResourceName())
                .resourceType(source.getResourceType())
                .ownerId(buyerId.toString())
                .pathTagId(request.getPathTagId())
                .preview(source.getPreview())
                .size(source.getSize())
                .build());

        try {
            Long forkedVersion = purchase.getForkedVersion() == null ? 0L : purchase.getForkedVersion();
            if (source.getResourceType() == ResourceType.NOTE) {
                remoteNoteService.forkNote(NoteForkRequest.builder()
                        .sourceResourceId(source.getResourceId())
                        .targetResourceId(forkedResourceId)
                        .version(forkedVersion)
                        .buyerId(buyerId)
                        .build());
            } else if (Set.of(ResourceType.PDF, ResourceType.DOC, ResourceType.DOCX, ResourceType.PPT,
                    ResourceType.PPTX, ResourceType.XLS, ResourceType.XLSX).contains(source.getResourceType())) {
                remoteDocumentService.forkDocument(DocumentForkRequest.builder()
                        .sourceResourceId(source.getResourceId())
                        .targetResourceId(forkedResourceId)
                        .version(forkedVersion)
                        .buyerId(buyerId)
                        .build());
            } else {
                throw new ServiceException(ResourceError.MARKET_RESOURCE_TYPE_NOT_SUPPORTED);
            }
            purchase.setForkedResourceId(forkedResourceId);
            MarketPurchaseEntity saved = marketPurchaseRepository.save(purchase);
            log.info("market fork finished purchaseId={} sourceResourceId={} forkedResourceId={}",
                    saved.getPurchaseId(), saved.getSourceResourceId(), forkedResourceId);
            return BeanUtil.copyProperties(saved, MarketPurchaseResponse.class);
        } catch (Exception e) {
            resourceService.softRemoveResources(List.of(forkedResourceId));
            log.warn("market fork compensated purchaseId={} forkedResourceId={}", purchase.getPurchaseId(), forkedResourceId, e);
            throw e;
        }
    }

    @Override
    public PageR<MarketListingResponse> listMarketListings(String marketGroupId, List<String> tagIds, int page, int size,
                                                           MarketListingSortBy sortBy, SortDirectionEnum sortDir) {
        // 检验是否为集市组
        Long marketGroupIdLong = Long.valueOf(marketGroupId);
        Map<Long, GroupDisplayBase> groupMap = remoteUserService.getGroupDisplayInfo(List.of(marketGroupIdLong)).getData();
        GroupDisplayBase groupInfo = groupMap == null ? null : groupMap.get(marketGroupIdLong);
        if (groupInfo == null || groupInfo.getGroupType() != GroupType.MARKET_GROUP) {
            throw new ServiceException(ResourceError.MARKET_GROUP_REQUIRED);
        }

        List<MarketListingEntity> listings;
        long total;
        Sort sort = Sort.by(sortDir.toSpringDirection(), sortBy.getDbField());
        // 排序：点赞、时间……
        if (sortBy.isInteractionField()) {
            Criteria criteria = Criteria.where("marketGroupId").is(marketGroupId)
                    .and("status").is(MarketListingStatus.LISTED);
            if (tagIds != null && !tagIds.isEmpty()) {
                criteria.and("tagIds").in(tagIds);
            }
            List<AggregationOperation> operations = new ArrayList<>();
            operations.add(Aggregation.match(criteria));
            operations.add(Aggregation.lookup(
                    "wisepen_resource_interact_info",
                    "sourceResourceId",
                    "resourceId",
                    "resourceInteractionInfo"
            ));
            operations.add(Aggregation.unwind("resourceInteractionInfo", true));
            operations.add(Aggregation.sort(sort));
            operations.add(Aggregation.skip((long) Math.max(page - 1, 0) * size));
            operations.add(Aggregation.limit(size));
            listings = mongoTemplate.aggregate(
                    Aggregation.newAggregation(operations),
                    "wisepen_market_listings",
                    MarketListingEntity.class
            ).getMappedResults();
            total = mongoTemplate.count(Query.query(criteria), MarketListingEntity.class);
        } else {
            Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, sort);
            Page<MarketListingEntity> entityPage = tagIds == null || tagIds.isEmpty()
                    ? marketListingRepository.findByMarketGroupIdAndStatus(marketGroupId, MarketListingStatus.LISTED, pageable)
                    : marketListingRepository.findByMarketGroupIdAndStatusAndTagIdsIn(
                            marketGroupId, MarketListingStatus.LISTED, tagIds, pageable);
            listings = entityPage.getContent();
            total = entityPage.getTotalElements();
        }

        Set<String> allTagIds = new HashSet<>();
        Set<Long> sellerIds = new HashSet<>();
        List<String> sourceResourceIds = listings.stream()
                .map(MarketListingEntity::getSourceResourceId)
                .toList();
        listings.forEach(entity -> {
            if (entity.getTagIds() != null) {
                allTagIds.addAll(entity.getTagIds());
            }
            try {
                sellerIds.add(Long.valueOf(entity.getSellerId()));
            } catch (NumberFormatException e) {
                log.debug("market seller id invalid sellerId={}", entity.getSellerId(), e);
            }
        });

        Map<String, ResourceInteractionInfoEntity> interactionMap = sourceResourceIds.isEmpty()
                ? Collections.emptyMap()
                : resourceInteractionInfoRepository.findByResourceIdIn(sourceResourceIds).stream()
                .collect(Collectors.toMap(ResourceInteractionInfoEntity::getResourceId, entity -> entity));

        Map<String, String> tagNameMap = new HashMap<>();
        if (!allTagIds.isEmpty()) {
            tagRepository.findAllById(allTagIds).forEach(tag -> tagNameMap.put(tag.getTagId(), tag.getTagName()));
        }

        Map<Long, UserDisplayBase> sellerInfoMap = Collections.emptyMap();
        if (!sellerIds.isEmpty()) {
            try {
                Map<Long, UserDisplayBase> remoteMap = remoteUserService.getUserDisplayInfo(sellerIds.stream().toList()).getData();
                sellerInfoMap = remoteMap == null ? Collections.emptyMap() : remoteMap;
            } catch (Exception e) {
                log.debug("market seller info degraded sellerIds={}", sellerIds, e);
            }
        }

        Map<Long, UserDisplayBase> finalSellerInfoMap = sellerInfoMap;
        PageR<MarketListingResponse> pageR = new PageR<>(total, page, size);
        pageR.addAll(listings.stream()
                .map(entity -> {
                    MarketListingResponse response = BeanUtil.copyProperties(entity, MarketListingResponse.class);
                    Map<String, String> tagMap = new HashMap<>();
                    if (entity.getTagIds() != null) {
                        entity.getTagIds().forEach(tagId -> tagMap.put(tagId, tagNameMap.get(tagId)));
                    }
                    response.setCurrentTags(tagMap);
                    response.setResourceInteractionInfo(
                            interactionMap.getOrDefault(entity.getSourceResourceId(), new ResourceInteractionInfoEntity()));
                    try {
                        response.setSellerInfo(finalSellerInfoMap.get(Long.valueOf(entity.getSellerId())));
                    } catch (NumberFormatException e) {
                        log.debug("market seller id invalid sellerId={}", entity.getSellerId(), e);
                    }
                    return response;
                })
                .toList());
        return pageR;
    }

    @Override
    public PageR<MarketListingResponse> listMyListings(String sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<MarketListingEntity> entityPage = marketListingRepository.findBySellerId(sellerId, pageable);
        List<String> sourceResourceIds = entityPage.getContent().stream()
                .map(MarketListingEntity::getSourceResourceId)
                .toList();
        Map<String, ResourceInteractionInfoEntity> interactionMap = sourceResourceIds.isEmpty()
                ? Collections.emptyMap()
                : resourceInteractionInfoRepository.findByResourceIdIn(sourceResourceIds).stream()
                .collect(Collectors.toMap(ResourceInteractionInfoEntity::getResourceId, entity -> entity));
        PageR<MarketListingResponse> pageR = new PageR<>(entityPage.getTotalElements(), page, size);
        pageR.addAll(entityPage.getContent().stream()
                .map(entity -> {
                    MarketListingResponse response = BeanUtil.copyProperties(entity, MarketListingResponse.class);
                    Map<String, String> tagMap = new HashMap<>();
                    if (entity.getTagIds() != null && !entity.getTagIds().isEmpty()) {
                        tagRepository.findAllById(entity.getTagIds()).forEach(tag -> tagMap.put(tag.getTagId(), tag.getTagName()));
                    }
                    response.setCurrentTags(tagMap);
                    response.setResourceInteractionInfo(
                            interactionMap.getOrDefault(entity.getSourceResourceId(), new ResourceInteractionInfoEntity()));
                    try {
                        Long seller = Long.valueOf(entity.getSellerId());
                        UserDisplayBase sellerInfo = remoteUserService.getUserDisplayInfo(List.of(seller)).getData().get(seller);
                        response.setSellerInfo(sellerInfo);
                    } catch (Exception e) {
                        log.debug("market seller info degraded sellerId={}", entity.getSellerId(), e);
                    }
                    return response;
                })
                .toList());
        return pageR;
    }

    @Override
    public PageR<MarketPurchaseResponse> listMyPurchases(String buyerId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<MarketPurchaseEntity> entityPage = marketPurchaseRepository.findByBuyerId(buyerId, pageable);
        PageR<MarketPurchaseResponse> pageR = new PageR<>(entityPage.getTotalElements(), page, size);
        pageR.addAll(entityPage.getContent().stream()
                .map(entity -> BeanUtil.copyProperties(entity, MarketPurchaseResponse.class))
                .toList());
        return pageR;
    }

}
