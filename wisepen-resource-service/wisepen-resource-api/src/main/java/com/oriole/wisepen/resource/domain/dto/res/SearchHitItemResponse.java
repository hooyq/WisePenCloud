package com.oriole.wisepen.resource.domain.dto.res;

import com.oriole.wisepen.resource.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHitItemResponse {
    private String resourceId;
    private ResourceType resourceType;
    private String resourceName;
    private String highlightContent;
    private LocalDateTime updateTime;
}
