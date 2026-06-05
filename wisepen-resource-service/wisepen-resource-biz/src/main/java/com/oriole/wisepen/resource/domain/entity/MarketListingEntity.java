package com.oriole.wisepen.resource.domain.entity;

import com.oriole.wisepen.resource.domain.base.MarketListingBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@Document(collection = "wisepen_market_listings")
@CompoundIndex(name = "idx_market_source", def = "{'marketGroupId': 1, 'sourceResourceId': 1}", unique = true)
public class MarketListingEntity extends MarketListingBase {
}
