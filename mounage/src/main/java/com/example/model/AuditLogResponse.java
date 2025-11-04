package com.example.model;

import java.time.Instant;
import java.util.Map;

public class    AuditLogResponse {
    private Integer id;
    private String tableName;
    private Integer recordId;
    private String action;
    private Map<String, Object> oldValues;
    private Map<String, Object> newValues;
    private UserInfo user;
    private String ipAddress;
    private Instant createdAt;

    public static class UserInfo {
        private Integer id;
        private String username;
        private String fullName;

        public UserInfo(Integer id, String username, String fullName) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
        }
        public Integer getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Map<String, Object> getOldValues() { return oldValues; }
    public void setOldValues(Map<String, Object> oldValues) { this.oldValues = oldValues; }
    public Map<String, Object> getNewValues() { return newValues; }
    public void setNewValues(Map<String, Object> newValues) { this.newValues = newValues; }
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
} 