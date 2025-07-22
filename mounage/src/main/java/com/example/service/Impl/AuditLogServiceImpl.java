package com.example.service.Impl;

import com.example.entity.AuditLog;
import com.example.model.AuditLogResponse;
import com.example.repository.AuditLogRepository;
import com.example.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AuditLogResponse toResponse(AuditLog log) {
        AuditLogResponse res = new AuditLogResponse();
        res.setId(log.getId());
        res.setTableName(log.getTableName());
        res.setRecordId(log.getRecordId());
        res.setAction(log.getAction());
        res.setOldValues(log.getOldValues());
        res.setNewValues(log.getNewValues());
        if (log.getUser() != null) {
            res.setUser(new AuditLogResponse.UserInfo(
                log.getUser().getId(),
                log.getUser().getUsername(),
                log.getUser().getFullName()
            ));
        }
        res.setIpAddress(log.getIpAddress());
        res.setCreatedAt(log.getCreatedAt());
        return res;
    }
} 