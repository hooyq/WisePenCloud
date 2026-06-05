package com.oriole.wisepen.resource.repository;

import com.oriole.wisepen.resource.domain.entity.MarketListingEntity;
import com.oriole.wisepen.resource.enums.MarketListingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketListingRepository extends MongoRepository<MarketListingEntity, String> {

    MarketListingEntity findByMarketGroupIdAndSourceResourceId(String marketGroupId, String sourceResourceId);

    Page<MarketListingEntity> findByMarketGroupIdAndStatus(
            String marketGroupId, MarketListingStatus status, Pageable pageable);

    Page<MarketListingEntity> findByMarketGroupIdAndStatusAndTagIdsIn(
            String marketGroupId, MarketListingStatus status, List<String> tagIds, Pageable pageable);

    Page<MarketListingEntity> findBySellerId(String sellerId, Pageable pageable);
}
