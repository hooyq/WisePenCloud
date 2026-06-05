package com.oriole.wisepen.resource.domain.base;

import com.oriole.wisepen.resource.enums.MarketListingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MarketListingBase extends ResourceItemInfoBase {
    @Id
    private String listingId;
    private String sourceResourceId;
    private String sellerId;
    private String marketGroupId;
    private List<String> tagIds;
    private Integer price;
    private Long listedVersion;
    private MarketListingStatus status;
    private Integer revision;
    @CreatedDate
    private LocalDateTime createTime;
    @LastModifiedDate
    private LocalDateTime updateTime;
}
