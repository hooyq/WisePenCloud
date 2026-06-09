package com.oriole.wisepen.resource.domain.mq;

import com.oriole.wisepen.resource.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceForkMessage {
    private String forkTaskId;

    private String sourceResourceId;

    private ResourceType resourceType;

    private Long version;

    private Long buyerId;

    private String resourceName;

    private String preview;

    private Long size;
}
