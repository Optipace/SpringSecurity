package com.example.user_service.client;

import com.example.user_service.dto.AuditRequest;
import org.hibernate.audit.AuditLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="audit-service")
public interface AuditClient {
    @PostMapping("/audit/save")
    void save(@RequestBody AuditRequest auditRequest);
}
