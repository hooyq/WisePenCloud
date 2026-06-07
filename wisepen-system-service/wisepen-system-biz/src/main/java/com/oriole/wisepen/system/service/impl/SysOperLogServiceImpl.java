package com.oriole.wisepen.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oriole.wisepen.common.core.domain.PageR;
import com.oriole.wisepen.system.api.domain.dto.SysOperLogDTO;
import com.oriole.wisepen.system.api.domain.dto.SysOperLogQueryRequest;
import com.oriole.wisepen.system.api.domain.dto.SysOperLogQueryResponse;
import com.oriole.wisepen.system.domain.entity.SysOperLogEntity;
import com.oriole.wisepen.system.mapper.SysOperLogMapper;
import com.oriole.wisepen.system.service.SysOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    @Autowired
    private SysOperLogMapper sysOperLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveLog(SysOperLogDTO dto) {
        SysOperLogEntity entity = BeanUtil.copyProperties(dto, SysOperLogEntity.class);
        int rows = sysOperLogMapper.insert(entity);
        return rows > 0;
    }

    @Override
    public PageR<SysOperLogQueryResponse> listLogs(SysOperLogQueryRequest query) {
        int page = query.getPage();
        int size = query.getSize();
        LambdaQueryWrapper<SysOperLogEntity> queryWrapper = Wrappers.<SysOperLogEntity>lambdaQuery()
                .in(query.getOperUrls() != null && !query.getOperUrls().isEmpty(),
                        SysOperLogEntity::getOperUrl, query.getOperUrls())
                .eq(query.getOperUserId() != null,
                        SysOperLogEntity::getOperUserId, query.getOperUserId())
                .ge(query.getStartTime() != null,
                        SysOperLogEntity::getOperTime, query.getStartTime())
                .le(query.getEndTime() != null,
                        SysOperLogEntity::getOperTime, query.getEndTime())
                .eq(query.getStatus() != null,
                        SysOperLogEntity::getStatus, query.getStatus())
                .orderByDesc(SysOperLogEntity::getOperTime);
        IPage<SysOperLogEntity> result = sysOperLogMapper.selectPage(new Page<>(page, size), queryWrapper);

        PageR<SysOperLogQueryResponse> pageR = new PageR<>(result.getTotal(), page, size);
        List<SysOperLogQueryResponse> list = result.getRecords().stream()
                .map(entity -> BeanUtil.copyProperties(entity, SysOperLogQueryResponse.class))
                .toList();
        pageR.addAll(list);

        return pageR;
    }
}
