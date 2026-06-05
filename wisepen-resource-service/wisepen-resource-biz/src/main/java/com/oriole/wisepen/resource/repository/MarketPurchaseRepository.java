package com.oriole.wisepen.resource.repository;

import com.oriole.wisepen.resource.domain.entity.MarketPurchaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketPurchaseRepository extends MongoRepository<MarketPurchaseEntity, String> {

    Optional<MarketPurchaseEntity> findByTradeTraceId(String tradeTraceId);

    Page<MarketPurchaseEntity> findByBuyerId(String buyerId, Pageable pageable);
}
