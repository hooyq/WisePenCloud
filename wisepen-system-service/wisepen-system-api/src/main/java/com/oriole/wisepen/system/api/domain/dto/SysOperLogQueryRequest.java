package com.oriole.wisepen.system.api.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SysOperLogQueryRequest {
    private List<String> operUrls;
    private Long operUserId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private int page;
    private int size;
}
