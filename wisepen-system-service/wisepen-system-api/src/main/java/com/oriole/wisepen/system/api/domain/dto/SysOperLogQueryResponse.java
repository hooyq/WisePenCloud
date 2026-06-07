package com.oriole.wisepen.system.api.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * System operation log query response.
 */
@Data
public class SysOperLogQueryResponse implements Serializable {
    private String title;
    private Integer businessType;
    private Long operUserId;
    private String operUrl;
    private String operIp;
    private String operParam;
    private String jsonResult;
    private Integer status;
    private String errorMsg;
    private LocalDateTime operTime;
}
