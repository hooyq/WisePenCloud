package com.oriole.wisepen.resource.domain.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceForkRequest {
    @NotBlank
    private String sourceResourceId;

    @NotBlank
    private String targetResourceId;

    @NotNull
    @Min(0)
    private Long version;

    @NotNull
    private Long buyerId;
}
