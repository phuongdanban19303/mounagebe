package com.example.service;

import com.example.model.AuditLogResponse;
import java.util.List;

public interface AuditLogService {
    List<AuditLogResponse> getAllLogs();
} 