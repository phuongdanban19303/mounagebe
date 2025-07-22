package com.example.service.Impl;

import com.example.entity.AuditLog;
import com.example.entity.user.User;
import com.example.repository.AuditLogRepository;
import com.example.repository.UserRepository;
import com.example.service.Auditable;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Pointcut("@annotation(com.example.service.Auditable)")
    public void auditableMethods() {}

    @AfterReturning(pointcut = "auditableMethods()", returning = "result")
    public void audit(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Auditable auditable = method.getAnnotation(Auditable.class);

            // 1. Get current user safely
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                // Handle cases where there is no authenticated user
                return;
            }
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username);


            // 2. Get IP address
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ipAddress = request.getRemoteAddr();

            // 3. Prepare audit data
            AuditLog auditLog = new AuditLog();
            auditLog.setUser(currentUser);
            auditLog.setTableName(auditable.tableName());
            auditLog.setAction(auditable.action());
            auditLog.setIpAddress(ipAddress);
            auditLog.setCreatedAt(Instant.now());

            Map<String, Object> oldValues = new HashMap<>();
            Map<String, Object> newValues = new HashMap<>();

            Object[] args = joinPoint.getArgs();

            if (auditable.action().equalsIgnoreCase("CREATE")) {
                auditLog.setRecordId(getRecordId(result));
                newValues = objectMapper.convertValue(result, Map.class);
            } else if (auditable.action().equalsIgnoreCase("UPDATE")) {
                // For UPDATE, we assume the first argument is the ID and the second is the request object
                auditLog.setRecordId((Integer) args[0]);
                // To get oldValues, you would typically fetch the entity before the update.
                // This is a simplified example. A more robust solution might involve another annotation
                // or fetching the entity from the DB.
                newValues = objectMapper.convertValue(result, Map.class); // result is the updated entity
            } else if (auditable.action().equalsIgnoreCase("DELETE")) {
                // For DELETE, the first argument is the ID
                auditLog.setRecordId((Integer) args[0]);
                // The 'result' for a delete operation is often a boolean or void.
                // 'oldValues' would require fetching the entity before deletion.
            }

            auditLog.setNewValues(newValues);
            auditLog.setOldValues(oldValues); // As noted, oldValues is simplified here.

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Log the error but don't let it crash the main operation
            System.err.println("Error while auditing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Integer getRecordId(Object result) {
        try {
            Method getIdMethod = result.getClass().getMethod("getId");
            return (Integer) getIdMethod.invoke(result);
        } catch (Exception e) {
            // Fallback for objects that don't have a standard getId()
            return null;
        }
    }
} 